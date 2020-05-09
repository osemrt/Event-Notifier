package ce.yildiz.edu.tr.calendar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import ce.yildiz.edu.tr.calendar.models.Event;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, DBStructure.DATABASE_NAME, null, DBStructure.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBQueries.CREATE_EVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBQueries.DROP_EVENT_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void saveEvent(SQLiteDatabase sqLiteDatabase, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_TITLE, event.getTitle());
        contentValues.put(DBTables.EVENT_ALL_DAY, Boolean.toString(event.isAllDay()));
        contentValues.put(DBTables.EVENT_DATE, event.getDate());
        contentValues.put(DBTables.EVENT_TIME, event.getTime());
        contentValues.put(DBTables.EVENT_MONTH, event.getMonth());
        contentValues.put(DBTables.EVENT_YEAR, event.getYear());
        contentValues.put(DBTables.EVENT_NOTIFY, Boolean.toString(event.isNotify()));
        contentValues.put(DBTables.NOTIFICATION_ID, event.getNotificationID());
        contentValues.put(DBTables.EVENT_NOTE, event.getNote());
        contentValues.put(DBTables.EVENT_COLOR, event.getColor());
        contentValues.put(DBTables.EVENT_LOCATION, event.getLocation());
        contentValues.put(DBTables.EVENT_PHONE_NUMBER, event.getPhoneNumber());
        contentValues.put(DBTables.EVENT_MAIL, event.getEmail());

        sqLiteDatabase.insert(DBTables.EVENT_TABLE_NAME, null, contentValues);

    }

    public Cursor readEventsByDate(SQLiteDatabase sqLiteDatabase, String date) {
        String[] projection = {
                DBTables.EVENT_TITLE,
                DBTables.EVENT_ALL_DAY,
                DBTables.EVENT_DATE,
                DBTables.EVENT_TIME,
                DBTables.EVENT_MONTH,
                DBTables.EVENT_YEAR,
                DBTables.EVENT_NOTIFY,
                DBTables.NOTIFICATION_ID,
                DBTables.EVENT_NOTE,
                DBTables.EVENT_COLOR,
                DBTables.EVENT_LOCATION,
                DBTables.EVENT_PHONE_NUMBER,
                DBTables.EVENT_MAIL};

        String where = DBTables.EVENT_DATE + "=?";
        String[] whereArgs = {date};

        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public Cursor readEventsByMonth(SQLiteDatabase sqLiteDatabase, String year, String month) {
        String[] projection = {
                DBTables.EVENT_TITLE,
                DBTables.EVENT_ALL_DAY,
                DBTables.EVENT_DATE,
                DBTables.EVENT_TIME,
                DBTables.EVENT_MONTH,
                DBTables.EVENT_YEAR,
                DBTables.EVENT_NOTIFY,
                DBTables.NOTIFICATION_ID,
                DBTables.EVENT_NOTE,
                DBTables.EVENT_COLOR,
                DBTables.EVENT_LOCATION,
                DBTables.EVENT_PHONE_NUMBER,
                DBTables.EVENT_MAIL};

        String where = DBTables.EVENT_YEAR + "=? and " + DBTables.EVENT_MONTH + "=?";
        String[] whereArgs = {year, month};
        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public Cursor readNotificationID(SQLiteDatabase sqLiteDatabase, String eventTitle, String date, String time) {
        String[] projection = {DBTables.NOTIFICATION_ID};
        String where = DBTables.EVENT_TITLE + "=? and " + DBTables.EVENT_DATE + "=? and " + DBTables.EVENT_TIME + "=?";
        String[] whereArgs = {eventTitle, date, time};

        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public void deleteEvent(SQLiteDatabase sqLiteDatabase, String eventTitle, String date, String time) {
        String where = DBTables.EVENT_TITLE + "=? and " + DBTables.EVENT_DATE + "=? and " + DBTables.EVENT_TIME + "=?";
        String[] whereArgs = {eventTitle, date, time};
        sqLiteDatabase.delete(DBTables.EVENT_TABLE_NAME, where, whereArgs);
    }

    public void updateEvent(SQLiteDatabase sqLiteDatabase, Event newEvent, String oldEventTitle, String oldEventDate, String oldEventTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_TITLE, newEvent.getTitle());
        contentValues.put(DBTables.EVENT_ALL_DAY, Boolean.toString(newEvent.isAllDay()));
        contentValues.put(DBTables.EVENT_DATE, newEvent.getDate());
        contentValues.put(DBTables.EVENT_TIME, newEvent.getTime());
        contentValues.put(DBTables.EVENT_MONTH, newEvent.getMonth());
        contentValues.put(DBTables.EVENT_YEAR, newEvent.getYear());
        contentValues.put(DBTables.EVENT_NOTIFY, Boolean.toString(newEvent.isNotify()));
        contentValues.put(DBTables.NOTIFICATION_ID, newEvent.getNotificationID());
        contentValues.put(DBTables.EVENT_NOTE, newEvent.getNote());
        contentValues.put(DBTables.EVENT_COLOR, newEvent.getColor());
        contentValues.put(DBTables.EVENT_LOCATION, newEvent.getLocation());
        contentValues.put(DBTables.EVENT_PHONE_NUMBER, newEvent.getPhoneNumber());
        contentValues.put(DBTables.EVENT_MAIL, newEvent.getEmail());

        String where = DBTables.EVENT_TITLE + "? and " + DBTables.EVENT_DATE + "? and " + DBTables.EVENT_TITLE + "=?";
        String[] whereArgs = {oldEventTitle, oldEventDate, oldEventTime};
        sqLiteDatabase.update(DBTables.EVENT_TABLE_NAME, contentValues, where, whereArgs);
    }

}
