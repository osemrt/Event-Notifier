package ce.yildiz.edu.tr.calendar.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

public class CalendarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private static final int ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE = 0;
    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    public static final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

    private List<Date> dates = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    private Context context;
    private ImageButton previousMonthImageButton, nextMonthImageButton;
    private TextView currentDateTextView;
    private GridView datesGridView;

    // AlertDialog components
    private RecyclerView savedEventsRecyclerView;
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
        setUpCalendar();

        return view;
    }

    private void defineViews(View view) {
        nextMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Next);
        previousMonthImageButton = view.findViewById(R.id.CalenderFragment_Button_Prev);
        currentDateTextView = view.findViewById(R.id.CalenderFragment_TextView_CurrentDate);
        datesGridView = view.findViewById(R.id.CalenderFragment_GridView_Dates);

        nextMonthImageButton.setOnClickListener(this);
        previousMonthImageButton.setOnClickListener(this);
        datesGridView.setOnItemClickListener(this);
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
        String dateString = Utils.dateFormat.format(calendar.getTime());
        currentDateTextView.setText(dateString);

        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 2;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        collectEventsPerMonth(Utils.monthFormat.format(calendar.getTime()), Utils.yearFormat.format(calendar.getTime()));

        while (dates.size() < Utils.MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        GridAdapter gridAdapter = new GridAdapter(getActivity().getBaseContext(), dates, calendar, events);
        datesGridView.setAdapter(gridAdapter);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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

        List<Event> eventsByDate = collectEventsByDate(date);

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
            EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate, alertDialog, this);
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

    private void collectEventsPerMonth(String Month, String Year) {
        events.clear();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventsByMonth(sqLiteDatabase, Year, Month);
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
        dbHelper.close();
    }

    private List<Event> collectEventsByDate(String date) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventsByDate(sqLiteDatabase, date);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(), "Event created!", Toast.LENGTH_SHORT).show();
                setUpCalendar();
            }
        } else if (requestCode == EDIT_EVENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(), "Event edited!", Toast.LENGTH_SHORT).show();
                setUpCalendar();
                alertDialog.dismiss();
            }
        }
    }
}
