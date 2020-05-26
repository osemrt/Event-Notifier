package ce.yildiz.edu.tr.calendar.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.adapters.EventAdapter;
import ce.yildiz.edu.tr.calendar.adapters.GridAdapter;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.models.RecurringPattern;

public class CalendarFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final int ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE = 0;
    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    public static final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private List<Date> dates = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private ImageButton previousMonthImageButton, nextMonthImageButton;
    private TextView currentDateTextView;
    private GridView datesGridView;

    // AlertDialog components
    public RecyclerView savedEventsRecyclerView;
    private Button addNewEventButton;
    private TextView noEventTextView;

    private AlertDialog alertDialog;

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        dbHelper = new DBHelper(getActivity());

        defineViews(view);
        defineListeners();
        setUpCalendar();

        return view;
    }

    private void defineViews(View view) {
        nextMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Next);
        previousMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Prev);
        currentDateTextView = view.findViewById(R.id.CalenderFragment_TextView_CurrentDate);
        datesGridView = view.findViewById(R.id.CalenderFragment_GridView_Dates);
    }

    private void defineListeners() {
        nextMonthImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });

        previousMonthImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
            }
        });

        datesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Avoid clicking on non-activate dates
                Date viewDate = dates.get(position);
                Calendar viewCalendar = calendar.getInstance();
                viewCalendar.setTime(viewDate);
                if (viewCalendar.get(Calendar.YEAR) != calendar.get(Calendar.YEAR) || viewCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    return;
                }

                // Show events alert dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                View dialogView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.layout_alert_dialog, parent, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setUpCalendar();

                    }
                });

                savedEventsRecyclerView = (RecyclerView) dialogView.findViewById(R.id.AlertDialog_RecyclerView_ListEvents);
                addNewEventButton = (Button) dialogView.findViewById(R.id.AlertDialog_Button_AddEvent);
                noEventTextView = (TextView) dialogView.findViewById(R.id.AlertDialog_TextView_NoEvent);


                final String date = Utils.eventDateFormat.format(dates.get(position));
                final List<Event> eventsByDate = collectEventsByDate(dates.get(position));

                if (eventsByDate.isEmpty()) {
                    savedEventsRecyclerView.setVisibility(View.INVISIBLE);
                    noEventTextView.setVisibility(View.VISIBLE);
                    addNewEventButton.setText("CREATE EVENT");
                } else {
                    savedEventsRecyclerView.setVisibility(View.VISIBLE);
                    noEventTextView.setVisibility(View.GONE);
                    savedEventsRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                    savedEventsRecyclerView.setLayoutManager(layoutManager);
                    final EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate, alertDialog, CalendarFragment.this);
                    savedEventsRecyclerView.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                    addNewEventButton.setText("ADD EVENT");
                }


                addNewEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), NewEventActivity.class);
                        intent.putExtra("date", date);
                        startActivityForResult(intent, ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE);
                        alertDialog.dismiss();
                    }
                });
            }
        });

    }

    public void setUpCalendar() {
        String dateString = Utils.dateFormat.format(calendar.getTime());
        currentDateTextView.setText(dateString);

        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1); // start from Monday

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        collectEventsByMonth(Utils.yearFormat.format(calendar.getTime()), Utils.monthFormat.format(calendar.getTime()));

        while (dates.size() < Utils.MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        GridAdapter gridAdapter = new GridAdapter(getContext(), dates, calendar, events);
        datesGridView.setAdapter(gridAdapter);

    }

    private void collectEventsByMonth(String year, String month) {
        events.clear();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventsByMonth(sqLiteDatabase, year, month);
        while (cursor.moveToNext()) {
            events.add(dbHelper.readEvent(sqLiteDatabase, cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID))));
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    private List<Event> collectEventsByDate(Date date) {
        List<Event> eventList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Add recurring events
        Event mEvent = new Event();
        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case Utils.DAILY:
                    mEvent = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                    mEvent.setDate(Utils.eventDateFormat.format(date));
                    eventList.add(mEvent);
                    break;
                case Utils.WEEKLY:
                    if (dayOfWeek == recurringPattern.getDayOfWeek()) {
                        mEvent = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        mEvent.setDate(Utils.eventDateFormat.format(date));
                        eventList.add(mEvent);
                    }
                    break;
                case Utils.MONTHLY:
                    if (dayOfMonth == recurringPattern.getDayOfMonth()) {
                        mEvent = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        mEvent.setDate(Utils.eventDateFormat.format(date));
                        eventList.add(mEvent);
                    }
                    break;
                case Utils.YEARLY:
                    if (month == recurringPattern.getMonthOfYear() && dayOfMonth == recurringPattern.getDayOfMonth()) {
                        mEvent = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        mEvent.setDate(Utils.eventDateFormat.format(date));
                        eventList.add(mEvent);
                    }
                    break;
            }
        }


        // Add non-recurring events
        Cursor cursor = dbHelper.readEventsByDate(sqLiteDatabase, Utils.eventDateFormat.format(date));
        while (cursor.moveToNext()) {
            int eventID = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID));
            if (!isContains(eventList, eventID)) {
                eventList.add(dbHelper.readEvent(sqLiteDatabase, eventID));
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return eventList;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                setUpCalendar();
                Toast.makeText(getActivity(), "Event created!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EDIT_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                setUpCalendar();
                Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        }
    }
}
