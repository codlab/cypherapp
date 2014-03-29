package eu.codlab.cyphersend.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageRead;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class DecodeUrlActivity extends Activity {
    private MainActivityDialogController _dialog_controller;

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }

    private DeviceAdapter _adapter;

    private String _message;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_decode);


        processIntentView();
    }


    public void processIntentView() {

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            DevicesController controller = DevicesController.getInstance(this);
            TextView decoded = (TextView) findViewById(R.id.decoded);
            String message = getIntent().getData().toString();
            String[] split = message.split("/");
            int idx = split.length - 1;
            if (message.endsWith("/"))
                idx--;

            MessageRead read = new MessageRead("", "", split[idx]);

            String decoded_msg = read.decode(MainActivityController.getKeys(this).getPrivate());
            Log.d("signature",decoded_msg);
            String identifier = split[idx - 1];

            Device device = controller.getDeviceFromSignature(identifier, decoded_msg);

            if (device != null) {
                decoded.setText(decoded_msg);
            }else{
                decoded.setText(getString(R.string.not_suppose_to_be_this_device)+" "+decoded_msg);
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
