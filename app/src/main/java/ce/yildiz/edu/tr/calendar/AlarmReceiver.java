package ce.yildiz.edu.tr.calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventTitle = intent.getStringExtra("eventTitle");
        String time = intent.getStringExtra("time");
        int notificationId = intent.getIntExtra("notificationId", 0);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "channel_id";
        CharSequence channel_name = "channel_name";
        String description = "channel_description";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channel_name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification = new Notification.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(eventTitle)
                .setContentText(time)
                .setDeleteIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.darkIndigo))
                .setGroup("Group_calendar_view")
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);
    }
}
