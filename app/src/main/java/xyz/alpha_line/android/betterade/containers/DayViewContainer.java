package xyz.alpha_line.android.betterade.containers;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kizitonwose.calendar.view.ViewContainer;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import xyz.alpha_line.android.betterade.MainActivity;
import xyz.alpha_line.android.betterade.R;

public class DayViewContainer extends ViewContainer {

    public View view;
    public TextView dayText;
    public LocalDate day;

    private static LocalDate selected_date = LocalDate.now();
    private static View last_selected_view;

    public DayViewContainer(View view) {
        super(view);
        dayText = view.findViewById(R.id.calendarDayText);
        this.view = view;
    }

    public void updateUi() {

        // On mets le jour en gras et couleur primaire si c'est aujourd'hui
        if (day.equals(LocalDate.now())) {
            dayText.setTypeface(null, Typeface.BOLD);
            dayText.setTextColor(view.getResources().getColor(R.color.colorPrimary));
        } else {
            dayText.setTypeface(null, Typeface.NORMAL);
            dayText.setTextColor(view.getResources().getColor(R.color.black));
        }

        // On sélectionne le jour en faisant attention de ne pas sélectionner qu'avec la vue car elles sont réutilisés
        view.setSelected(day.equals(selected_date));
        if (day.equals(selected_date)) {
            if (last_selected_view != null) {
                last_selected_view.setSelected(false);
            }
            last_selected_view = view;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DayViewContainer", "onClick: " + dayText.getText());
                Log.i("DayViewContainer", "onClick: " + day.toString());

                // on mets a jour la date sélectionnée
                selected_date = day;
                if (last_selected_view != null) {
                    last_selected_view.setSelected(false);
                }
                last_selected_view = view;
                v.setSelected(true);

                // On envoi a la mainactivity le signal pour qu'elle mette a jour la liste des cours
                MainActivity.instance.updateTimeTable();
            }
        });
    }

    public static Date getSelectedDate() {
        Calendar c = Calendar.getInstance();
        c.set(selected_date.getYear(), selected_date.getMonthValue() - 1, selected_date.getDayOfMonth());
        return c.getTime();
    }

}
