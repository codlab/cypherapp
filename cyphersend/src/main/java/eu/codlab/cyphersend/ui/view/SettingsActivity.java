package eu.codlab.cyphersend.ui.view;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.widget.EditText;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.settings.controller.ServerForceRegister;
import eu.codlab.cyphersend.settings.controller.ServerRegister;
import eu.codlab.cyphersend.settings.listener.ServerForceRegisterListener;
import eu.codlab.cyphersend.settings.listener.ServerRegisterListener;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityDialogController;
import eu.codlab.cyphersend.utils.keys.KeyManager;

public class SettingsActivity extends PreferenceActivity
        implements ServerRegisterListener, ServerForceRegisterListener {

    private SettingsActivityController _controller;

    private SettingsActivityController getController() {
        if (_controller == null) _controller = new SettingsActivityController();
        return _controller;
    }

    private SettingsActivityDialogController _dialog_controller;

    private SettingsActivityDialogController getDialogController() {
        if (_dialog_controller == null)
            _dialog_controller = new SettingsActivityDialogController(this);
        return _dialog_controller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        if (Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        Preference server_register = this.findPreference(getString(R.string.register));
        if (server_register != null) {
            server_register.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String identifier = Base64Coder.encodeString(SettingsActivityController.getDeviceIdentifier(SettingsActivity.this));

                    String pass = Base64Coder.encodeString(SettingsActivityController.getDevicePass(SettingsActivity.this));
                    String website = SettingsActivityController.getDeviceURL(SettingsActivity.this);
                    ServerRegister register = new ServerRegister(SettingsActivity.this, website, identifier, pass);
                    register.send();
                    return true;
                }
            });
        }
        Preference server_force_register = this.findPreference(getString(R.string.register_force));
        if (server_force_register != null) {
            server_force_register.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String identifier = Base64Coder.encodeString(SettingsActivityController.resetDeviceIdentifier(SettingsActivity.this));
                    String pass = Base64Coder.encodeString(SettingsActivityController.resetDevicePass(SettingsActivity.this));
                    String website = SettingsActivityController.getDeviceURL(SettingsActivity.this);
                    ServerForceRegister register = new ServerForceRegister(SettingsActivity.this, website, identifier, pass);
                    register.send();
                    return true;
                }
            });
        }

        Preference keys_export = this.findPreference(getString(R.string.keys_export));
        if (keys_export != null) {
            keys_export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final EditText edit = new EditText(SettingsActivity.this);
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            KeyManager manager = new KeyManager(SettingsActivity.this);

                            String name = SettingsActivityController.getDeviceName(SettingsActivity.this);
                            String identifier = SettingsActivityController.getDeviceIdentifier(SettingsActivity.this);
                            String pass = SettingsActivityController.getDevicePass(SettingsActivity.this);
                            String website = SettingsActivityController.getDeviceURL(SettingsActivity.this);

                            manager.exportKeys(MainActivityController.getKeys(SettingsActivity.this).getPublic(),
                                    MainActivityController.getKeys(SettingsActivity.this).getPrivate(),
                                    name, identifier, pass, website,
                                    edit.getText().toString());
                            dialog.dismiss();
                        }
                    };
                    createInputPassword(R.string.keys_input_export, edit, listener);

                    return true;
                }
            });
        }

        Preference keys_import = this.findPreference(getString(R.string.keys_import));
        if (keys_import != null) {
            keys_import.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final EditText edit = new EditText(SettingsActivity.this);
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            KeyManager manager = new KeyManager(SettingsActivity.this);
                            boolean result = manager.importKeys(edit.getText().toString());
                            if (result == true) {
                                CypherRSA.saveKeyPublicPrivate(SettingsActivity.this,
                                        manager.getPublicKey(),
                                        manager.getPrivateKey());
                                String name = manager.getName();
                                String identifier = manager.getIdentifier();
                                String pass = manager.getPass();
                                String website = manager.getWebsite();

                                SettingsActivityController.setDeviceName(SettingsActivity.this, name);
                                SettingsActivityController.setDeviceIdentifier(SettingsActivity.this, identifier);
                                SettingsActivityController.setDevicePass(SettingsActivity.this, pass);
                                SettingsActivityController.setDeviceUrl(SettingsActivity.this, website);
                                createDestroyDialog();

                            } else {
                                createAlertDialog(R.string.keys_import_error);
                            }
                            dialog.dismiss();
                        }
                    };
                    createInputPassword(R.string.keys_input_import, edit, listener);
                    return true;
                }
            });
        }
    }

    private void createAlertDialog(int error_message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle(getString(R.string.error))
                .setMessage(getString(error_message))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void createDestroyDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(getString(R.string.restart_app_title))
                .setMessage(getString(R.string.restart_app_message))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlarmManager alm = (AlarmManager) SettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
                                alm.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, PendingIntent.getActivity(SettingsActivity.this, 0, new Intent(SettingsActivity.this, CypherMainActivity.class), 0));
                                System.exit(0);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void createInputPassword(int title, final EditText edittext, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(edittext);
        alertDialogBuilder
                .setTitle(getString(title))
                .setCancelable(false)
                .setPositiveButton("OK",
                        listener)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onRegisterOk() {
        getDialogController().createDialogServerRegisterOk();
    }

    @Override
    public void onRegisterKo() {
        getDialogController().createDialogServerRegisterError();
    }

    @Override
    public void onRegisterTimeout() {
        getDialogController().createDialogServerRegisterErrorNull();
    }

    @Override
    public void onForceRegisterOk() {
        getDialogController().createDialogServerForceRegisterOk();
    }

    @Override
    public void onForceRegisterKo() {
        getDialogController().createDialogServerForceRegisterKo();
    }
}
