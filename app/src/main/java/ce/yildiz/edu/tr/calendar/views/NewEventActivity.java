package ce.yildiz.edu.tr.calendar.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import ce.yildiz.edu.tr.calendar.database.DBStructure;
import ce.yildiz.edu.tr.calendar.other.AlarmReceiver;
import petrov.kristiyan.colorpicker.ColorPicker;

public class NewEventActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Toolbar toolbar;
    private TextView time;
    private TextView date;
    private TextInputLayout eventTitle;
    private TextInputLayout location;
    private TextInputLayout addPeople;
    private TextInputLayout note;
    private ProgressBar progressBar;
    private TextView pickNoteColorTextView;

    private DBHelper dbHelper;

    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_event);

        defineViews();
        initViews();
        dbHelper = new DBHelper(this);
        setSupportActionBar(toolbar);

    }


    private void defineViews() {
        toolbar = (Toolbar) findViewById(R.id.AddNewEventActivity_Toolbar);
        time = (TextView) findViewById(R.id.AddNewEventActivity_TexView_Time);
        date = (TextView) findViewById(R.id.AddNewEventActivity_TexView_Date);
        eventTitle = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_EventTitle);
        location = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Location);
        addPeople = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_AddPeople);
        note = (TextInputLayout) findViewById(R.id.AddNewEventActivity_TextInputLayout_Note);
        progressBar = (ProgressBar) findViewById(R.id.AddNewEventActivity_ProgressBar);
        pickNoteColorTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_PickNoteColor);
    }


    private void initViews() {
        Intent intent = getIntent();


        date.setText(intent.getStringExtra("date"));
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

                time.setText(eventTime);

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

                date.setText(eventTime);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void setNoteColor(View view) {
        final ArrayList<String> colors = Utils.getColors(this);
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker
                .setColors(colors)
                .setColumns(5)
                .setDefaultColorButton(R.color.blue)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ToolBar_Item_Save:
                if (confirmInputs()) {
                    String eventName = eventTitle.getEditText().getText().toString().trim();

                    Date aDate = null;
                    try {
                        aDate = eventDateFormat.parse((String) date.getText());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String eventDate = eventDateFormat.format(aDate);
                    String month = monthFormat.format(aDate);
                    String year = yearFormat.format(aDate);

                    // TODO: First check the notify switch
                    if (true) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute, 0);
                        int requestCode = getRequestCode(date.getText().toString(), eventName, time.getText().toString());
                        setAlarm(calendar, eventName, time.getText().toString(), requestCode);
                        new SaveEventAsyncTask().execute(eventName, (String) time.getText(), eventDate, month, year, "on");
                    } else {
                        new SaveEventAsyncTask().execute(eventName, (String) time.getText(), eventDate, month, year, "off");
                    }


                }
                break;
        }

        return true;
    }

    private static String hexStringColor(Resources resources, @ColorRes int colorResId) {
        return String.format("#%06X", (0xFFFFFF & resources.getColor(colorResId)));
    }

    private boolean confirmInputs() {
        if (validateEventTitle()) {
            return true;
        }

        return false;

    }

    private boolean validateEventTitle() {
        String eventTitleString = eventTitle.getEditText().getText().toString().trim();
        if (eventTitleString.isEmpty()) {
            eventTitle.setError("Field can't be empty!");
            return false;
        } else {
            eventTitle.setError(null);
            return true;
        }
    }

    private void saveEvent(String eventTitle, String time, String date, String month, String year, String notify) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.saveEvent(sqLiteDatabase, eventTitle, time, date, month, year, notify);
        dbHelper.close();
    }

    private int getRequestCode(String Date, String EventTitle, String Time) {
        int code = 0;
        dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readIDEvents(EventTitle, Date, Time, sqLiteDatabase);
        while (cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        sqLiteDatabase.close();
        return code;
    }

    private void setAlarm(Calendar calendar, String eventTitle, String time, int requestCode) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("time", time);
        intent.putExtra("notificationId", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int requestCode) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    private class SaveEventAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            saveEvent(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setResult(RESULT_OK);
            finish();
        }
    }
}
