package eu.codlab.cyphersend.ui.view.main.controller.internal;

import android.annotation.TargetApi;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;

import eu.codlab.cyphersend.ui.view.main.controller.NfcController;

/**
 * Created by kevinleperf on 13/08/14.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NfcCallback implements NfcAdapter.CreateNdefMessageCallback {
    private NfcController.NfcControllerListener _listener;

    public NfcCallback(NfcController.NfcControllerListener listener) {
        _listener = listener;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(_listener.getDefaultUri())});
        return nfcmessage;

    }
}
