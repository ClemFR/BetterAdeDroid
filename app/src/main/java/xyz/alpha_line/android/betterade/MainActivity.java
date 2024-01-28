package xyz.alpha_line.android.betterade;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import xyz.alpha_line.android.betterade.containers.DayViewFactory;
import xyz.alpha_line.android.betterade.util.InstallNotifier;
import xyz.alphaline.mintimetablenew.MinTimeTableView;
import xyz.alphaline.mintimetablenew.model.ScheduleEntity;
import com.kizitonwose.calendar.core.WeekDay;
import com.kizitonwose.calendar.view.WeekCalendarView;
import com.kizitonwose.calendar.view.WeekDayBinder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kotlin.Unit;
import xyz.alpha_line.android.betterade.api.BetterAdeApi;
import xyz.alpha_line.android.betterade.containers.DayViewContainer;

public class MainActivity extends AppCompatActivity {


    private TextView monthText;
    private WeekCalendarView calendarView;
    private MinTimeTableView timetable;
    private ArrayList<ScheduleEntity> listeCours;
    private ActivityResultLauncher<Intent> itemSelectorResultLauncher;

    private String promosRecherche = "";
    private int typeRecherche = -1;

    public static String PREFERENCES_PROMOS = "promos";
    public static String PREFERENCE_TYPE_RECHERCHE = "type";

    private Date lastSelectedDate = null;
    private boolean firstFocus = true;
    private boolean onCreate = false;
    private DayViewFactory dayViewFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity", "onCreate: ");
        onCreate = true;

        monthText = findViewById(R.id.monthText);
        calendarView = findViewById(R.id.calendarView);
        timetable = findViewById(R.id.table);


        if (savedInstanceState != null) {
            lastSelectedDate = (Date) savedInstanceState.getSerializable("lastSelectedDate");
            listeCours = (ArrayList<ScheduleEntity>) savedInstanceState.getSerializable("listeCours");

            // Supression vue timetable + réinitialisation
            timetable = new MinTimeTableView(this);
            timetable = findViewById(R.id.table);

            initTimeTable();
            Log.i("MainActivity", "onCreate: Taille liste cours : " + listeCours.size());

            // Fix dégeu : Lancement thread poure re-remplir la timetable avec les cours récupérés
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    MainActivity.this.runOnUiThread(() -> timetable.updateSchedules(listeCours));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            listeCours = new ArrayList<>();

            // Métriques de nouvelle installation
            InstallNotifier.detectNewInstall(this);
        }

        // Init système pour recevoir les intents de l'activité ItemSelector
        startListenerCallbackActiviteFille();

        // On initialise le bouton flottant qui lance l'activité ItemSelector
        findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        });

        ((TextView) findViewById(R.id.dateText)).setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));

        initCalendar();

        // On regarde si on a dans les préferences une date sélectionnée et une liste de promos sélectionnées
        // Si oui, on les récupère
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);

        promosRecherche = prefs.getString(PREFERENCES_PROMOS, "");
        typeRecherche = prefs.getInt(PREFERENCE_TYPE_RECHERCHE, -1);

        findViewById(R.id.fab_rech_rapide).setOnClickListener(v -> {
            Intent intent = new Intent(this, QuickSearch.class);
            startActivity(intent);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // lance automatiquement la recherche si on a déjà des promos sélectionnées
        if (typeRecherche != -1) {
            if (hasFocus && firstFocus) {
                initTimeTable();
                updateTimeTable();
            }
        } else {
            // On lance l'activité ItemSelector pour que l'utilisateur choisisse une promo
            firstFocus = true;
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        }
    }

    private void initTimeTable() {
        timetable.initTable(new String[]{""});
    }

    public void initCalendar() {

        // On initialise le calendrier
        dayViewFactory = new DayViewFactory(this::updateTimeTable);

        WeekDayBinder<DayViewContainer> dayBinder = new WeekDayBinder<DayViewContainer>() {
            @Override
            public void bind(@NonNull DayViewContainer dayView, WeekDay weekDay) {
                dayView.day = weekDay.getDate();
                dayView.dayText.setText(String.valueOf(weekDay.getDate().getDayOfMonth()));
                dayView.updateUi();
            }

            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return dayViewFactory.createContainer(view);
            }
        };

        calendarView.setDayBinder(dayBinder);

        // Set up the calendar
        Calendar calendar = Calendar.getInstance();

        // Initialisation du calendrier de JUILLET à JUILLET
        int annee = calendar.get(Calendar.YEAR);
        int mois = calendar.get(Calendar.MONTH);
        if (mois <= 6) {
            annee--;
        }

        LocalDate start = LocalDate.of(annee, 7, 1);
        LocalDate end = LocalDate.of(annee + 1, 7, 31);

        calendarView.setup(start, end, DayOfWeek.MONDAY);
        calendarView.scrollToWeek(LocalDate.now());

        // On sélectionne le jour actuel
        calendarView.scrollToDate(LocalDate.now());


        // On détecte quand on scroll sur le calendrier
        calendarView.setWeekScrollListener(
                week -> {
                    String textMonth = "";
                    String temp;

                    // On regarde si la semaine est à cheval sur 2 mois
                    if (week.getDays().get(0).getDate().getMonth() != week.getDays().get(6).getDate().getMonth()) {
                        temp = week.getDays().get(0).getDate().format(DateTimeFormatter.ofPattern("MMMM"));
                        textMonth += temp.substring(0, 1).toUpperCase() + temp.substring(1); // On met la première lettre en majuscule
                        textMonth += " - ";
                        temp = week.getDays().get(6).getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                        textMonth += temp.substring(0, 1).toUpperCase() + temp.substring(1); // On met la première lettre en majuscule
                    } else {
                        temp = week.getDays().get(0).getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                        textMonth += temp.substring(0, 1).toUpperCase() + temp.substring(1); // On met la première lettre en majuscule
                    }

                    monthText.setText(textMonth);
                    return Unit.INSTANCE;
                });
    }

    public void updateTimeTable() {

        // On regarde si la date sélectionnée est la même que la dernière fois
        if (lastSelectedDate != null && lastSelectedDate.equals(dayViewFactory.getSelectedDate())) {
            return;
        }

        Log.i("MainActivity", "updateTimeTable: last selected : " + lastSelectedDate);
        Log.i("MainActivity", "updateTimeTable: selected      : " + dayViewFactory.getSelectedDate());
        lastSelectedDate = dayViewFactory.getSelectedDate();
        Log.i("MainActivity", "updateTimeTable: " + promosRecherche);
        Log.i("MainActivity", "updateTimeTable: " + typeRecherche);

        if (typeRecherche == -1) {
            return;
        }

        BetterAdeApi.recupereAfficheCoursDay(
                promosRecherche,
                dayViewFactory.getSelectedDate(),
                listeCours,
                timetable,
                typeRecherche,
                this);
    }

    public void lancerItemSelector() {
        Intent intent = new Intent(this, ItemSelector.class);
        startActivity(intent);
    }

    private void startListenerCallbackActiviteFille() {
        itemSelectorResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        if (typeRecherche == -1) {
                            lancerItemSelector();
                        }
                    }

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        typeRecherche = data.getIntExtra(ItemSelector.INTENT_TYPE_RESSOURCE, -1);
                        promosRecherche = data.getStringExtra(ItemSelector.INTENT_LISTE_RESSOURCE);

                        if (typeRecherche == -1) {
                            lancerItemSelector();
                            return;
                        }

                        // On sauvegarde les préférences
                        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(PREFERENCES_PROMOS, promosRecherche);
                        editor.putInt(PREFERENCE_TYPE_RECHERCHE, typeRecherche);
                        editor.apply();

                        updateTimeTable();
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putSerializable("lastSelectedDate", lastSelectedDate);
            // Serialisation de la liste de cours
            outState.putSerializable("listeCours", listeCours);

        } catch (Exception e) {
            Log.e("MainActivity", "onSaveInstanceState: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listeCours != null && listeCours.size() > 0) {
            // Si on peut récupérer la liste de cours, on la met à jour
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    MainActivity.this.runOnUiThread(() -> timetable.updateSchedules(listeCours));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}