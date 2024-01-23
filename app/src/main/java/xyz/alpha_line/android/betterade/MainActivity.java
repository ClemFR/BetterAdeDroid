package xyz.alpha_line.android.betterade;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import java.util.List;

import kotlin.Unit;
import xyz.alpha_line.android.betterade.api.BetterAdeApi;
import xyz.alpha_line.android.betterade.containers.DayViewContainer;
import xyz.alpha_line.android.betterade.recyclerview.CoursAdapter;
import xyz.alpha_line.android.betterade.recyclerview.CoursInfos;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private TextView monthText;
    private WeekCalendarView calendarView;

    public RecyclerView rv;
    public List<CoursInfos> coursListe;

    private String promosRecherche = "";
    private int typeRecherche = -1;

    public static String PREFERENCES_PROMOS = "promos";
    public static String PREFERENCE_TYPE_RECHERCHE = "type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        monthText = findViewById(R.id.monthText);
        calendarView = findViewById(R.id.calendarView);

        // Init système pour recevoir les intents de l'activité ItemSelector
        ActivityResultLauncher<Intent> itemSelectorResultLauncher = registerForActivityResult(
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

                    updateRecyclerView();
                }
            }
        );

        // On initialise le bouton flottant qui lance l'activité ItemSelector
        findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        });

        initCalendar();
        setupRecyclerView();

        // On regarde si on a dans les préferences une date sélectionnée et une liste de promos sélectionnées
        // Si oui, on les récupère
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);

        promosRecherche = prefs.getString(PREFERENCES_PROMOS, "");
        typeRecherche = prefs.getInt(PREFERENCE_TYPE_RECHERCHE, -1);

        if (typeRecherche != -1) {
            updateRecyclerView();
        } else {
            // On lance l'activité ItemSelector pour que l'utilisateur choisisse une promo
            Intent intent = new Intent(this, ItemSelector.class);
            itemSelectorResultLauncher.launch(intent);
        }
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
                return new DayViewContainer(view);
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

        calendarView.setup(start, end, DayOfWeek.SUNDAY);
        calendarView.scrollToWeek(LocalDate.now());

        // On sélectionne le jour actuel
        calendarView.scrollToDate(LocalDate.now());


        // On détecte quand on scroll sur le calendrier
        calendarView.setWeekScrollListener(
                week -> {
                    monthText.setText(week.getDays().get(0).getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
                    return Unit.INSTANCE;
                });
    }

    public void setupRecyclerView() {
        coursListe = new ArrayList<>();

        rv = (RecyclerView) findViewById(R.id.recycler_view);
        CoursAdapter adapter = new CoursAdapter(coursListe);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    public void updateRecyclerView() {

        Log.i("MainActivity", "updateRecyclerView: " + DayViewContainer.getSelectedDate().toString());
        Log.i("MainActivity", "updateRecyclerView: " + promosRecherche);
        Log.i("MainActivity", "updateRecyclerView: " + typeRecherche);

        if (typeRecherche == -1) {
            return;
        }

        BetterAdeApi.recupereAfficheCoursDay(
                promosRecherche,
                DayViewContainer.getSelectedDate(),
                coursListe,
                rv,
                typeRecherche);
    }

    public void lancerItemSelector() {
        Intent intent = new Intent(this, ItemSelector.class);
        startActivity(intent);
    }
}