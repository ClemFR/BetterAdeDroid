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

import xyz.alpha_line.android.betterade.util.InstallNotifier;
import xyz.alphaline.mintimetablenew.BaseTimeTable;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthText = findViewById(R.id.monthText);
        calendarView = findViewById(R.id.calendarView);
        timetable = findViewById(R.id.table);

        listeCours = new ArrayList<>();

        // Métriques de nouvelle installation
        InstallNotifier.detectNewInstall(this);

        // Init système pour recevoir les intents de l'activité ItemSelector
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

        // On initialise le bouton flottant qui lance l'activité ItemSelector
        findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        });

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
        initTimeTable();
        if (typeRecherche != -1) {
            updateTimeTable();
        } else {
            // On lance l'activité ItemSelector pour que l'utilisateur choisisse une promo
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        }
    }

    private void initTimeTable() {
        timetable.initTable(new String[]{""});
    }

    public void initCalendar() {

        WeekDayBinder<DayViewContainer> dayBinder = new WeekDayBinder<DayViewContainer>() {
            @Override
            public void bind(@NonNull DayViewContainer dayView, WeekDay weekDay) {
                dayView.day = weekDay.getDate();
                dayView.dayText.setText(String.valueOf(weekDay.getDate().getDayOfMonth()));
                Log.i("MainActivity", "bind: " + dayView.day.toString());
                dayView.updateUi();
            }

            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view, MainActivity.this);
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

        Log.i("MainActivity", "updateTimeTable: " + DayViewContainer.getSelectedDate().toString());
        Log.i("MainActivity", "updateTimeTable: " + promosRecherche);
        Log.i("MainActivity", "updateTimeTable: " + typeRecherche);

        if (typeRecherche == -1) {
            return;
        }

        BetterAdeApi.recupereAfficheCoursDay(
                promosRecherche,
                DayViewContainer.getSelectedDate(),
                listeCours,
                timetable,
                typeRecherche,
                this);
    }

    public void lancerItemSelector() {
        Intent intent = new Intent(this, ItemSelector.class);
        startActivity(intent);
    }
}