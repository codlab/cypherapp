package eu.codlab.cyphersend.ui.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.ui.view.activity.SettingsActivity;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class SettingsActivityDialogController {
    private SettingsActivity _view;

    private SettingsActivity getView() {
        return _view;
    }

    private Dialog _alert_dialog;

    public boolean hasDialog() {
        return _alert_dialog != null;
    }

    public Dialog getDialog() {
        return _alert_dialog;
    }

    public SettingsActivityDialogController(SettingsActivity view) {
        _view = view;
    }


    public void createDialogServerRegisterOk() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_register_message_ok)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogServerRegisterError() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_register_message_ko)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogServerRegisterErrorNull() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title_error)
                .setMessage(R.string.dialog_message_register_message_timeout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogServerForceRegisterOk() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_force_title)
                .setMessage(R.string.dialog_message_force_message_ok)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogServerForceRegisterKo() {
        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_force_title)
                .setMessage(R.string.dialog_message_force_message_ko)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
