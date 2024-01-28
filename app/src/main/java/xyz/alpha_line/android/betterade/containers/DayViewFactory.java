package xyz.alpha_line.android.betterade.containers;

import android.view.View;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DayViewFactory {

    public LocalDate selectedDate = LocalDate.now();
    public View lastSelectedView = null;
    private Runnable updateUi;

    public DayViewFactory(Runnable updateUi) {
        this.updateUi = updateUi;
    }

    public DayViewContainer createContainer(View v) {
        return new DayViewContainer(v, this);
    }

    public void updateTimeTable() {
        updateUi.run();
    }

    public Date getSelectedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
