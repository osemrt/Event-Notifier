package ce.yildiz.edu.tr.calendar;

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

import petrov.kristiyan.colorpicker.ColorPicker;

public class NewEventActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    private SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    private Toolbar toolbar;
    private DBOpenHelper dbOpenHelper;
    private TextView time;
    private TextView date;
    private TextInputLayout eventTitle;
    private TextInputLayout location;
    private TextInputLayout addPeople;
    private TextInputLayout note;
    private ProgressBar progressBar;
    //private ImageButton noteColorImageButton;
    //private EditText pickNoteColorEditText;
    private TextView pickNoteColorTextView;

    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_event);

        defineViews();
        initViews();
        dbOpenHelper = new DBOpenHelper(this);
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
        //noteColorImageButton = (ImageButton) findViewById(R.id.AddNewEventActivity_ImageButton_NoteColor);
        //pickNoteColorEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_PickNoteColor);
        pickNoteColorTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_PickNoteColor);
    }


    private void initViews() {
        Intent intent = getIntent();
        String dateString = intent.getStringExtra("date");
        date.setText(dateString);
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
        ColorPicker colorPicker = new ColorPicker(this);

        final ArrayList<String> colors = getColors();
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

    @SuppressLint("ResourceType")
    private ArrayList<String> getColors() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add(getResources().getString(R.color.darkIndigo));
        colors.add(getResources().getString(R.color.yellow));
        colors.add(getResources().getString(R.color.deepPurple));
        colors.add(getResources().getString(R.color.pink));
        colors.add(getResources().getString(R.color.Grey));
        colors.add(getResources().getString(R.color.cyan));
        colors.add(getResources().getString(R.color.green));
        colors.add(getResources().getString(R.color.lime));
        colors.add(getResources().getString(R.color.lightIndigo));
        colors.add(getResources().getString(R.color.black));
        colors.add(getResources().getString(R.color.white));
        colors.add(getResources().getString(R.color.lite_blue));
        colors.add(getResources().getString(R.color.red));
        colors.add(getResources().getString(R.color.brown));
        colors.add(getResources().getString(R.color.color11));
        return colors;
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
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.saveEvent(sqLiteDatabase, eventTitle, time, date, month, year, notify);
        dbOpenHelper.close();
    }

    private int getRequestCode(String Date, String EventTitle, String Time) {
        int code = 0;
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readIDEvents(EventTitle, Date, Time, sqLiteDatabase);
        while (cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        sqLiteDatabase.close();
        return code;
    }

    private void updateEvent(String Date, String EventTitle, String Time, String notify) {
        dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent(EventTitle, Date, Time, notify, sqLiteDatabase);
        dbOpenHelper.close();
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

    private Date convertStringToTime(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aDate;
    }


    private class SaveEventAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.INVISIBLE);
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
