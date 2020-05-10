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

    public static enum Period {
        TODAY,
        NEXT_7_DAYS,
        NEXT_30_DAYS,
        ALL_EVENTS
    }

    public static final int MAX_CALENDAR_DAYS = 42;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

    public static Date convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
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
        colors.add(context.getResources().getString(R.color.white));
        colors.add(context.getResources().getString(R.color.lite_blue));
        colors.add(context.getResources().getString(R.color.red));
        colors.add(context.getResources().getString(R.color.brown));
        colors.add(context.getResources().getString(R.color.color11));
        return colors;
    }

}
