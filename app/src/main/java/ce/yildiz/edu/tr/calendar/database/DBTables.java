package ce.yildiz.edu.tr.calendar.database;

public class DBTables {

    //Constants for identifying table and columns

    // Event Table
    public static final String EVENT_TABLE_NAME = "EVENT";

    public static final String EVENT_ID = "id";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_ALL_DAY = "allDay";
    public static final String EVENT_DATE = "date";
    public static final String EVENT_MONTH = "month";
    public static final String EVENT_YEAR = "year";
    public static final String EVENT_TIME = "time";
    public static final String EVENT_DURATION = "duration";
    public static final String EVENT_NOTIFY = "notify";
    public static final String EVENT_REPETITION = "repetition";
    public static final String EVENT_NOTE = "note";
    public static final String EVENT_COLOR = "color";
    public static final String EVENT_LOCATION = "location";
    public static final String EVENT_PHONE_NUMBER = "number";
    public static final String EVENT_MAIL = "mail";

    // Notification Table
    public static final String NOTIFICATION_TABLE_NAME = "NOTIFICATION";

    public static final String NOTIFICATION_ID = "id";
    public static final String NOTIFICATION_EVENT_ID = "event_id";
    public static final String NOTIFICATION_TIME = "time";
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";

}
