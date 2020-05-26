package ce.yildiz.edu.tr.calendar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.models.RecurringPattern;

public class GridAdapter extends ArrayAdapter {

    private final String DAILY = "Repeat Daily";
    private final String WEEKLY = "Repeat Weekly";
    private final String MONTHLY = "Repeat Monthly";
    private final String YEARLY = "Repeat Yearly";

    private Utils.AppTheme appTheme;

    private List<Date> dates;
    private Calendar selectedCalendar;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;
    private ArrayList<Integer> colors;
    private DBHelper dbHelper;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar selectedCalendar, List<Event> events) {
        super(context, R.layout.layout_cell);

        this.dates = dates;
        this.selectedCalendar = selectedCalendar;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
        dbHelper = new DBHelper(context);

        this.appTheme = getAppTheme();
        colors = getColors();
    }


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date viewDate = dates.get(position);
        Calendar viewCalendar = Calendar.getInstance();
        viewCalendar.setTime(viewDate);

        int viewMonth = viewCalendar.get(Calendar.MONTH);
        int viewYear = viewCalendar.get(Calendar.YEAR);
        int viewDayOfMonth = viewCalendar.get(Calendar.DAY_OF_MONTH);
        int viewDayOfWeek = viewCalendar.get(Calendar.DAY_OF_WEEK);

        int selectedMonth = selectedCalendar.get(Calendar.MONTH);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_cell, parent, false);
            dayTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_Day);
            eventCountTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

            dayTextView.setText(String.valueOf(viewDayOfMonth));
        }

        TextView dayTextView = convertView.findViewById(R.id.LayoutCell_TextView_Day);
        TextView eventCountTextView = convertView.findViewById(R.id.LayoutCell_TextView_EventCount);
        LinearLayout bgLinearLayout = convertView.findViewById(R.id.LayoutCell_LinearLayout);


        if (viewYear == selectedYear && viewMonth == selectedMonth) {
            // Active dates
            convertView.setBackgroundColor(getContext().getResources().getColor(colors.get(2)));
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(3)));
            eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(6)));

        } else {
            // Inactive dates
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(1)));
            eventCountTextView.setVisibility(View.GONE);
        }

        // Highlight current day on the calendar
        Calendar mCalendar = Calendar.getInstance();
        if (viewYear == mCalendar.get(Calendar.YEAR) && viewMonth == mCalendar.get(Calendar.MONTH) && viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH)) {
            bgLinearLayout.setBackgroundColor(getContext().getResources().getColor(colors.get(4)));
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));
            eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));
        }


        // Find event count
        List<Integer> eventIDs = new ArrayList<>();
        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case DAILY:
                    eventIDs.add(recurringPattern.getEventId());
                    break;
                case WEEKLY:
                    if (viewDayOfWeek == recurringPattern.getDayOfWeek()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case MONTHLY:
                    if (viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case YEARLY:
                    if (viewMonth == recurringPattern.getMonthOfYear() && viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
            }
        }

        mCalendar = Calendar.getInstance();
        for (Event event : events) {
            if(event.getDate()!=null){
                mCalendar.setTime(Utils.convertStringToDate(event.getDate()));
                if (viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH) && viewMonth == mCalendar.get(Calendar.MONTH) && viewYear == mCalendar.get(Calendar.YEAR) && !eventIDs.contains(event.getId())) {
                    eventIDs.add(event.getId());
                }
            }

        }

        if (eventIDs.size() > 0) {
            eventCountTextView.setText(Integer.toString(eventIDs.size()));
        }

        return convertView;
    }

    private List<RecurringPattern> readRecurringPatterns() {
        List<RecurringPattern> recurringPatterns = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readAllRecurringPatterns(sqLiteDatabase);
        while (cursor.moveToNext()) {
            RecurringPattern recurringPattern = new RecurringPattern();
            recurringPattern.setEventId(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_EVENT_ID)));
            recurringPattern.setPattern(cursor.getString(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_TYPE)));
            recurringPattern.setMonthOfYear(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_MONTH_OF_YEAR)));
            recurringPattern.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_DAY_OF_MONTH)));
            recurringPattern.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_DAY_OF_WEEK)));
            recurringPatterns.add(recurringPattern);
        }
        return recurringPatterns;
    }

    private boolean isContains(List<Event> events, int eventId) {
        for (Event event : events) {
            if (event.getId() == eventId) {
                return true;
            }
        }
        return false;
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
                colors.add(R.color.Grey800); // active date backgroundColor
                colors.add(R.color.white); // active date textColor
                colors.add(R.color.black); // current date backgroundColor
                colors.add(R.color.white); // current date textColor
                colors.add(R.color.white); // event count textColor
                break;

        }
        return colors;
    }
}
