package ce.yildiz.edu.tr.calendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.models.Event;

public class GridAdapter extends ArrayAdapter {
    private List<Date> dates;
    private Calendar currentDate;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Event> events) {
        super(context, R.layout.layout_cell);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date date = dates.get(position);
        Calendar dateCalender = Calendar.getInstance();
        dateCalender.setTime(date);

        int dayNo = dateCalender.get(Calendar.DAY_OF_MONTH);

        int displayMonth = dateCalender.get(Calendar.MONTH);
        int displayYear = dateCalender.get(Calendar.YEAR);

        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentYear = currentDate.get(Calendar.YEAR);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_cell, parent, false);
            dayTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_Day);
            eventCountTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

            dayTextView.setText(String.valueOf(dayNo));
        }

        TextView day = convertView.findViewById(R.id.LayoutCell_TextView_Day);
        TextView eventCount = convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

        if (displayMonth == currentMonth && displayYear == currentYear) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.lightIndigo));
            day.setTextColor(getContext().getResources().getColor(R.color.black));
        } else {
            day.setTextColor(getContext().getResources().getColor(R.color.lightGrey));
        }

        ArrayList<String> strings = new ArrayList<>();
        Calendar aCalendar = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            aCalendar.setTime(convertStringToDate(events.get(i).getDate()));
            if (dayNo == aCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == aCalendar.get(Calendar.MONTH) && displayYear == aCalendar.get(Calendar.YEAR)) {
                strings.add(events.get(i).getTitle());
            }
        }

        if (strings.size() > 0) {
            eventCount.setText(Integer.toString(strings.size()));
        }


        return convertView;
    }

    private Date convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aDate;
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
