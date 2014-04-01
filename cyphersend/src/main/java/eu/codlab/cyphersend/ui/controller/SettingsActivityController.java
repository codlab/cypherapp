package eu.codlab.cyphersend.ui.controller;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.KeyPair;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.view.CypherMainActivity;
import eu.codlab.cyphersend.ui.view.MainDefaultFragment;
import eu.codlab.cyphersend.ui.view.MainFriendsFragment;
import eu.codlab.cyphersend.ui.view.SettingsActivity;
import eu.codlab.cyphersend.utils.RandomStrings;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SettingsActivityController {
    public static String getDeviceName(SettingsActivity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.device), "device");
    }

    public static String getDeviceURL(SettingsActivity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.website), "https://cypher.codlab.eu/");
    }

    public static String getDeviceIdentifier(SettingsActivity activity) {
        String identifier = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.identifier), null);
        if (identifier == null) {
            identifier = createRandomIdentifier(activity, 256);
        }
        return identifier;
    }
    public static String getDevicePass(SettingsActivity activity) {
        String pass = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.pass), null);
        if (pass == null) {
            pass = createRandomPass(activity, 256);
        }
        return pass;
    }

    public static String resetDeviceIdentifier(SettingsActivity activity) {
        String identifier = createRandomIdentifier(activity, 256);
        return identifier;
    }
    public static String resetDevicePass(SettingsActivity activity) {
        String pass = createRandomPass(activity, 256);
        return pass;
    }

    public static String createRandomIdentifier(SettingsActivity activity, int length){
        String identifier = RandomStrings.generate(256);
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(R.string.identifier), identifier).commit();
        return identifier;
    }

    public static String createRandomPass(SettingsActivity activity, int length){
        String pass = RandomStrings.generate(256);
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(R.string.pass), pass).commit();
        return pass;
    }

}