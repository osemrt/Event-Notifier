package ce.yildiz.edu.tr.calendar.other;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.views.MainActivity;

public class AlarmService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private Bundle bundle;
    private String eventTitle;
    private String eventNote;
    private String interval;
    private String eventTimeStamp;
    private int notificationId;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bundle = intent.getExtras();
        eventTitle = bundle.getString("eventTitle");
        eventNote = bundle.getString("eventNote");
        eventTimeStamp = bundle.getString("eventTimeStamp");
        interval = bundle.getString("interval");
        notificationId = bundle.getInt("notificationId");

        showNotification();
        setNewAlarm();

        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, activityIntent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("0", "Event Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Get notifications about your events");
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new Notification.Builder(this, "0")
                .setContentTitle(eventTitle)
                .setContentText(eventNote)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setGroup("Group_calendar_view")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .build();

//        Notification notification = new Notification.Builder(this, "0")
//                .setSmallIcon(R.drawable.ic_notify)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentTitle(eventTitle)
//                .setContentText(eventNote)
//                .setDeleteIntent(pendingIntent)
//                .setColor(getResources().getColor(R.color.darkIndigo))
//                .setGroup("Group_calendar_view")
//                .build();

        Log.d("APP_TEST", "setNewAlarm: Alarm at " + eventNote + "\n" + eventTimeStamp);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }

    private void setNewAlarm() {
        Intent intent = new Intent(this, ServiceAutoLauncher.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        long triggerAtMillis = getNextEventTriggerMillis();
        if (triggerAtMillis != 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
            Log.d("APP_TEST", "repeatingAlarm: Alarm at " + Long.toString(triggerAtMillis));
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }


    }

    private long getNextEventTriggerMillis() {
        Calendar calendar = Calendar.getInstance();
        if (interval.equals(getString(R.string.daily))) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (interval.equals(getString(R.string.weekly))) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } else if (interval.equals(getString(R.string.monthly))) {
            return getNextMonthMillis();
        } else if (interval.equals(getString(R.string.yearly))) {
            calendar.add(Calendar.YEAR, 1);
        } else {
            return 0;
        }

        return calendar.getTimeInMillis();

    }

    private long getNextMonthMillis() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        currentMonth++;

        if (currentMonth > Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY;
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }

        cal.set(Calendar.MONTH, currentMonth);
        int maximumDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, maximumDay);
        return cal.getTimeInMillis();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
