package ce.yildiz.edu.tr.calendar.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ce.yildiz.edu.tr.calendar.other.AlarmReceiver;
import petrov.kristiyan.colorpicker.ColorPicker;

public class NewEventActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private final int MAPS_ACTIVITY_REQUEST = 1;

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
    private RadioGroup notificationPreferenceRadioGroup;
    private RadioGroup repetitionPreferenceRadioGroup;
    private RadioButton selectedPreferenceRadioButton;
    private View notificationDialogView;
    private View eventRepetitionDialogView;
    private Button notificationBackButton;
    private Button repetitionBackButton;
    private TextInputLayout eventNoteTextInputLayout;
    private TextView pickNoteColorTextView;
    private TextInputLayout eventLocationTextInputLayout;
    private ImageButton locationImageButton;
    private TextInputLayout phoneNumberTextInputLayout;
    private TextInputLayout mailTextInputLayout;
    private TextInputEditText mailTextInputEditText;
    private Switch mailSwitch;

    private AlertDialog notificationAlertDialog;
    private AlertDialog repetitionAlertDialog;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private int notColor;
    private DBHelper dbHelper;
    private List<Notification> notifications;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        event = new Event();
        notifications = new ArrayList<>();
        dbHelper = new DBHelper(this);

        defineViews();
        initViews();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        notificationDialogView = LayoutInflater.from(getApplicationContext().getApplicationContext()).inflate(R.layout.layout_alert_dialog_notification, null, false);
        eventRepetitionDialogView = LayoutInflater.from(getApplicationContext().getApplicationContext()).inflate(R.layout.layout_alert_dialog_repeat, null, false);
        repetitionPreferenceRadioGroup = (RadioGroup) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_Repeat_RadioGroup_RepeatPreference);
        repetitionBackButton = (Button) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_Repeat_Button_Back);
        notificationPreferenceRadioGroup = (RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_Notification_RadioGroup_NotificationPreference);
        notificationBackButton = (Button) notificationDialogView.findViewById(R.id.AlertDialogLayout_Notification_Button_Back);
        builder.setView(notificationDialogView);
        notificationAlertDialog = builder.create();
        builder.setView(eventRepetitionDialogView);
        repetitionAlertDialog = builder.create();

    }

    @SuppressLint("ResourceType")
    private void initViews() {
        Intent intent = getIntent();
        String asd = intent.getStringExtra("date");
        setDateTextView.setText(intent.getStringExtra("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        setTimeTextView.setText(new SimpleDateFormat("K:mm a", Locale.ENGLISH).format(calendar.getTime()));

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorTextView.getBackground();
        bgShape.setColor(getResources().getInteger(R.color.red));

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

        notificationAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                int selectedId = notificationPreferenceRadioGroup.getCheckedRadioButtonId();
                selectedPreferenceRadioButton = (RadioButton) notificationDialogView.findViewById(selectedId);
                notifications.add(new Notification(selectedPreferenceRadioButton.getText().toString()));
                setUpRecyclerView();
            }
        });

        ((RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_Notification_RadioGroup_NotificationPreference)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                selectedPreferenceRadioButton = (RadioButton) notificationDialogView.findViewById(buttonId);
                notifications.add(new Notification(selectedPreferenceRadioButton.getText().toString()));
                notificationAlertDialog.dismiss();
                setUpRecyclerView();
            }
        });

        ((RadioGroup) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_Repeat_RadioGroup_RepeatPreference)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int buttonId) {
                selectedPreferenceRadioButton = (RadioButton) eventRepetitionDialogView.findViewById(buttonId);
                repeatTextView.setText("Repeat " + selectedPreferenceRadioButton.getText().toString());
                repetitionAlertDialog.dismiss();
            }
        });

        notificationBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationAlertDialog.dismiss();
            }
        });

        repeatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetitionAlertDialog.show();
            }
        });

        repetitionBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetitionAlertDialog.dismiss();
            }
        });

        repetitionAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                int selectedId = repetitionPreferenceRadioGroup.getCheckedRadioButtonId();
                selectedPreferenceRadioButton = (RadioButton) repetitionAlertDialog.findViewById(selectedId);
                repeatTextView.setText(selectedPreferenceRadioButton.getText().toString());
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
                startActivityForResult(new Intent(getApplicationContext(), MapsActivity.class), MAPS_ACTIVITY_REQUEST);
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
                    if (event.isNotify()) {
                        setAlarms();
                    }
                }
                break;
        }

        return true;
    }

    private void setAlarms() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute, 0);
        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            switch (notification.getTime()) {
                case "10 minutes before":
                    aCal.add(Calendar.MINUTE, -10);
                    break;
                case "1 hour before":
                    aCal.add(Calendar.HOUR_OF_DAY, -1);
                    break;
                case "1 day before":
                    aCal.add(Calendar.DAY_OF_MONTH, -1);
                    break;
            }
            setAlarm(aCal, event.getTitle(), event.getTime(), notification.getId());
        }
    }

    private void setAlarm(Calendar calendar, String eventTitle, String time, int notificationId) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("time", time);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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
        event.setRepetition(repeatTextView.getText().toString());
        event.setNote(eventNoteTextInputLayout.getEditText().getText().toString().trim());
        if (notColor == 0) {
            notColor = getResources().getInteger(R.color.red);
        } else {
            event.setColor(notColor);
        }
        event.setLocation(eventLocationTextInputLayout.getEditText().getText().toString().trim());
        event.setPhoneNumber(phoneNumberTextInputLayout.getEditText().getText().toString().trim());
        event.setMail(mailTextInputLayout.getEditText().getText().toString().trim());

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

    private int getEventId(String eventTitle, String eventDate, String eventTime) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEvent(sqLiteDatabase, eventTitle, eventDate, eventTime);

        int eventId = -1;
        while (cursor.moveToNext()) {
            eventId = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID));
        }

        cursor.close();
        sqLiteDatabase.close();
        return eventId;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAPS_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                eventLocationTextInputLayout.getEditText().setText(data.getStringExtra("address"));

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }
}
