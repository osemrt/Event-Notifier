package ce.yildiz.edu.tr.calendar.other;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import ce.yildiz.edu.tr.calendar.R;
import ce.yildiz.edu.tr.calendar.views.MainActivity;

public class AlarmService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private Bundle bundle;
    private String eventTitle;
    private String eventNote;
    private int eventColor;
    private String interval;
    private String eventTimeStamp;
    private int notificationId;
    private String soundName;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        if (intent != null) {
            bundle = intent.getExtras();
            eventTitle = bundle.getString("eventTitle", "No title");
            eventNote = bundle.getString("eventNote", "No note");
            eventColor = bundle.getInt("eventColor", -49920);
            eventTimeStamp = bundle.getString("eventTimeStamp", "No Timestamp");
            interval = bundle.getString("interval", "");
            notificationId = bundle.getInt("notificationId", 0);
            soundName = bundle.getString("soundName", "Consequence");


            showNotification();
            setNewAlarm();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {
        // Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + getSoundResourceId(soundName));
        Ringtone r = RingtoneManager.getRingtone(this, ringtoneUri);
        r.play();

        boolean vibrate = true;
        long[] vibratePattern = new long[]{0L, 1000L};

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "0");
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        grantUriPermission("com.android.systemui", ringtoneUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, activityIntent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel mChannel = new NotificationChannel("0", "Event Notification", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(eventNote);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setLightColor(eventColor);
            mChannel.setSound(ringtoneUri, att);
            mChannel.setBypassDnd(true);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mChannel.setShowBadge(true);

            if (mNotifyManager != null) {
                mNotifyManager.createNotificationChannel(mChannel);
            }

            notificationBuilder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setColor(eventColor)
                    .setContentTitle(eventTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(eventNote))
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

        } else {
            notificationBuilder.setContentTitle(eventTitle)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setVibrate(vibratePattern)
                    .setSound(ringtoneUri)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(eventNote))
                    .setColor(eventColor)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent);

        }

        notificationBuilder.setContentText(eventNote);

        if (mNotifyManager != null) {
            mNotifyManager.notify(notificationId, notificationBuilder.build());
        }

        Log.d(TAG, "showNotification: End");
    }

    private void setNewAlarm() {
        Intent intent = new Intent(this, ServiceAutoLauncher.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        long triggerAtMillis = getNextEventTriggerMillis();
        if (triggerAtMillis != 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
            Log.d("APP_TEST", "repeatingAlarm: Alarm at " + Long.toString(triggerAtMillis));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
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

    private int getSoundResourceId(String soundName) {
        switch (soundName) {
            case "consequence":
                return R.raw.consequence;
            case "Juntos":
                return R.raw.juntos;
            case "Piece of cake":
                return R.raw.piece_of_cake;
            case "Point blank":
                return R.raw.point_blank;
            case "Slow spring board":
                return R.raw.slow_spring_board;
        }

        return R.raw.consequence;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
