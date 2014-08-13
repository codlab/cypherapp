package eu.codlab.cyphersend.ui.view.share.activity.internal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.ui.view.main.activity.CypherMainActivity;
import eu.codlab.cyphersend.ui.view.main.controller.MainActivityController;

/**
 * Created by kevinleperf on 13/08/14.
 */
public abstract class ShareActivityAbstract extends FragmentActivity implements RequestSendListener, MessageSenderListener {
    private MainActivityDialogController _dialog_controller;
    private AlertDialog _alert;

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }
    private MainActivityController _controller;

    private synchronized MainActivityController getController() {
        if (_controller == null) _controller = new MainActivityController();
        return _controller;
    }

    private DeviceAdapter _adapter;

    private String _message;



    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Crashlytics.start(this);
        setContentView(R.layout.activity_friends);

        ListView list = (ListView)findViewById(getContentView());
        _adapter = new DeviceAdapter(this, this, getMode());
        list.setAdapter(_adapter);

        processIntentView();
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
    public final boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }



    private void requestMessage(){
        final EditText edit_text = getEditText();
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
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert = null;
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        _alert.show();
    }

    public void processIntentView(){

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

    protected String getMessage(){
        return _message;
    }



    @Override
    public final void onSendError() {
        getDialogController().createDialogSendError();
    }

    @Override
    public final void onOk() {
        getDialogController().createDialogSendOk();
    }


    abstract protected int getMode();
    abstract protected int getContentView();
    abstract protected EditText getEditText();
}
