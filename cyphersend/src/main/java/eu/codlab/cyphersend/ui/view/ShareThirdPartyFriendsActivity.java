package eu.codlab.cyphersend.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.utils.MD5;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ShareThirdPartyFriendsActivity extends Activity implements RequestSendListener, MessageSenderListener {
    private MainActivityController _controller;
    private MainActivityDialogController _dialog_controller;

    private synchronized MainActivityController getController() {
        if (_controller == null) _controller = new MainActivityController();
        return _controller;
    }

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }

    private DeviceAdapter _adapter;

    private String _message;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_share_cyphered);

        ListView list = (ListView) findViewById(R.id.main_friends_list);
        _adapter = new DeviceAdapter(this, this);
        list.setAdapter(_adapter);

        processIntentView();
    }


    public void processIntentView() {

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) &&
                getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            String message = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            _message = message;
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onRequestSend(Device device) {
        PublicKey key = device.getPublicKey();//
        String message_encoded = new String(Base64Coder.encode(CypherRSA.encrypt(_message, key)));

        String idSender = new String(Base64Coder.encode(CypherRSA.encrypt(MD5.encode(_message), MainActivityController.getKeys(this).getPrivate())));
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());
        Log.d("device key", device.getPublic());
        //PrivateKey pri = MainActivityController.getKeys(this).getPrivate();
        Class c = key.getClass();
        Log.d("name", c.getName());
        // MainActivityController.getKeys(this).getPublic();


        String share_string = "http://254.254.254.254/decode/"+idSender+"/"+message_encoded;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.personnal_data_subject_third_party));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, share_string);
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
    }

    @Override
    public void onSendError() {
        getDialogController().createDialogSendError();
    }

    @Override
    public void onOk() {
        getDialogController().createDialogSendOk();
    }
}
