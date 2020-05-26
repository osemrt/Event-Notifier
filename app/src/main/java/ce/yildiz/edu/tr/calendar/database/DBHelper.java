package ce.yildiz.edu.tr.calendar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ce.yildiz.edu.tr.calendar.Utils;
import ce.yildiz.edu.tr.calendar.models.Event;
import ce.yildiz.edu.tr.calendar.models.Notification;

public class DBHelper extends SQLiteOpenHelper {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public DBHelper(@Nullable Context context) {
        super(context, DBStructure.DATABASE_NAME, null, DBStructure.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBQueries.CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(DBQueries.CREATE_RECURRING_PATTERN_TABLE);
        sqLiteDatabase.execSQL(DBQueries.CREATE_EVENT_INSTANCE_EXCEPTION_TABLE);
        sqLiteDatabase.execSQL(DBQueries.CREATE_NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBQueries.DROP_EVENT_TABLE);
        sqLiteDatabase.execSQL(DBQueries.DROP_RECURRING_TABLE);
        sqLiteDatabase.execSQL(DBQueries.DROP_EVENT_INSTANCE_EXCEPTION_TABLE);
        sqLiteDatabase.execSQL(DBQueries.DROP_NOTIFICATION_TABLE);
        onCreate(sqLiteDatabase);
    }

    // Create functions
    public void saveNotification(SQLiteDatabase sqLiteDatabase, Notification notification) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.NOTIFICATION_EVENT_ID, notification.getEventId());
        contentValues.put(DBTables.NOTIFICATION_TIME, notification.getTime());
        contentValues.put(DBTables.NOTIFICATION_CHANNEL_ID, notification.getChannelId());

        sqLiteDatabase.insert(DBTables.NOTIFICATION_TABLE_NAME, null, contentValues);

    }

    public void saveEvent(SQLiteDatabase sqLiteDatabase, Event event) {
        int eventId = createEvent(sqLiteDatabase, event);
        event.setId(eventId);
        int parentId = createEventParent(sqLiteDatabase, event);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_PARENT_ID, parentId);

        String where = DBTables.EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(event.getId())};
        sqLiteDatabase.update(DBTables.EVENT_TABLE_NAME, contentValues, where, whereArgs);

        if (event.isRecurring()) {
            createRecurringPattern(sqLiteDatabase, event);
        }
    }

    private void createRecurringPattern(SQLiteDatabase sqLiteDatabase, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.RECURRING_PATTERN_EVENT_ID, event.getId());
        contentValues.put(DBTables.RECURRING_PATTERN_TYPE, event.getRecurringPeriod());

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Utils.eventDateFormat.parse(event.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        contentValues.put(DBTables.RECURRING_PATTERN_MONTH_OF_YEAR, calendar.get(Calendar.MONTH));
        contentValues.put(DBTables.RECURRING_PATTERN_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        contentValues.put(DBTables.RECURRING_PATTERN_DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));

        sqLiteDatabase.insert(DBTables.RECURRING_PATTERN_TABLE_NAME, null, contentValues);
    }

    private int createEvent(SQLiteDatabase sqLiteDatabase, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_TITLE, event.getTitle());
        contentValues.put(DBTables.EVENT_IS_ALL_DAY, event.isAllDay() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_DATE, event.getDate());
        contentValues.put(DBTables.EVENT_MONTH, event.getMonth());
        contentValues.put(DBTables.EVENT_YEAR, event.getYear());
        contentValues.put(DBTables.EVENT_TIME, event.getTime());
        contentValues.put(DBTables.EVENT_DURATION, event.getDuration());
        contentValues.put(DBTables.EVENT_IS_NOTIFY, event.isNotify() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_IS_RECURRING, event.isRecurring() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_NOTE, event.getNote());
        contentValues.put(DBTables.EVENT_COLOR, event.getColor());
        contentValues.put(DBTables.EVENT_LOCATION, event.getLocation());
        contentValues.put(DBTables.EVENT_PHONE_NUMBER, event.getPhoneNumber());
        contentValues.put(DBTables.EVENT_MAIL, event.getMail());
        contentValues.put(DBTables.EVENT_PARENT_ID, -1);

        return (int) sqLiteDatabase.insert(DBTables.EVENT_TABLE_NAME, null, contentValues);

    }

    private int createEventParent(SQLiteDatabase sqLiteDatabase, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID, event.getId());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_IS_CHANGED, FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_IS_CANCELED, FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TITLE, event.getTitle());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_ALL_DAY, event.isAllDay() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DATE, event.getDate());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_MONTH, event.getMonth());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_YEAR, event.getYear());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TIME, event.getTime());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DURATION, event.getDuration());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_NOTIFY, event.isNotify() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_NOTE, event.getNote());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_COLOR, event.getColor());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_LOCATION, event.getLocation());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_PHONE_NUMBER, event.getPhoneNumber());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_MAIL, event.getMail());

        return (int) sqLiteDatabase.insert(DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME, null, contentValues);

    }

    // Read functions
    public Cursor readEventsByMonth(SQLiteDatabase sqLiteDatabase, String year, String month) {
        String[] projection = {
                DBTables.EVENT_ID};
        String where = DBTables.EVENT_YEAR + "=? and " + DBTables.EVENT_MONTH + "=?";
        String[] whereArgs = {year, month};

        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);


    }

    public Event readEvent(SQLiteDatabase sqLiteDatabase, int eventId) {
        Cursor cursor = null;
        if (isChanged(sqLiteDatabase, eventId)) {
            cursor = readEventInstanceExceptionByEventId(sqLiteDatabase, eventId);
        } else {
            cursor = readEventById(sqLiteDatabase, eventId);
        }
        Event event = new Event();
        while (cursor.moveToNext()) {
            event.setId(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID)));
            event.setTitle(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TITLE)));
            event.setAllDay(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_IS_ALL_DAY)) == TRUE);
            event.setDate(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DATE)));
            event.setMonth(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MONTH)));
            event.setYear(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_YEAR)));
            event.setTime(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_TIME)));
            event.setDuration(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_DURATION)));
            event.setNotify(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_IS_NOTIFY)) == TRUE);
            event.setRecurring(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_IS_RECURRING)) == TRUE);
            event.setNote(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_NOTE)));
            event.setColor(cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_COLOR)));
            event.setLocation(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_LOCATION)));
            event.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_PHONE_NUMBER)));
            event.setMail(cursor.getString(cursor.getColumnIndex(DBTables.EVENT_MAIL)));
        }
        return event;
    }

    private boolean isChanged(SQLiteDatabase sqLiteDatabase, int eventId) {
        boolean isChanged = false;
        Cursor cursor = readEventInstanceExceptionByEventId(sqLiteDatabase, eventId);
        while (cursor.moveToNext()) {
            isChanged = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_INSTANCE_EXCEPTION_IS_CHANGED)) == TRUE;
        }
        return isChanged;
    }

    public boolean isRecurring(SQLiteDatabase sqLiteDatabase, int eventId) {
        boolean isRecurring = false;
        Cursor cursor = readEventById(sqLiteDatabase, eventId);
        while (cursor.moveToNext()) {
            isRecurring = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_IS_RECURRING)) == TRUE;
        }
        return isRecurring;
    }

    private Cursor readEventInstanceExceptionByEventId(SQLiteDatabase sqLiteDatabase, int eventId) {
        String[] projection = {
                DBTables.EVENT_INSTANCE_EXCEPTION_ID,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID,
                DBTables.EVENT_INSTANCE_EXCEPTION_IS_CHANGED,
                DBTables.EVENT_INSTANCE_EXCEPTION_IS_CANCELED,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TITLE,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_ALL_DAY,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DATE,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_MONTH,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_YEAR,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TIME,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DURATION,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_NOTIFY,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_RECURRING,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_NOTE,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_COLOR,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_LOCATION,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_PHONE_NUMBER,
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_MAIL};

        String where = DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        return sqLiteDatabase.query(DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    private Cursor readEventById(SQLiteDatabase sqLiteDatabase, int eventId) {
        String[] projection = {
                DBTables.EVENT_ID,
                DBTables.EVENT_TITLE,
                DBTables.EVENT_IS_ALL_DAY,
                DBTables.EVENT_DATE,
                DBTables.EVENT_MONTH,
                DBTables.EVENT_YEAR,
                DBTables.EVENT_TIME,
                DBTables.EVENT_DURATION,
                DBTables.EVENT_IS_NOTIFY,
                DBTables.EVENT_IS_RECURRING,
                DBTables.EVENT_NOTE,
                DBTables.EVENT_COLOR,
                DBTables.EVENT_LOCATION,
                DBTables.EVENT_PHONE_NUMBER,
                DBTables.EVENT_MAIL,
                DBTables.EVENT_PARENT_ID};
        String where = DBTables.EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};

        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public Cursor readRecurringEvents(SQLiteDatabase sqLiteDatabase, String period) {
        String[] projection = {
                DBTables.RECURRING_PATTERN_EVENT_ID,
                DBTables.RECURRING_PATTERN_TYPE,
                DBTables.RECURRING_PATTERN_MONTH_OF_YEAR,
                DBTables.RECURRING_PATTERN_DAY_OF_MONTH,
                DBTables.RECURRING_PATTERN_DAY_OF_WEEK};
        String where = DBTables.RECURRING_PATTERN_TYPE + "=?";
        String[] whereArgs = {period};

        return sqLiteDatabase.query(DBTables.RECURRING_PATTERN_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public Cursor readAllRecurringPatterns(SQLiteDatabase sqLiteDatabase) {
        String[] projection = {
                DBTables.RECURRING_PATTERN_EVENT_ID,
                DBTables.RECURRING_PATTERN_TYPE,
                DBTables.RECURRING_PATTERN_MONTH_OF_YEAR,
                DBTables.RECURRING_PATTERN_DAY_OF_MONTH,
                DBTables.RECURRING_PATTERN_DAY_OF_WEEK};
        return sqLiteDatabase.query(DBTables.RECURRING_PATTERN_TABLE_NAME, projection, null, null, null, null, null);
    }

    public Cursor readEventsByDate(SQLiteDatabase sqLiteDatabase, String date) {
        String[] projection = {
                DBTables.EVENT_ID};
        String where = DBTables.EVENT_DATE + "=?";
        String[] whereArgs = {date};

        return sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public List<Event> readAllEvents(SQLiteDatabase sqLiteDatabase) {
        List<Event> events = new ArrayList<>();
        String[] projection = {
                DBTables.EVENT_ID};
        Cursor cursor = sqLiteDatabase.query(DBTables.EVENT_TABLE_NAME, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            events.add(readEvent(getReadableDatabase(), cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID))));
        }
        cursor.close();
        return events;
    }

    public Event readEventByTimestamp(SQLiteDatabase sqLiteDatabase, String eventTitle, String date, String time) {
        String[] projection = {
                DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID};
        String where = DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TITLE + "=? and " + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DATE + "=? and " + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TIME + "=?";
        String[] whereArgs = {eventTitle, date, time};

        Cursor cursor = sqLiteDatabase.query(DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME, projection, where, whereArgs, null, null, null);

        int eventId = 0;
        while (cursor.moveToNext()) {
            eventId = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID));
        }
        cursor.close();
        return readEvent(sqLiteDatabase, eventId);

    }

    public String readRecurringPeriod(SQLiteDatabase sqLiteDatabase, int eventId) {
        String[] projection = {
                DBTables.RECURRING_PATTERN_TYPE};
        String where = DBTables.RECURRING_PATTERN_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};

        String recurringPeriod = null;
        Cursor cursor = sqLiteDatabase.query(DBTables.RECURRING_PATTERN_TABLE_NAME, projection, where, whereArgs, null, null, null);
        while (cursor.moveToNext()) {
            recurringPeriod = cursor.getString(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_TYPE));
        }
        cursor.close();
        return recurringPeriod == null ? "One-Time" : recurringPeriod;
    }

    public Cursor readEventNotifications(SQLiteDatabase sqLiteDatabase, int eventId) {
        String[] projection = {
                DBTables.NOTIFICATION_ID,
                DBTables.NOTIFICATION_EVENT_ID,
                DBTables.NOTIFICATION_TIME,
                DBTables.NOTIFICATION_CHANNEL_ID
        };

        String where = DBTables.NOTIFICATION_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};

        return sqLiteDatabase.query(DBTables.NOTIFICATION_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public static boolean checkIsAlreadyInDBorNot(SQLiteDatabase sqLiteDatabase, String tableName, String field, String fieldValue) {
        String Query = "SELECT * FROM " + tableName + " where " + field + " = " + fieldValue;
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    // Update functions
    public void updateEvent(SQLiteDatabase sqLiteDatabase, int eventId, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_IS_CHANGED, TRUE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TITLE, event.getTitle());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_ALL_DAY, event.isAllDay() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DATE, event.getDate());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_MONTH, event.getDate());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_YEAR, event.getYear());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TIME, event.getTime());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DURATION, event.getDuration());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_NOTIFY, event.isNotify() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_RECURRING, event.isRecurring() ? TRUE : FALSE);
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_NOTE, event.getNote());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_COLOR, event.getColor());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_LOCATION, event.getLocation());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_PHONE_NUMBER, event.getPhoneNumber());
        contentValues.put(DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_MAIL, event.getMail());

        String where = DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        sqLiteDatabase.update(DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME, contentValues, where, whereArgs);

        if (event.isRecurring()) {
            if (checkIsAlreadyInDBorNot(sqLiteDatabase, DBTables.RECURRING_PATTERN_TABLE_NAME, DBTables.RECURRING_PATTERN_EVENT_ID, Integer.toString(event.getId()))) {
                updateRecurringPattern(sqLiteDatabase, event);
            } else {
                createRecurringPattern(sqLiteDatabase, event);
            }
        }

    }

    private void updateRecurringPattern(SQLiteDatabase sqLiteDatabase, Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTables.RECURRING_PATTERN_EVENT_ID, event.getId());
        contentValues.put(DBTables.RECURRING_PATTERN_TYPE, event.getRecurringPeriod());

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Utils.eventDateFormat.parse(event.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        contentValues.put(DBTables.RECURRING_PATTERN_MONTH_OF_YEAR, calendar.get(Calendar.MONTH));
        contentValues.put(DBTables.RECURRING_PATTERN_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        contentValues.put(DBTables.RECURRING_PATTERN_DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));

        String where = DBTables.RECURRING_PATTERN_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(event.getId())};
        sqLiteDatabase.update(DBTables.RECURRING_PATTERN_TABLE_NAME, contentValues, where, whereArgs);
    }

    // Delete functions
    public void deleteNotificationsByEventId(SQLiteDatabase sqLiteDatabase, int eventId) {
        String where = DBTables.NOTIFICATION_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        sqLiteDatabase.delete(DBTables.NOTIFICATION_TABLE_NAME, where, whereArgs);
    }

    public void deleteEvent(SQLiteDatabase sqLiteDatabase, int eventId) {
        String where = DBTables.EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        sqLiteDatabase.delete(DBTables.EVENT_TABLE_NAME, where, whereArgs);
    }

    public void deleteRecurringPattern(SQLiteDatabase sqLiteDatabase, int eventId) {
        String where = DBTables.RECURRING_PATTERN_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        sqLiteDatabase.delete(DBTables.RECURRING_PATTERN_TABLE_NAME, where, whereArgs);
    }

    public void deleteEventInstanceException(SQLiteDatabase sqLiteDatabase, int eventId) {
        String where = DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID + "=?";
        String[] whereArgs = {Integer.toString(eventId)};
        sqLiteDatabase.delete(DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME, where, whereArgs);
    }

    public void deleteNotificationById(SQLiteDatabase sqLiteDatabase, int notificationId) {
        String where = DBTables.NOTIFICATION_ID + "=?";
        String[] whereArgs = {Integer.toString(notificationId)};
        sqLiteDatabase.delete(DBTables.NOTIFICATION_TABLE_NAME, where, whereArgs);
    }


}
