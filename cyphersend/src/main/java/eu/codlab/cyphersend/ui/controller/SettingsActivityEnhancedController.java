package eu.codlab.cyphersend.ui.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.config.controller.ConfigController;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SettingsActivityEnhancedController extends SettingsActivityController{

    public static void cleanValues(Context context){
        if(context != null) {
            String key_identifier = context.getString(R.string.identifier);
            String key_pass = context.getString(R.string.pass);
            String identifier = PreferenceManager.getDefaultSharedPreferences(context).getString(key_identifier, null);
            String pass = PreferenceManager.getDefaultSharedPreferences(context).getString(key_pass, null);

            if(identifier != null){
                ConfigController.getInstance(context).setConfig(key_identifier, identifier);
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key_identifier).commit();
            }

            if(pass != null){
                ConfigController.getInstance(context).setConfig(key_pass, pass);

                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key_pass).commit();
            }

            int _pin = context.getSharedPreferences("__INTERNAL__", 0).getInt("code", -1);
            if(_pin != -1){
                ConfigController.getInstance(context).setConfig("code",Integer.toString(_pin, 10));
                context.getSharedPreferences("__INTERNAL__",0).edit().remove("code").commit();
            }

            SharedPreferences pref = context.getSharedPreferences("Context", 0);

            String public_key = pref.getString("public", null);
            String private_key = pref.getString("private", null);
            if(public_key != null) {
                ConfigController.getInstance(context).setConfig(ConfigController.PUBLIC_KEY, public_key);
                pref.edit().remove("public").commit();
            }

            if(private_key != null) {
                ConfigController.getInstance(context).setConfig(ConfigController.PRIVATE_KEY, private_key);
                pref.edit().remove("private").commit();
            }
        }
    }


}