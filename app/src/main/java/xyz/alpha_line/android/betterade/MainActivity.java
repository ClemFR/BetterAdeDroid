package xyz.alpha_line.android.betterade;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import xyz.alpha_line.android.betterade.containers.DayViewFactory;
import xyz.alpha_line.android.betterade.util.InstallNotifier;
import xyz.alphaline.mintimetablenew.MinTimeTableView;
import xyz.alphaline.mintimetablenew.model.ScheduleEntity;

import com.google.android.material.navigation.NavigationView;
import com.kizitonwose.calendar.core.WeekDay;
import com.kizitonwose.calendar.view.WeekCalendarView;
import com.kizitonwose.calendar.view.WeekDayBinder;

import xyz.alpha_line.android.betterade.BuildConfig;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kotlin.Unit;
import xyz.alpha_line.android.betterade.api.BetterAdeApi;
import xyz.alpha_line.android.betterade.containers.DayViewContainer;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
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

        setupToolbar();

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

        ((TextView) findViewById(R.id.dateText)).setText(LocalDate.now().format(DateTimeFormatter.ofPattern("E dd MMM yyyy", Locale.FRANCE)));

        initCalendar();

        // On regarde si on a dans les préferences une date sélectionnée et une liste de promos sélectionnées
        // Si oui, on les récupère
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);

        promosRecherche = prefs.getString(PREFERENCES_PROMOS, "");
        typeRecherche = prefs.getInt(PREFERENCE_TYPE_RECHERCHE, -1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // lance automatiquement la recherche si on a déjà des promos sélectionnées
        if (typeRecherche != -1) {
            if (hasFocus && firstFocus) {
                Log.i("MainActivity", "onWindowFocusChanged: Update TimeTable");
                initTimeTable();
                updateTimeTable();
                firstFocus = false;
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
                    Log.i("MainActivity", "startListenerCallbackActiviteFille: " + result.getResultCode());
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        if (typeRecherche == -1) {
                            Log.i("MainActivity", "startListenerCallbackActiviteFille: Lancement ItemSelector (typeRecherche == -1, resultCode != OK)");
                            lancerItemSelector();
                        }
                    }

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        typeRecherche = data.getIntExtra(ItemSelector.INTENT_TYPE_RESSOURCE, -1);
                        promosRecherche = data.getStringExtra(ItemSelector.INTENT_LISTE_RESSOURCE);

                        if (typeRecherche == -1) {
                            Log.i("MainActivity", "startListenerCallbackActiviteFille: Lancement ItemSelector (typeRecherche == -1, resultCode == OK)");
                            lancerItemSelector();
                            return;
                        }

                        // On sauvegarde les préférences
                        Log.i("MainActivity", "startListenerCallbackActiviteFille: Sauvegarde des préférences");
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

    public void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        System.out.println("Item selected : " + item.getTitle());
        if (item.getItemId() == R.id.nav_rech_rapide) {
            Intent intent = new Intent(this, QuickSearch.class);
            startActivity(intent);
            drawer.closeDrawers();
            return true;
        } else if (item.getItemId() == R.id.nav_modif_edt) {
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
            drawer.closeDrawers();
            return true;
        } else if (item.getItemId() == R.id.nav_about) {
            // Show popup
            showAboutPopup();
            drawer.closeDrawers();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutPopup() {
        AlertDialog.Builder popup_builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.popup_about, null);
        TextView appver = customLayout.findViewById(R.id.app_version);
        appver.setText("Version : " + BuildConfig.VERSION_NAME);
        TextView buildver = customLayout.findViewById(R.id.build_ver);
        buildver.setText("BuildVer : " + BuildConfig.VERSION_CODE);


        popup_builder.setView(customLayout);
        popup_builder.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        popup_builder.create().show();
    }
}