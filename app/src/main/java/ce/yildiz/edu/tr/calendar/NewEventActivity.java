package ce.yildiz.edu.tr.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    }


    private void initViews() {
        Intent intent = getIntent();
        String dateString = intent.getStringExtra("date");
        date.setText(dateString);
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

                    new SaveEventAsyncTask().execute(eventName, (String) time.getText(), eventDate, month, year);

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
        String eventTitleString = eventTitle.getEditText().getText().toString().trim();
        if (eventTitleString.isEmpty()) {
            eventTitle.setError("Field can't be empty!");
            return false;
        } else {
            eventTitle.setError(null);
            return true;
        }
    }

    private void saveEvent(String eventTitle, String time, String date, String month, String year) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.saveEvent(sqLiteDatabase, eventTitle, time, date, month, year);
        dbOpenHelper.close();
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
                date.setText(eventTime);
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    private class SaveEventAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            saveEvent(strings[0], strings[1], strings[2], strings[3], strings[4]);
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
