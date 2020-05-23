package ce.yildiz.edu.tr.calendar.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceAutoLauncher extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mIntent = new Intent(context, AlarmService.class);
        mIntent.putExtras(intent.getExtras());
        Log.d("APP_TEST", "onReceive: " + "Calling AlarmService...");
        context.startService(mIntent);
    }
}
