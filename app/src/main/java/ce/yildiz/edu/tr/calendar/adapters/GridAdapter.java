package ce.yildiz.edu.tr.calendar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.models.Event;

public class GridAdapter extends ArrayAdapter {

    private Utils.AppTheme appTheme;

    private List<Date> dates;
    private Calendar selectedCalendar;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;
    private ArrayList<Integer> colors;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar selectedCalendar, List<Event> events) {
        super(context, R.layout.layout_cell);


        this.dates = dates;
        this.selectedCalendar = selectedCalendar;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);

        this.appTheme = getAppTheme();
        colors = getColors();
    }


    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date date = dates.get(position);
        Calendar dateCalender = Calendar.getInstance();

        int currentMonth = dateCalender.get(Calendar.MONTH);
        int currentYear = dateCalender.get(Calendar.YEAR);
        int currentDay = dateCalender.get(Calendar.DAY_OF_MONTH);

        dateCalender.setTime(date);

        int dayNo = dateCalender.get(Calendar.DAY_OF_MONTH);

        int displayMonth = dateCalender.get(Calendar.MONTH);
        int displayYear = dateCalender.get(Calendar.YEAR);
        int displayDay = dateCalender.get(Calendar.DAY_OF_MONTH);

        int selectedMonth = selectedCalendar.get(Calendar.MONTH);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_cell, parent, false);
            dayTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_Day);
            eventCountTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

            dayTextView.setText(String.valueOf(dayNo));
        }

        TextView dayTextView = convertView.findViewById(R.id.LayoutCell_TextView_Day);
        TextView eventCountTextView = convertView.findViewById(R.id.LayoutCell_TextView_EventCount);
        LinearLayout bgLinearLayout = convertView.findViewById(R.id.LayoutCell_LinearLayout);

        if (displayYear == selectedYear && displayMonth == selectedMonth) {
            convertView.setBackgroundColor(getContext().getResources().getColor(colors.get(2)));
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(3)));
            eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(6)));

        } else {
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(1)));

        }

        if (displayYear == currentYear && displayMonth == currentMonth && displayDay == currentDay) {
            if (displayYear == selectedYear && displayMonth == selectedMonth) {

                bgLinearLayout.setBackgroundColor(getContext().getResources().getColor(colors.get(4)));
                dayTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));
                eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));


            }
        }

        ArrayList<String> strings = new ArrayList<>();
        Calendar aCalendar = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            aCalendar.setTime(Utils.convertStringToDate(events.get(i).getDate()));
            if (dayNo == aCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == aCalendar.get(Calendar.MONTH) && displayYear == aCalendar.get(Calendar.YEAR)) {
                strings.add(events.get(i).getTitle());
            }
        }

        if (strings.size() > 0) {
            eventCountTextView.setText(Integer.toString(strings.size()));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    private Utils.AppTheme getAppTheme() {
        Utils.AppTheme theme = Utils.AppTheme.INDIGO;
        switch (getString()) {
            case "Dark":
                theme = Utils.AppTheme.DARK;
                break;
            case "Indigo":
                theme = Utils.AppTheme.INDIGO;
                break;
        }
        return theme;
    }

    private String getString() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString("theme", "Indigo");
    }

    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        switch (appTheme) {
            case INDIGO:
                colors.add(R.color.white); // disabled date backgroundColor
                colors.add(R.color.lightGrey); // disabled date textColor
                colors.add(R.color.lightIndigo); // active date backgroundColor
                colors.add(R.color.darkIndigo); // active date textColor
                colors.add(R.color.darkIndigo); // current date backgroundColor
                colors.add(R.color.white); // current date textColor
                colors.add(R.color.darkIndigo); // event count textColor
                break;
            case DARK:
                colors.add(R.color.darkGrey); // disabled date backgroundColor
                colors.add(R.color.lightGrey2); // disabled date textColor
                colors.add(R.color.darkGrey2); // active date backgroundColor
                colors.add(R.color.white); // active date textColor
                colors.add(R.color.black); // current date backgroundColor
                colors.add(R.color.white); // current date textColor
                colors.add(R.color.white); // event count textColor
                break;

        }
        return colors;
    }
}
