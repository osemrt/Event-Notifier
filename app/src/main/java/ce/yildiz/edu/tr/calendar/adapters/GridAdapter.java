package ce.yildiz.edu.tr.calendar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private List<Date> dates;
    private Calendar selectedCalendar;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar selectedCalendar, List<Event> events) {
        super(context, R.layout.layout_cell);

        this.dates = dates;
        this.selectedCalendar = selectedCalendar;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
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
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.lightIndigo));
            dayTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        } else {
            dayTextView.setTextColor(getContext().getResources().getColor(R.color.lightGrey));
        }

        if (displayYear == currentYear && displayMonth == currentMonth && displayDay == currentDay) {
            if (displayYear == selectedYear && displayMonth == selectedMonth) {
                bgLinearLayout.setBackgroundColor(getContext().getResources().getColor(R.color.Indigo));
                dayTextView.setTextColor(getContext().getResources().getColor(R.color.white));
                eventCountTextView.setTextColor(getContext().getResources().getColor(R.color.white));
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
}
