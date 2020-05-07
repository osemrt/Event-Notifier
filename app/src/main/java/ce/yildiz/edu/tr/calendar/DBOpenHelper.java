package ce.yildiz.edu.tr.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + DBStructure.EVENT_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBStructure.EVENT + " TEXT, "
                    + DBStructure.TIME + " TIME, "
                    + DBStructure.DATE + " TEXT, " + DBStructure.MONTH + " TEXT, " + DBStructure.YEAR + " TEXT)";

    public static final String DROP_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + DBStructure.EVENT_TABLE_NAME;

    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_EVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_EVENT_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void saveEvent(SQLiteDatabase sqLiteDatabase, String event, String time, String date, String month, String year) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.EVENT, event);
        contentValues.put(DBStructure.TIME, time);
        contentValues.put(DBStructure.DATE, date);
        contentValues.put(DBStructure.MONTH, month);
        contentValues.put(DBStructure.YEAR, year);
        sqLiteDatabase.insert(DBStructure.EVENT_TABLE_NAME, null, contentValues);

    }

    public Cursor readEvents(String date, SQLiteDatabase sqLiteDatabase) {
        String[] projection = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR};
        String where = DBStructure.DATE + "=?";
        String[] whereArgs = {date};

        return sqLiteDatabase.query(DBStructure.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public Cursor readEventsPerMonth(String month, String year, SQLiteDatabase sqLiteDatabase) {
        String[] projection = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR};
        String where = DBStructure.MONTH + "=? and " + DBStructure.YEAR + "=?";
        String[] whereArgs = {month, year};
        return sqLiteDatabase.query(DBStructure.EVENT_TABLE_NAME, projection, where, whereArgs, null, null, null);
    }

    public void deleteEvent(String eventTitle, String date, String time, SQLiteDatabase sqLiteDatabase) {
        String selection = DBStructure.EVENT + "=? and " + DBStructure.DATE + "=? and " + DBStructure.TIME + "=?";
        String[] selectionArgs = {eventTitle, date, time};
        sqLiteDatabase.delete(DBStructure.EVENT_TABLE_NAME, selection, selectionArgs);
    }

}
