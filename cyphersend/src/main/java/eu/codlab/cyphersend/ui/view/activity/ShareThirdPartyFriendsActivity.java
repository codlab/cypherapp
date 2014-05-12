package eu.codlab.cyphersend.ui.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.utils.RandomStrings;
import eu.codlab.cyphersend.utils.UrlsHelper;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ShareThirdPartyFriendsActivity extends FragmentActivity implements RequestSendListener, MessageSenderListener {
    private MainActivityController _controller;
    private MainActivityDialogController _dialog_controller;
    private AlertDialog _alert;

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
        _adapter = new DeviceAdapter(this, this, DeviceAdapter.SHARE_ONLY);
        list.setAdapter(_adapter);

        processIntentView();
    }


    public void processIntentView() {

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) &&
                getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            String message = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if(!CypherMainActivity.isCallerMyself(getIntent())){
                _message = message;
            }else{
                CypherMainActivity.sendTextIntent(this, message);
                Toast.makeText(this, R.string.no_intent, Toast.LENGTH_LONG).show();
                finish();
            }
        } else if(getIntent().hasExtra("message") && getIntent().getStringExtra("message") != null){
            _message = getIntent().getStringExtra("message");
        } else {
            //requestMessage();
            finish();
        }
    }

    private void requestMessage(){
        final EditText edit_text = new EditText(this);
        _alert = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_request_send_title)
                .setMessage(R.string.dialog_request_send_message_web)
                .setView(edit_text)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _message = edit_text.getText().toString();
                        _alert = null;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        _alert = null;
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        _alert.show();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(_alert != null){
            _alert.show();
        }
    }

    @Override
    public void onPause(){
        if(_alert != null){
            _alert.cancel();
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onRequestWebSend(Device device) {

    }

    @Override
    public void onRequestSend(Device device) {
        PublicKey key = device.getPublicKey();//
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());


        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        write.encodeMessage(_message);
        //http://254.254.254.254/
        String share_string = UrlsHelper.getDecodeURL(write);
        CypherMainActivity.sendTextIntent(this, share_string);
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
