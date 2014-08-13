package eu.codlab.cyphersend.ui.view.decode.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.controller.DevicesController;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.messages.model.MessageRead;
import eu.codlab.cyphersend.messages.model.content.MessageContent;
import eu.codlab.cyphersend.messages.model.content.MessageString;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.view.main.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;

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
        Crashlytics.start(this);
        setContentView(R.layout.activity_decode);


        processIntentView();
    }


    public void processIntentView() {

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            DevicesController controller = DevicesController.getInstance(this);
            View v = findViewById(R.id.incognito_layer);
            if (v != null)
                v.setVisibility(View.GONE);

            TextView decoded = (TextView) findViewById(R.id.decoded);
            String message = getIntent().getData().toString();
            String[] split = message.split("/");
            int idx = split.length - 1;
            if (message.endsWith("/"))
                idx--;

            MessageRead read = new MessageRead("", "", split[idx], true);

            MessageContent decoded_msg = read.decode(MainActivityController.getKeys(this).getPrivate());

            if (decoded_msg == null) {
                decoded.setText(R.string.decode_error_maybe_invalid);
            } else {
                String msg = "";

                if (decoded_msg instanceof MessageString) {
                    msg = ((MessageString) decoded_msg).getMessage();
                }

                String identifier = split[idx - 1];

                Device device = controller.getDeviceFromSignature(identifier, msg);

                if (device != null) {
                    decoded.setText(getString(R.string.known_sender).replace("%s", device.getName()) + " " + msg);
                } else if(msg != null) {
                    decoded.setText(getString(R.string.unknown_sender) + " " + msg);
                } else {
                    decoded.setText(getString(R.string.not_suppose_to_be_this_device) + " " + msg);
                }
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
