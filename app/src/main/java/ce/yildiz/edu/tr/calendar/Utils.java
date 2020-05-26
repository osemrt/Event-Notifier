package ce.yildiz.edu.tr.calendar;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final int MAX_CALENDAR_DAYS = 42;

    public static final String DAILY = "Repeat Daily";
    public static final String WEEKLY = "Repeat Weekly";
    public static final String MONTHLY = "Repeat Monthly";
    public static final String YEARLY = "Repeat Yearly";

    public static String CURRENT_FILTER = "Today";
    public static final String TODAY = "Today";
    public static final String NEXT_7_DAYS = "Next 7 days";
    public static final String NEXT_30_DAYS = "Next 30 days";
    public static final String THIS_YEAR = "This Year";

    public enum NotificationPreference {
        TEN_MINUTES_BEFORE,
        ONE_HOUR_BEFORE,
        ONE_DAY_BEFORE,
        AT_THE_TIME_OF_EVENT
    }

    public enum AppTheme {
        INDIGO,
        DARK,
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    public static Date convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return aDate;
    }

    public static Date convertStringToTime(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aDate;
    }

    @SuppressLint("ResourceType")
    public static ArrayList<String> getColors(Context context) {
        ArrayList<String> colors = new ArrayList<>();
        colors.add(context.getResources().getString(R.color.darkIndigo));
        colors.add(context.getResources().getString(R.color.yellow));
        colors.add(context.getResources().getString(R.color.deepPurple));
        colors.add(context.getResources().getString(R.color.pink));
        colors.add(context.getResources().getString(R.color.Grey));
        colors.add(context.getResources().getString(R.color.cyan));
        colors.add(context.getResources().getString(R.color.green));
        colors.add(context.getResources().getString(R.color.lime));
        colors.add(context.getResources().getString(R.color.lightIndigo));
        colors.add(context.getResources().getString(R.color.black));
        colors.add(context.getResources().getString(R.color.color9));
        colors.add(context.getResources().getString(R.color.lite_blue));
        colors.add(context.getResources().getString(R.color.red));
        colors.add(context.getResources().getString(R.color.brown));
        colors.add(context.getResources().getString(R.color.color11));
        return colors;
    }

}
