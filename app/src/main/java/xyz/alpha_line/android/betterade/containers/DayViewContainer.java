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
import xyz.alpha_line.android.betterade.QuickSearch;
import xyz.alpha_line.android.betterade.R;

public class DayViewContainer extends ViewContainer {

    public View view;
    public TextView dayText;
    public LocalDate day;
    public DayViewFactory factory;

    public DayViewContainer(View view, DayViewFactory factory) {
        super(view);
        dayText = view.findViewById(R.id.calendarDayText);
        this.view = view;
        this.factory = factory;
    }

    private LocalDate getSelectedDate() {
        return factory.selectedDate;
    }

    private View getLastSelectedView() {
        return factory.lastSelectedView;
    }

    private void setSelectedDate(LocalDate date) {
        factory.selectedDate = date;
    }

    private void setLastSelectedView(View view) {
        factory.lastSelectedView = view;
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
        view.setSelected(day.equals(getSelectedDate()));
        if (day.equals(getSelectedDate())) {
            if (getLastSelectedView() != null) {
                getLastSelectedView().setSelected(false);
            }
            setLastSelectedView(view);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DayViewContainer", "onClick: " + dayText.getText());
                Log.i("DayViewContainer", "onClick: " + day.toString());

                // on mets a jour la date sélectionnée
                setSelectedDate(day);
                if (getLastSelectedView() != null) {
                    getLastSelectedView().setSelected(false);
                }
                setLastSelectedView(view);
                v.setSelected(true);

                // On envoi a la mainactivity le signal pour qu'elle mette a jour la liste des cours
                factory.updateTimeTable();
            }
        });
    }

}
