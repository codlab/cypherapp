package eu.codlab.cyphersend.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ShareFriendsActivity extends FragmentActivity implements RequestSendListener, MessageSenderListener {
    private MainActivityDialogController _dialog_controller;

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }

    private DeviceAdapter _adapter;

    private String _message;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_friends);

        ListView list = (ListView)findViewById(R.id.main_friends_list);
        _adapter = new DeviceAdapter(this, this);
        list.setAdapter(_adapter);

        processIntentView();
    }


    public void processIntentView(){

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) &&
                getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            String message = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            _message = message;
        }else{
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return false;
    }

    @Override
    public void onRequestSend(Device device){
        String idSender = Base64Coder.encodeString(SettingsActivityController.getDeviceIdentifier(this));
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());
        Log.d("device key", device.getPublic());
        PublicKey key = device.getPublicKey();//
        //PrivateKey pri = MainActivityController.getKeys(this).getPrivate();
        Class c = key.getClass();
        Log.d("name", c.getName());
        // MainActivityController.getKeys(this).getPublic();

        String message_encoded = new String(Base64Coder.encode(CypherRSA.encrypt(_message, key)));
        //String message_decoded = CypherRSA.decrypt(Base64Coder.decode(message_encoded), pri);
        Log.d("original sender", idSender);
        Log.d("original receiver", idReceiver);
        Log.d("original message", _message);
        Log.d("encoded message", message_encoded);
        //Log.d("decoded message",message_decoded);

        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        MessageSender sender = new MessageSender(this, device.getWebsite(), write, _message);
        sender.send();
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
