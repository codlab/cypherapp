package eu.codlab.cyphersend.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.EditText;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.ui.view.CypherMainActivity;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MainActivityDialogController {
    private Activity _view;

    private Activity getView() {
        return _view;
    }

    private Dialog _alert_dialog;

    public boolean hasDialog() {
        return _alert_dialog != null;
    }

    public Dialog getDialog() {
        return _alert_dialog;
    }

    public MainActivityDialogController(Activity view) {
        _view = view;
    }


    public void createDialogReceivedErrorNull() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_received_content_error_null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogReceivedErrorDecypher() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_received_content_error_decypher)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogReceivedNoNewMessage() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_empty)
                .setMessage(R.string.dialog_message_received_content_empty)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogSendError() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_send_title)
                .setMessage(R.string.dialog_message_send_content_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogSendOk() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_send_title)
                .setMessage(R.string.dialog_message_send_content_ok)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogReceivedMessage(String emit_by, String message) {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title)
                .setMessage(getView().getString(R.string.dialog_message_received_message)
                        .replace("A", emit_by).replace("B", message))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogRequestSend(final CypherMainActivity parent, final Device device) {
        final EditText edit_text = new EditText(parent);
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_request_send_title)
                .setMessage(R.string.dialog_request_send_message)
                .setView(edit_text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        parent.onValidateMessageSend(device, edit_text.getText().toString());
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    private void show() {
        getView().runOnUiThread(new Runnable() {
            public void run() {
                _alert_dialog.show();
            }
        });
    }

    public void onResume() {
        if (_alert_dialog != null) {
            _alert_dialog.show();
        }
    }

    public void onPause() {
        if (_alert_dialog != null) {
            _alert_dialog.dismiss();
        }
    }
}
