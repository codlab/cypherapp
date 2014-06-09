package eu.codlab.cyphersend.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.ui.view.activity.CypherMainActivity;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class DiscutionDialogController {
    private static final Integer NODIALOG = new Integer(0);
    private static final Integer DIALOG = new Integer(1);
    private Integer __instance = NODIALOG;
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

    public DiscutionDialogController(Activity view) {
        _view = view;
    }


    public void createDialogReceivedErrorNull() {
        __instance = NODIALOG;
        Crouton.makeText(_view, R.string.dialog_message_received_content_error_null, Application.ERROR_LONG).show();
        /*_alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_received_content_error_null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();*/
        show();
    }

    public void createDialogReceivedErrorDecypher() {
        __instance = NODIALOG;
        Crouton.makeText(_view, R.string.dialog_message_received_content_error_decypher, Application.ERROR_LONG).show();
        /*_alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_received_content_error_decypher)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();*/
        show();
    }

    public void createDialogReceivedNoNewMessage() {
        __instance = NODIALOG;
        Crouton.makeText(_view, R.string.dialog_message_received_content_empty, Application.OK_SHORT).show();
        /*_alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_empty)
                .setMessage(R.string.dialog_message_received_content_empty)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();*/
        show();
    }

    public void createDialogSendError() {
        __instance = NODIALOG;
        Crouton.makeText(_view, R.string.dialog_message_send_content_error, Application.ERROR_LONG).show();
        /*_alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_send_title)
                .setMessage(R.string.dialog_message_send_content_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();*/
        show();
    }

    public void createDialogSendOk() {
        __instance = NODIALOG;
        Crouton.makeText(_view, R.string.dialog_message_send_content_ok, Application.OK_SHORT).show();
        /*_alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_send_title)
                .setMessage(R.string.dialog_message_send_content_ok)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();*/
        show();
    }

    public void createDialogReceivedMessage(String emit_by, String message) {
        __instance = DIALOG;
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title)
                .setMessage(getView().getString(R.string.dialog_message_received_message)
                        .replace("A", emit_by).replace("B", message))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        __instance = NODIALOG;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    private void show() {
        getView().runOnUiThread(new Runnable() {
            public void run() {
                if(_alert_dialog != null)
                    _alert_dialog.show();
            }
        });
    }

    public void onResume() {
        if(__instance == DIALOG){

        }
        if (_alert_dialog != null) {
            _alert_dialog.show();
        }
    }

    public void onPause() {
        Crouton.cancelAllCroutons();
        if (_alert_dialog != null) {
            _alert_dialog.dismiss();
        }
    }
}