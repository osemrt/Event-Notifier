package ce.yildiz.edu.tr.calendar.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.other.AlarmReceiver;
import petrov.kristiyan.colorpicker.ColorPicker;

public class EditEventActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private TextInputLayout eventTitleTextInputLayout;
    private Switch allDayEventSwitch;
    private LinearLayout setDateLinearLayout;
    private TextView setDateTextView;
    private LinearLayout setTimeLinearLayout;
    private TextView setTimeTextView;
    private TextView notificationTextView;
    private Switch notifySwitch;
    private TextInputLayout eventNoteTextInputLayout;
    private TextView pickNoteColorTextView;
    private TextInputLayout eventLocationTextInputLayout;
    private TextInputLayout phoneNumberTextInputLayout;
    private TextInputLayout mailTextInputLayout;
    private TextInputEditText mailTextInputEditText;
    private Switch mailSwitch;

    private DBHelper dbHelper;

    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private int notColor;

    private String oldEventTitle;
    private String oldEventDate;
    private String oldEventTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        dbHelper = new DBHelper(this);

        defineViews();
        initViews();
        defineListeners();

        setSupportActionBar(toolbar);
    }

    private void defineViews() {
        eventTitleTextInputLayout = (TextInputLayout) findViewById(R.id.EditEventActivity_TextInputLayout_EventTitle);
        allDayEventSwitch = (Switch) findViewById(R.id.EditEventActivity_Switch_AllDayEvent);
        setDateLinearLayout = (LinearLayout) findViewById(R.id.EditEventActivity_LinearLayout_SetDate);
        setDateTextView = (TextView) findViewById(R.id.EditEventActivity_TexView_SetDate);
        setTimeLinearLayout = (LinearLayout) findViewById(R.id.EditEventActivity_LinearLayout_SetTime);
        setTimeTextView = (TextView) findViewById(R.id.EditEventActivity_TexView_SetTime);
        notificationTextView = (TextView) findViewById(R.id.EditEventActivity_TextView_Notification);
        notifySwitch = (Switch) findViewById(R.id.EditEventActivity_Switch_Notification);
        eventNoteTextInputLayout = (TextInputLayout) findViewById(R.id.EditEventActivity_TextInputLayout_Note);
        pickNoteColorTextView = (TextView) findViewById(R.id.EditEventActivity_TextView_PickNoteColor);
        eventLocationTextInputLayout = (TextInputLayout) findViewById(R.id.EditEventActivity_TextInputLayout_Location);
        phoneNumberTextInputLayout = (TextInputLayout) findViewById(R.id.EditEventActivity_TextInputLayout_PhoneNumber);
        mailTextInputLayout = (TextInputLayout) findViewById(R.id.EditEventActivity_TextInputLayout_Mail);
        mailTextInputEditText = (TextInputEditText) findViewById(R.id.EditEventActivity_TextInputEditText_Mail);
        mailSwitch = (Switch) findViewById(R.id.EditEventActivity_Switch_Mail);

        progressBar = (ProgressBar) findViewById(R.id.EditEventActivity_ProgressBar);
        toolbar = (Toolbar) findViewById(R.id.EditEventActivity_Toolbar);
    }

    private void initViews() {
        Intent intent = getIntent();
        oldEventTitle = intent.getStringExtra("eventTitle");
        oldEventDate = intent.getStringExtra("eventDate");
        oldEventTime = intent.getStringExtra("eventTime");

        Event event = readEvent(oldEventTitle, oldEventDate, oldEventTime);

        eventTitleTextInputLayout.getEditText().setText(event.getTitle());

        setDateTextView.setText(event.getDate());

        if (event.isAllDay()) {
            allDayEventSwitch.setChecked(true);
            setTimeLinearLayout.setVisibility(View.GONE);

        } else {
            allDayEventSwitch.setChecked(false);
            setTimeTextView.setText(event.getTime());
        }

        if (event.isNotify()) {
            notifySwitch.setChecked(true);
            notificationTextView.setText("At the time of event");
            notificationTextView.setEnabled(true);
        } else {
            notifySwitch.setChecked(false);
            notificationTextView.setText("Don't notify me");
            notificationTextView.setEnabled(false);
        }

        eventNoteTextInputLayout.getEditText().setText(event.getNote());

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
        bgShape.setColor(event.getColor());

        eventLocationTextInputLayout.getEditText().setText(event.getLocation());

        phoneNumberTextInputLayout.getEditText().setText(event.getPhoneNumber());

        if (event.getEmail() == null | "".equals(event.getEmail())) {
            mailSwitch.setChecked(false);
            mailTextInputEditText.setText("");
            mailTextInputEditText.setEnabled(false);
            mailTextInputLayout.setEnabled(false);
        } else {
            mailSwitch.setChecked(true);
            mailTextInputEditText.setEnabled(true);
            mailTextInputLayout.setEnabled(true);
            mailTextInputLayout.getEditText().setText(event.getEmail());
        }


    }

    private void defineListeners() {
        allDayEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setTimeLinearLayout.setVisibility(View.GONE);
                } else {
                    setTimeLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        setDateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        setTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(view);
            }
        });

        notificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implement alert dialog
            }
        });

        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    notificationTextView.setText("At the time of event");
                    notificationTextView.setEnabled(true);
                } else {
                    notificationTextView.setText("Don't notify me");
                    notificationTextView.setEnabled(false);
                }
            }
        });

        pickNoteColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNoteColor(view);
            }
        });

        mailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mailTextInputEditText.setEnabled(true);
                    mailTextInputLayout.setEnabled(true);
                } else {
                    mailTextInputEditText.setText("");
                    mailTextInputEditText.setEnabled(false);
                    mailTextInputLayout.setEnabled(false);
                }

            }
        });
    }


    public void setTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aCal = Calendar.getInstance();
                aCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                aCal.set(Calendar.MINUTE, minute);
                aCal.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmHour = hourOfDay;
                alarmMinute = minute;

                setTimeTextView.setText(eventTime);

            }
        }, hour, minute, false);

        timePickerDialog.show();

    }

    public void setDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar aCal = Calendar.getInstance();
                aCal.set(Calendar.YEAR, year);
                aCal.set(Calendar.MONTH, month);
                aCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                aCal.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmYear = year;
                alarmMonth = month;
                alarmDay = dayOfMonth;

                setDateTextView.setText(eventTime);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void pickNoteColor(View view) {
        final ArrayList<String> colors = Utils.getColors(this);
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker
                .setColors(colors)
                .setColumns(5)
                .setDefaultColorButton(R.color.blue)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        notColor = color;
                        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
                        bgShape.setColor(color);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ToolBar_Item_Save:
                if (confirmInputs()) {
                    Date aDate = null;
                    try {
                        aDate = Utils.eventDateFormat.parse((String) setDateTextView.getText());
                    } catch (ParseException e) {
                        Log.e(TAG, "An error has occurred while parsing the date string");
                    }

                    Event event = new Event();
                    event.setTitle(eventTitleTextInputLayout.getEditText().getText().toString().trim());
                    event.setAllDay(allDayEventSwitch.isChecked());
                    event.setDate(Utils.eventDateFormat.format(aDate));
                    event.setMonth(Utils.monthFormat.format(aDate));
                    event.setYear(Utils.yearFormat.format(aDate));
                    event.setTime(setTimeTextView.getText().toString());
                    event.setNotify(notifySwitch.isChecked());
                    int notificationID = getNotificationID(event.getTitle(), event.getDate(), event.getTime());
                    event.setNotificationID(notificationID);
                    event.setNote(eventNoteTextInputLayout.getEditText().getText().toString().trim());
                    if (notColor == 0) {
                        notColor = getResources().getInteger(R.color.red);
                    }
                    event.setColor(notColor);
                    event.setLocation(eventLocationTextInputLayout.getEditText().getText().toString().trim());
                    event.setPhoneNumber(phoneNumberTextInputLayout.getEditText().getText().toString().trim());
                    event.setEmail(mailTextInputLayout.getEditText().getText().toString().trim());

                    if (event.isNotify()) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute, 0);
                        setAlarm(calendar, event.getTitle(), event.getTime(), event.getNotificationID());
                    }

                    new UpdateEventAsyncTask().execute(event);

                }
                break;


        }

        return true;
    }

    private boolean confirmInputs() {
        if (validateEventTitle()) {
            return true;
        }

        return false;

    }

    private boolean validateEventTitle() {
        String eventTitleString = eventTitleTextInputLayout.getEditText().getText().toString().trim();
        if (eventTitleString.isEmpty()) {
            eventTitleTextInputLayout.setError("Field can't be empty!");
            return false;
        } else {
            eventTitleTextInputLayout.setError(null);
            return true;
        }
    }

    private int getNotificationID(String eventTitle, String date, String time) {
        int code = 0;
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readNotification(sqLiteDatabase, eventTitle, date, time);
        while (cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_ID));
        }
        cursor.close();
        sqLiteDatabase.close();
        return code;
    }

    private void setAlarm(Calendar calendar, String eventTitle, String time, int notificationID) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("time", time);
        intent.putExtra("notificationId", notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationID, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int requestCode) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private Event readEvent(String eventTitle, String eventDate, String eventTime) {
        Event event = new Event();

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEvent(sqLiteDatabase, eventTitle, eventDate, eventTime);

        while (cursor.moveToNext()) {

            event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
            event.setAllDay(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_ALL_DAY))));
            event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
            event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
            event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
            event.setNotify(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTIFY))));
            event.setNotificationID(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_ID)));
            event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
            event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
            event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
            event.setEmail(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
        }

        cursor.close();
        dbHelper.close();

        return event;
    }

    private class UpdateEventAsyncTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Event... events) {
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
            dbHelper.updateEvent(sqLiteDatabase, events[0], oldEventTitle, oldEventDate, oldEventTime);
            sqLiteDatabase.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dbHelper.close();
            setResult(RESULT_OK);
            finish();
        }
    }
}
