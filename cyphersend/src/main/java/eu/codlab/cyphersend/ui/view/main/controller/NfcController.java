package eu.codlab.cyphersend.ui.view.main.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import eu.codlab.cyphersend.ui.view.main.controller.internal.NfcCallback;

/**
 * Created by kevinleperf on 13/08/14.
 */
public class NfcController {
    private NfcControllerListener _listener;

    public interface NfcControllerListener {
        public Context getContext();

        public Uri getDefaultUri();

        public void onShared();
    }

    private NfcAdapter _adapter;
    private Context _context;

    private NfcAdapter.CreateNdefMessageCallback ndefCallback;
    private NfcAdapter.OnNdefPushCompleteCallback ndefCompleteCallback;


    private static final int MESSAGE_SENT = 1;

    private final Handler _handler;

    public NfcController(NfcControllerListener listener) {
        _context = listener.getContext();
        _listener = listener;

        _handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_SENT:
                        if (_listener != null) {
                            _listener.onShared();
                        }

                        break;
                }
            }
        };

        initAdapter();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate() {
        if (isNfcICS()) {
            ndefCallback = new NfcCallback(_listener);
            ndefCompleteCallback = new NfcAdapter.OnNdefPushCompleteCallback() {
                @Override
                public void onNdefPushComplete(NfcEvent nfcEvent) {
                    _handler.obtainMessage(MESSAGE_SENT).sendToTarget();
                }
            };
        }

    }

    private boolean isTargetNfc() {
        return Build.VERSION.SDK_INT >= 10;
    }
    private boolean isNfcEnable() {
        return _adapter != null && isTargetNfc();
    }

    private boolean isNfcICS() {
        return _adapter != null && Build.VERSION.SDK_INT >= 14;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerNfc(Activity activity) {
        if (isNfcICS()) {
            _adapter.setNdefPushMessageCallback(ndefCallback, activity);
            _adapter.setOnNdefPushCompleteCallback(ndefCompleteCallback, activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void initAdapter(){
        if (isTargetNfc()) {
            _adapter = NfcAdapter.getDefaultAdapter(_context);
        }
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void sendNfc(Activity activity) {
        if (isNfcICS()) {
            NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(_listener.getDefaultUri())});
                _adapter.setNdefPushMessage(nfcmessage, activity);
        }
    }
}
