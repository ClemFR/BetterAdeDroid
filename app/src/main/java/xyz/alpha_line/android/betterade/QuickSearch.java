package xyz.alpha_line.android.betterade;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import xyz.alphaline.mintimetablenew.MinTimeTableView;
import xyz.alphaline.mintimetablenew.model.ScheduleEntity;

public class QuickSearch extends AppCompatActivity {

    private MinTimeTableView timetable;
    private ArrayList<ScheduleEntity> listeCours;
    private ActivityResultLauncher<Intent> itemSelectorResultLauncher;
    private WeekCalendarView calendarView;
    private int typeRecherche = -1;
    private String promosRecherche = "";
    private TextView monthText;
    private Date lastSelectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.text_welcome)).setText("Recherche rapide");

        // Init timetable
        timetable = findViewById(R.id.table);
        timetable.initTable(new String[]{""});

        // Init calendar
        calendarView = findViewById(R.id.calendarView);
        monthText = findViewById(R.id.monthText);
        initCalendar();

        // Init système pour recevoir les intents de l'activité ItemSelector
        startListenerCallbackActiviteFille();

        // On initialise le bouton flottant qui lance l'activité ItemSelector
        findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        });

        findViewById(R.id.fab_rech_rapide).setVisibility(View.GONE);

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

                        updateTimeTable();
                    }
                }
        );
    }

    public void updateTimeTable() {

        // On regarde si la date sélectionnée est la même que la dernière fois
        if (lastSelectedDate != null && lastSelectedDate.equals(DayViewContainer.getSelectedDate())) {
            return;
        }

        Log.i("MainActivity", "updateTimeTable: last selected : " + lastSelectedDate);
        Log.i("MainActivity", "updateTimeTable: selected      : " + DayViewContainer.getSelectedDate());
        lastSelectedDate = DayViewContainer.getSelectedDate();
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

    public void initCalendar() {

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
                return new DayViewContainer(view, QuickSearch.this);
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
}