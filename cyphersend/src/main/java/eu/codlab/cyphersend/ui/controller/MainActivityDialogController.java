package eu.codlab.cyphersend.ui.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.ui.view.activity.CypherMainActivity;

/**
 * Created by kevinleperf on 29/06/13.
 */
public class MainActivityDialogController {
    private static final Integer NODIALOG = new Integer(0);
    private static final Integer DIALOG = new Integer(1);
    private static Integer __instance = NODIALOG;
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

    public void createDialogReceivedMessage(String emit_by, String message, boolean incognito) {
        __instance = DIALOG;

        View v = ((LayoutInflater)_view.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.activity_decode, null, false);
        View incognito_disabled_image = v.findViewById(R.id.incognito_disabled);
        View incognito_disabled_text = v.findViewById(R.id.incognito_disabled_text);
        View incognito_enabled_image = v.findViewById(R.id.incognito_enabled);
        View incognito_enabled_text = v.findViewById(R.id.incognito_text);
        if(incognito){
            incognito_disabled_image.setVisibility(View.GONE);
            incognito_disabled_text.setVisibility(View.GONE);
            incognito_enabled_image.setVisibility(View.VISIBLE);
            incognito_enabled_text.setVisibility(View.VISIBLE);
        }else{
            incognito_disabled_image.setVisibility(View.VISIBLE);
            incognito_disabled_text.setVisibility(View.VISIBLE);
            incognito_enabled_image.setVisibility(View.GONE);
            incognito_enabled_text.setVisibility(View.GONE);
        }

        TextView content = (TextView) v.findViewById(R.id.decoded);
        content.setText(getView().getString(R.string.dialog_message_received_message)
                .replace("A", emit_by).replace("B", message));

        _alert_dialog = new AlertDialog.Builder(getView())
                .setTitle(R.string.dialog_message_received_title)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _alert_dialog = null;
                        __instance = NODIALOG;
                        dialog.dismiss();
                    }
                }).create();
        show();
    }

    public void createDialogRequestSend(final CypherMainActivity parent, final Device device, final boolean use_web_service) {
        parent.createDialogRequestSend(parent, device, use_web_service);
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
