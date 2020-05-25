package ce.yildiz.edu.tr.calendar.database;

public class DBQueries {

    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + DBTables.EVENT_TABLE_NAME + " (" + DBTables.EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBTables.EVENT_TITLE + " TEXT, "
                    + DBTables.EVENT_IS_ALL_DAY + " INT, "
                    + DBTables.EVENT_DATE + " TEXT, "
                    + DBTables.EVENT_MONTH + " TEXT, "
                    + DBTables.EVENT_YEAR + " TEXT,"
                    + DBTables.EVENT_TIME + " TEXT, "
                    + DBTables.EVENT_DURATION + " TEXT,"
                    + DBTables.EVENT_IS_NOTIFY + " INT,"
                    + DBTables.EVENT_IS_RECURRING + " INT,"
                    + DBTables.EVENT_NOTE + " TEXT,"
                    + DBTables.EVENT_COLOR + " INTEGER,"
                    + DBTables.EVENT_LOCATION + " TEXT,"
                    + DBTables.EVENT_PHONE_NUMBER + " TEXT,"
                    + DBTables.EVENT_MAIL + " TEXT,"
                    + DBTables.EVENT_PARENT_ID + " TEXT)";

    public static final String CREATE_RECURRING_PATTERN_TABLE =
            "CREATE TABLE " + DBTables.RECURRING_PATTERN_TABLE_NAME + " (" + DBTables.RECURRING_PATTERN_EVENT_ID + " INTEGER PRIMARY KEY, "
                    + DBTables.RECURRING_PATTERN_TYPE + " TEXT, "
                    + DBTables.RECURRING_PATTERN_MONTH_OF_YEAR + " TEXT, "
                    + DBTables.RECURRING_PATTERN_DAY_OF_MONTH + " TEXT, "
                    + DBTables.RECURRING_PATTERN_DAY_OF_WEEK + " TEXT)";

    public static final String CREATE_EVENT_INSTANCE_EXCEPTION_TABLE =
            "CREATE TABLE " + DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME + " (" + DBTables.EVENT_INSTANCE_EXCEPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_ID + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_IS_CHANGED + " INT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_IS_CANCELED + " INT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TITLE + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_ALL_DAY + " INT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DATE + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_MONTH + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_YEAR + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_TIME + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_DURATION + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_NOTIFY + " INT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_IS_RECURRING + " INT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_NOTE + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_COLOR + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_LOCATION + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_PHONE_NUMBER + " TEXT, "
                    + DBTables.EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_MAIL + " TEXT)";

    public static final String CREATE_NOTIFICATION_TABLE =
            "CREATE TABLE " + DBTables.NOTIFICATION_TABLE_NAME + " (" + DBTables.NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBTables.NOTIFICATION_EVENT_ID + " INTEGER, "
                    + DBTables.NOTIFICATION_TIME + " TEXT, "
                    + DBTables.NOTIFICATION_CHANNEL_ID + " INTEGER)";

    public static final String DROP_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.EVENT_TABLE_NAME;

    public static final String DROP_RECURRING_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.RECURRING_PATTERN_TABLE_NAME;

    public static final String DROP_EVENT_INSTANCE_EXCEPTION_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.EVENT_INSTANCE_EXCEPTION_TABLE_NAME;

    public static final String DROP_NOTIFICATION_TABLE =
            "DROP TABLE IF EXISTS " + DBTables.NOTIFICATION_TABLE_NAME;

}
