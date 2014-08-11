package eu.codlab.cyphersend;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.view.activity.CypherMainActivity;

/**
 * Created by kevinleperf on 01/04/2014.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private static int NOTIFICATION_ID = 424;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().containsKey("new_messages")) {
            sendNotification(context);
        }
        setResultCode(Activity.RESULT_OK);
    }

    private void sendNotification(Context context) {

        if (SettingsActivityController.getGCMAccepted(context)) {
            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(context, CypherMainActivity.class);
            intent.putExtra(MainActivityController.LOAD_WEB_MESSAGES, true);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_stat_ic_launcher)
                            .setLights(0xFFff0000, 100, 100)
                            .setContentTitle(context.getString(R.string.gcm_title))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(context.getString(R.string.gcm_message)))
                            .setContentInfo(context.getString(R.string.gcm_message))
                            .setSubText(context.getString(R.string.gcm_message))
                            .setContentText(context.getString(R.string.gcm_message))
                            .setAutoCancel(true);


            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    public static void removeNotification(Context context){
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
