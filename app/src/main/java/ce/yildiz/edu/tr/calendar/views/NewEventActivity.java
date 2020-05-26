package ce.yildiz.edu.tr.calendar.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.adapters.NotificationAdapter;
import ce.yildiz.edu.tr.calendar.database.DBHelper;
import ce.yildiz.edu.tr.calendar.database.DBTables;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.models.Notification;
import ce.yildiz.edu.tr.calendar.other.ServiceAutoLauncher;
import petrov.kristiyan.colorpicker.ColorPicker;

public class NewEventActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private final int MAPS_ACTIVITY_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextInputLayout eventTitleTextInputLayout;
    private Switch allDayEventSwitch;
    private LinearLayout setDateLinearLayout;
    private TextView setDateTextView;
    private LinearLayout setTimeLinearLayout;
    private TextView setTimeTextView;
    private Button setDurationButton;
    private RecyclerView notificationsRecyclerView;
    private TextView addNotificationTextView;
    private TextView repeatTextView;
    private TextInputLayout eventNoteTextInputLayout;
    private TextView pickNoteColorTextView;
    private TextInputLayout eventLocationTextInputLayout;
    private ImageButton locationImageButton;
    private TextInputLayout phoneNumberTextInputLayout;
    private TextInputLayout mailTextInputLayout;
    private TextInputEditText mailTextInputEditText;
    private Switch mailSwitch;

    private boolean mLocationPermissionGranted;

    private AlertDialog notificationAlertDialog;
    private AlertDialog repetitionAlertDialog;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private int notColor;
    private DBHelper dbHelper;
    private List<Notification> notifications;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        event = new Event();
        notifications = new ArrayList<>();
        dbHelper = new DBHelper(this);

        defineViews();
        initViews();
        initVariables();
        createAlertDialogs();
        defineListeners();

        setSupportActionBar(toolbar);
    }


    private void defineViews() {
        eventTitleTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_EventTitle);
        allDayEventSwitch = (Switch) findViewById(R.id.AddNewEventActivity_Switch_AllDayEvent);
        setDateLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetDate);
        setDateTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetDate);
        setTimeLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetTime);
        setTimeTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetTime);
        setDurationButton = (Button) findViewById(R.id.AddNewEventActivity_Button_Duration);
        notificationsRecyclerView = (RecyclerView) findViewById(R.id.AddNewEventActivity_RecyclerView_Notifications);
        repeatTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Repeat);
        addNotificationTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Add_Notification);
        eventNoteTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Note);
        pickNoteColorTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_PickNoteColor);
        eventLocationTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Location);
        locationImageButton = (ImageButton) findViewById(R.id.AddNewEventActivity_ImageButton_Location);
        phoneNumberTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_PhoneNumber);
        mailTextInputLayout = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Mail);
        mailTextInputEditText = (TextInputEditText) findViewById(R.id.AddNewEventActivity_TextInputEditText_Mail);
        mailSwitch = (Switch) findViewById(R.id.AddNewEventActivity_Switch_Mail);

        progressBar = (ProgressBar) findViewById(R.id.AddNewEventActivity_ProgressBar);
        toolbar = (Toolbar) findViewById(R.id.AddNewEventActivity_Toolbar);

    }

    @SuppressLint("ResourceType")
    private void initViews() {
        Intent intent = getIntent();
        setDateTextView.setText(intent.getStringExtra("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        setTimeTextView.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(calendar.getTime()));

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
        bgShape.setColor(getResources().getInteger(R.color.red));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        repeatTextView.setText(sharedPreferences.getString("frequency", "Repeat One-Time"));
        notifications.add(new Notification(sharedPreferences.getString("reminder", getResources().getString(R.string.at_the_time_of_event))));
        setUpRecyclerView();
    }

    private void initVariables() {
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeZone(TimeZone.getDefault());
        alarmHour = mCal.get(Calendar.HOUR_OF_DAY);
        alarmMinute = mCal.get(Calendar.MINUTE);

        try {
            mCal.setTime(Utils.eventDateFormat.parse(getIntent().getStringExtra("date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        alarmYear = mCal.get(Calendar.YEAR);
        alarmMonth = mCal.get(Calendar.MONTH);
        alarmDay = mCal.get(Calendar.DAY_OF_MONTH);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }

    }


    private void createAlertDialogs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        // Notification AlertDialog
        final View notificationDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_notification, null, false);
        RadioGroup notificationRadioGroup = (RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        notificationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                notifications.add(new Notification(((RadioButton) notificationDialogView.findViewById(checkedId)).getText().toString()));
                notificationAlertDialog.dismiss();
                setUpRecyclerView();
            }
        });
        builder.setView(notificationDialogView);
        notificationAlertDialog = builder.create();
        ((Button) notificationDialogView.findViewById(R.id.AlertDialogLayout_Button_Back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAlertDialog.dismiss();
            }
        });

        // Event repetition AlertDialog
        final View eventRepetitionDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_repeat, null, false);
        RadioGroup eventRepetitionRadioGroup = (RadioGroup) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        eventRepetitionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                repeatTextView.setText("Repeat " + ((RadioButton) eventRepetitionDialogView.findViewById(checkedId)).getText().toString());
                repetitionAlertDialog.dismiss();
            }
        });
        builder.setView(eventRepetitionDialogView);
        repetitionAlertDialog = builder.create();
        ((Button) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_Button_Back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repetitionAlertDialog.dismiss();
            }
        });
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

        setDurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDuration(view);
            }
        });

        addNotificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationAlertDialog.show();
            }
        });

        repeatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetitionAlertDialog.show();
            }
        });

        pickNoteColorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNoteColor(view);
            }
        });

        locationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationPermissionGranted) {
                    getLocationPermission();
                } else {
                    startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), MAPS_ACTIVITY_REQUEST);
                }
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

    private void setDuration(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DurationPickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setDurationButton.setText("DURATION: " + Integer.toString(hourOfDay) + " HOURS " + Integer.toString(minute) + " MINUTES");
            }
        }, 0, 0, true);
        timePickerDialog.setTitle("Duration");

        timePickerDialog.show();
    }

    public void setTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aCal = Calendar.getInstance();
                aCal.setTimeZone(TimeZone.getDefault());
                aCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                aCal.set(Calendar.MINUTE, minute);
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
                aCal.setTimeZone(TimeZone.getDefault());
                aCal.set(Calendar.YEAR, year);
                aCal.set(Calendar.MONTH, month);
                aCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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

    private void setUpRecyclerView() {
        notificationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setMeasurementCacheEnabled(false);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        NotificationAdapter notificationAdapter = new NotificationAdapter(this, notifications);
        notificationsRecyclerView.setAdapter(notificationAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ToolBar_Item_Save:
                if (confirmInputs()) {
                    getViewValues();
                    new SaveAsyncTask().execute();
                }
                break;
        }
        return true;
    }

    private void setAlarms() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                Log.i(TAG, "setAlarms: ");
            }

            setAlarm(notification, aCal.getTimeInMillis());
        }
    }

    private void setAlarm(Notification notification, long triggerAtMillis) {
        Intent intent = new Intent(getApplicationContext(), ServiceAutoLauncher.class);
        intent.putExtra("eventTitle", event.getTitle());
        intent.putExtra("eventNote", event.getNote());
        intent.putExtra("eventColor", event.getColor());
        intent.putExtra("eventTimeStamp", event.getDate() + ", " + event.getTime());
        intent.putExtra("interval", getInterval());
        intent.putExtra("notificationId", notification.getChannelId());
        intent.putExtra("soundName", getString("ringtone"));

        Log.d(TAG, "setAlarm: " + notification.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notification.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    private String getInterval() {
        String interval = getString(R.string.one_time);
        String repeatingPeriod = repeatTextView.getText().toString();
        if (repeatingPeriod.equals(getString(R.string.daily))) {
            interval = getString(R.string.daily);
        } else if (repeatingPeriod.equals(getString(R.string.weekly))) {
            interval = getString(R.string.weekly);
        } else if (repeatingPeriod.equals(getString(R.string.monthly))) {
            interval = getString(R.string.monthly);
        } else if (repeatingPeriod.equals(getString(R.string.yearly))) {
            interval = getString(R.string.yearly);
        }
        return interval;
    }

    @SuppressLint("ResourceType")
    private void getViewValues() {
        Date aDate = null;
        try {
            aDate = Utils.eventDateFormat.parse((String) setDateTextView.getText());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "An error has occurred while parsing the date string");
        }
        event.setTitle(eventTitleTextInputLayout.getEditText().getText().toString().trim());
        event.setAllDay(allDayEventSwitch.isChecked());
        event.setDate(Utils.eventDateFormat.format(aDate));
        event.setMonth(Utils.monthFormat.format(aDate));
        event.setYear(Utils.yearFormat.format(aDate));
        event.setTime(setTimeTextView.getText().toString());
        event.setDuration(setDurationButton.getText().toString());
        event.setNotify(!notifications.isEmpty());
        event.setRecurring(isRecurring(repeatTextView.getText().toString()));
        event.setRecurringPeriod(repeatTextView.getText().toString());
        event.setNote(eventNoteTextInputLayout.getEditText().getText().toString().trim());
        if (notColor == 0) {
            notColor = getResources().getInteger(R.color.red);
            event.setColor(notColor);
        } else {
            event.setColor(notColor);
        }
        event.setLocation(eventLocationTextInputLayout.getEditText().getText().toString().trim());
        event.setPhoneNumber(phoneNumberTextInputLayout.getEditText().getText().toString().trim());
        event.setMail(mailTextInputLayout.getEditText().getText().toString().trim());

    }

    private boolean isRecurring(String toString) {
        return !toString.equals("Repeat One-Time");
    }

    private boolean confirmInputs() {
        if (!validateEventTitle()) {
            return false;
        }

        if (!validateNotifications()) {
            Snackbar.make(addNotificationTextView, "You cannot set a notification to the past.", BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private boolean validateNotifications() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                Log.i(TAG, "setAlarms: ");
            }

            if (aCal.before(Calendar.getInstance())) {
                return false;
            }
        }
        return true;
    }

    private class SaveAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper.saveEvent(dbHelper.getWritableDatabase(), event);
            int event_id = getEventId(event.getTitle(), event.getDate(), event.getTime());
            for (Notification notification : notifications) {
                notification.setEventId(event_id);
                dbHelper.saveNotification(dbHelper.getWritableDatabase(), notification);
            }
            notifications = readNotifications(event_id);
            if (event.isNotify()) {
                setAlarms();
            }
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

    private ArrayList<Notification> readNotifications(int eventId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventNotifications(sqLiteDatabase, eventId);
        while (cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_ID)));
            notification.setEventId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_EVENT_ID)));
            notification.setTime(cursor.getString(cursor.getColumnIndex(DBTables.NOTIFICATION_TIME)));
            notification.setChannelId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_CHANNEL_ID)));
            notifications.add(notification);
        }

        return notifications;
    }

    private int getEventId(String eventTitle, String eventDate, String eventTime) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = dbHelper.readEventByTimestamp(sqLiteDatabase, eventTitle, eventDate, eventTime);
        sqLiteDatabase.close();
        return event.getId();
    }

    private int getAppTheme() {
        switch (getString("theme")) {
            case "Dark":
                return R.style.DarkTheme;
            case "Indigo":
                return R.style.DarkIndigoTheme;
        }

        return R.style.DarkIndigoTheme;
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(key, "Indigo");
    }

    private void getLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), MAPS_ACTIVITY_REQUEST);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAPS_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                eventLocationTextInputLayout.getEditText().setText(data.getStringExtra("address"));
            }
        }

    }
}
