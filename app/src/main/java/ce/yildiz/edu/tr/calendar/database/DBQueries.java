package ce.yildiz.edu.tr.calendar.database;

public class DBQueries {

    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + DBTables.EVENT_TABLE_NAME + " (" + DBTables.EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBTables.EVENT_TITLE + " TEXT, "
                    + DBTables.EVENT_ALL_DAY + " TIME, "
                    + DBTables.EVENT_DATE + " TEXT, "
                    + DBTables.EVENT_TIME + " TEXT, "
                    + DBTables.EVENT_MONTH + " TEXT, "
                    + DBTables.EVENT_YEAR + " TEXT,"
                    + DBTables.EVENT_DURATION + " TEXT,"
                    + DBTables.EVENT_NOTIFY + " TEXT,"
                    + DBTables.EVENT_REPETITION + " TEXT,"
                    + DBTables.EVENT_NOTE + " TEXT,"
                    + DBTables.EVENT_COLOR + " INTEGER,"
                    + DBTables.EVENT_LOCATION + " TEXT,"
                    + DBTables.EVENT_PHONE_NUMBER + " TEXT,"
                    + DBTables.EVENT_MAIL + " TEXT)";

    public static final String CREATE_NOTIFICATION_TABLE =
            "CREATE TABLE " + DBTables.NOTIFICATION_TABLE_NAME + " (" + DBTables.NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBTables.NOTIFICATION_EVENT_ID + " INTEGER, "
                    + DBTables.NOTIFICATION_TIME + " TEXT, "
                    + DBTables.NOTIFICATION_CHANNEL_ID + " INTEGER)";

    public static final String DROP_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.EVENT_TABLE_NAME;

    public static final String DROP_NOTIFICATION_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.NOTIFICATION_TABLE_NAME;

}
