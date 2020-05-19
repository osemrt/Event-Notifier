package ce.yildiz.edu.tr.calendar.views;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.UniversalTimeScale;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.adapters.UpcomingEventAdapter;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;

public class UpcomingEventsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private ImageButton changePeriodImageButton;
    private TextView periodTextView;
    private RecyclerView eventsRecyclerView;

    private DBHelper dbHelper;

    private Utils.Period period;
    private String todayDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        dbHelper = new DBHelper(getActivity());
        period = Utils.Period.TODAY;

        defineViews(view);
        initViews();
        defineListeners();

        return view;
    }


    private void defineViews(View view) {
        changePeriodImageButton = (ImageButton) view.findViewById(R.id.UpcomingEventsFragment_ImageButton_Period);
        periodTextView = (TextView) view.findViewById(R.id.UpcomingEventsFragment_TextView_Period);
        eventsRecyclerView = (RecyclerView) view.findViewById(R.id.UpcomingEventsFragment_RecyclerView_Events);
    }

    private void initViews() {
        periodTextView.setText("Today");
        setUpRecyclerView();
    }

    private void defineListeners() {
        changePeriodImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate menu
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_period, popup.getMenu());
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
                popup.show();

                setUpRecyclerView();
            }

            class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.PopupPeriod_Item_Today:
                            periodTextView.setText("Today");
                            period = Utils.Period.TODAY;
                            break;
                        case R.id.PopupPeriod_Item_Next7Days:
                            periodTextView.setText("Next 7 days");
                            period = Utils.Period.NEXT_7_DAYS;
                            break;
                        case R.id.PopupPeriod_Item_Next30Days:
                            periodTextView.setText("Next 30 days");
                            period = Utils.Period.NEXT_30_DAYS;
                            break;
                        case R.id.PopupPeriod_Item_AllEvents:
                            periodTextView.setText("All events");
                            period = Utils.Period.ALL_EVENTS;
                            break;
                    }

                    setUpRecyclerView();

                    return true;
                }
            }


        });
    }


    public void setUpRecyclerView(){
        todayDate = Utils.eventDateFormat.format(Calendar.getInstance().getTime());
        eventsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventsRecyclerView.setLayoutManager(layoutManager);
        UpcomingEventAdapter upcomingEventAdapter = new UpcomingEventAdapter(getActivity(), collectEvents(todayDate), this);
        eventsRecyclerView.setAdapter(upcomingEventAdapter);
        upcomingEventAdapter.notifyDataSetChanged();

    }


    private List<Event> collectEvents(String today) {
        List<Event> events = null;
        try {
            switch (period) {
                case TODAY:
                    events = collectTodayEvents(today);
                    break;
                case NEXT_7_DAYS:
                    events = collectNext7DaysEvents(today);
                    break;
                case NEXT_30_DAYS:
                    events = collectNext30DaysEvents(today);
                    break;
                case ALL_EVENTS:
                    events = collectAllEvents(today);
                    break;
            }
        } catch (ParseException e) {
            Log.e(TAG, "An error has occurred while parsing the date string");
        }

        return events;
    }

    private List<Event> collectTodayEvents(String today) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventsByDate(sqLiteDatabase, today);
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID)));
            event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
            event.setAllDay(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY))));
            event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
            event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
            event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
            event.setDuration(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DURATION)));
            event.setNotify(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY))));
            event.setRepetition(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_REPETITION)));
            event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
            event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
            event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
            events.add(event);
        }
        cursor.close();
        sqLiteDatabase.close();

        return events;
    }

    private List<Event> collectNext7DaysEvents(String today) throws ParseException {

        Date fromDate = Utils.eventDateFormat.parse(today);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, 8);
        Date toDate = calendar.getTime();

        List<Event> events = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readAllEvents(sqLiteDatabase);
        while (cursor.moveToNext()) {
            Date currentDate = Utils.eventDateFormat.parse(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            if (currentDate.after(fromDate) && currentDate.before(toDate)) {
                Event event = new Event();
                event.setId(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
                event.setAllDay(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY))));
                event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
                event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
                event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
                event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
                event.setDuration(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DURATION)));
                event.setNotify(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY))));
                event.setRepetition(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_REPETITION)));
                event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
                event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
                event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
                event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
                event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
            }
        }
        cursor.close();
        sqLiteDatabase.close();

        return events;
    }

    private List<Event> collectNext30DaysEvents(String today) throws ParseException {

        Date fromDate = Utils.eventDateFormat.parse(today);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, 31);
        Date toDate = calendar.getTime();

        List<Event> events = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readAllEvents(sqLiteDatabase);
        while (cursor.moveToNext()) {
            Date currentDate = Utils.eventDateFormat.parse(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            if (currentDate.after(fromDate) && currentDate.before(toDate)) {
                Event event = new Event();
                event.setId(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
                event.setAllDay(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY))));
                event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
                event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
                event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
                event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
                event.setDuration(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DURATION)));
                event.setNotify(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY))));
                event.setRepetition(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_REPETITION)));
                event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
                event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
                event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
                event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
                event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
                events.add(event);
            }
        }
        cursor.close();
        sqLiteDatabase.close();

        return events;
    }

    private List<Event> collectAllEvents(String today) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readAllEvents(sqLiteDatabase);
        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID)));
            event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
            event.setAllDay(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY))));
            event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
            event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
            event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
            event.setDuration(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DURATION)));
            event.setNotify(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY))));
            event.setRepetition(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_REPETITION)));
            event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
            event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
            event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
            events.add(event);
        }
        cursor.close();
        sqLiteDatabase.close();

        return events;
    }


}
