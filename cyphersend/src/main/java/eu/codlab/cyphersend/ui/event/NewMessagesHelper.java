package eu.codlab.cyphersend.ui.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by kevinleperf on 11/08/14.
 */
public class NewMessagesHelper {
    private static final String ACTION_NEW_MESSAGES = "ACTION_NEW_MESSAGES";
    public static interface NewMessagesHelperReceiver {
        public void onReceive();
    }

    private BroadcastReceiver _receiver;
    private Context _context;
    private NewMessagesHelperReceiver _listener;

    public NewMessagesHelper(Context context, NewMessagesHelperReceiver listener){
        _context = context;
        _listener = listener;

        _receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(_listener != null)_listener.onReceive();
                register();
            }
        };
    }

    public void register(){
        LocalBroadcastManager.getInstance(_context)
                .registerReceiver(_receiver, new IntentFilter(ACTION_NEW_MESSAGES));
    }

    public void unregister(){
        LocalBroadcastManager.getInstance(_context).unregisterReceiver(_receiver);
    }

    public static void sendEvent(Context context) {
        Intent intent = new Intent(ACTION_NEW_MESSAGES);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
