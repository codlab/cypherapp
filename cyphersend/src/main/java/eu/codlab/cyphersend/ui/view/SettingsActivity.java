package eu.codlab.cyphersend.ui.view;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.settings.controller.ServerForceRegister;
import eu.codlab.cyphersend.settings.controller.ServerRegister;
import eu.codlab.cyphersend.settings.listener.ServerForceRegisterListener;
import eu.codlab.cyphersend.settings.listener.ServerRegisterListener;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityDialogController;

public class SettingsActivity extends PreferenceActivity
implements ServerRegisterListener,ServerForceRegisterListener {

    private SettingsActivityController _controller;
    private SettingsActivityController getController(){
        if(_controller == null)_controller = new SettingsActivityController();
        return _controller;
    }

    private SettingsActivityDialogController _dialog_controller;
    private SettingsActivityDialogController getDialogController(){
        if(_dialog_controller == null)_dialog_controller = new SettingsActivityDialogController(this);
        return _dialog_controller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        Preference server_register = this.findPreference(getString(R.string.register));
        if(server_register != null){
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
        if(server_force_register != null){
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
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
