package ce.yildiz.edu.tr.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private static final int MAX_CALENDAR_DAYS = 42;

    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    private SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    private List<Date> dates = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private Context context;
    private ImageButton prevButton, nextButton;
    private TextView currentDate;
    private GridView gridView;

    private DBOpenHelper dbOpenHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initializeViews(view);
        setUpCalendar();

        return view;
    }

    private void initializeViews(View view) {

        nextButton = view.findViewById(R.id.CalenderFragment_Button_Next);
        prevButton = view.findViewById(R.id.CalenderFragment_Button_Prev);
        currentDate = view.findViewById(R.id.CalenderFragment_TextView_CurrentDate);
        gridView = view.findViewById(R.id.CalenderFragment_GridView_Dates);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        gridView.setOnItemClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.CalenderFragment_Button_Prev:
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
                break;
            case R.id.CalenderFragment_Button_Next:
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
                break;

        }

    }

    public void setUpCalendar() {
        String dateString = dateFormat.format(calendar.getTime());
        currentDate.setText(dateString);

        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        collectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        GridAdapter gridAdapter = new GridAdapter(getActivity().getApplicationContext(), dates, calendar, events);
        gridView.setAdapter(gridAdapter);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }

    private void collectEventsPerMonth(String Month, String Year) {
        events.clear();
        dbOpenHelper = new DBOpenHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEventsPerMonth(Month, Year, sqLiteDatabase);
        while (cursor.moveToNext()) {
            String eventTitle = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));

            events.add(new Event(eventTitle, time, date, month, year));

        }

        cursor.close();
        dbOpenHelper.close();

    }

}
