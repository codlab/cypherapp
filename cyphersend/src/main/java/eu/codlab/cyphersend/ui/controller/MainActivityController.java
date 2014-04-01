package eu.codlab.cyphersend.ui.controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.KeyPair;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.view.CypherMainActivity;
import eu.codlab.cyphersend.ui.view.MainDefaultFragment;
import eu.codlab.cyphersend.ui.view.MainFriendsFragment;
import eu.codlab.cyphersend.ui.view.MainHelpFragment;
import eu.codlab.cyphersend.utils.RandomStrings;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainActivityController {
    private MainDefaultFragment _default_fragment;
    private MainFriendsFragment _friends_fragment;
    private MainHelpFragment _help_fragment;

    private Fragment getDefaultFragment(){
        if(_default_fragment == null)_default_fragment = new MainDefaultFragment();
        return _default_fragment;
    }

    private Fragment getFriendsFragment(){
        if(_friends_fragment == null)_friends_fragment = new MainFriendsFragment();
        return _friends_fragment;
    }

    private Fragment getHelpFragment(){
        if(_help_fragment == null)_help_fragment = new MainHelpFragment();
        return _help_fragment;
    }

    public Fragment getFragment(int id){
        switch(id){
            case 1:
                return getFriendsFragment();
            case 2:
                return getHelpFragment();
            default:
                return getDefaultFragment();
        }
    }


    private static KeyPair _keys;

    public static String getDeviceName(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.device), "device");
    }

    public static String getDeviceURL(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.website), "https://cypher.codlab.eu/");
    }

    public static String getDeviceIdentifier(Activity activity) {
        String identifier = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.identifier), null);
        if (identifier == null) {
            identifier = RandomStrings.generate(256);
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(R.string.identifier), identifier).commit();
        }
        return identifier;
    }

    public static String getDevicePass(Activity activity) {
        String identifier = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.pass), null);
        if (identifier == null) {
            identifier = RandomStrings.generate(256);
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(R.string.pass), identifier).commit();
        }
        return identifier;
    }

    public static String getDevicePublicKey(Activity activity) {
        return new String(Base64Coder.encode(CypherRSA.exportPublicKey(getKeys(activity).getPublic())));
    }

    public Uri createUri(Activity activity) {
        return Uri.parse(createUriString(activity));
    }

    public String createUriString(Activity activity){
        return "http://254.254.254.254/me/"
                + Base64Coder.encodeString(getDeviceName(activity))
                + "/"
                + Base64Coder.encodeString(getDeviceURL(activity))
                + "/"
                + Base64Coder.encodeString(getDeviceIdentifier(activity))
                + "/"
                + getDevicePublicKey(activity)
                + "/";
    }

    public boolean onNewUri(Context context, Uri uri){
        if(uri != null){
            Log.d("path", uri.getPath());
            String [] splitted = uri.getPath().split("/");
            if(splitted.length >= 6){
                String device_name = Base64Coder.decodeString(splitted[2]);
                String device_url = Base64Coder.decodeString(splitted[3]);
                String device_identifier = Base64Coder.decodeString(splitted[4]);
                String public_key = splitted[5];

                if(DevicesController.getInstance(context).hasDevice(device_name)){
                    return false;
                }else{
                    DevicesController.getInstance(context).addDevice(device_name, device_identifier, public_key, device_url);
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean hasKeys(){
        return _keys != null;
    }
    public static KeyPair getKeys(Activity activity) {
        if (_keys == null) {
            if (!CypherRSA.areKeysPresent(activity)) {
                _keys = CypherRSA.generateKey();
                CypherRSA.saveKeyPair(activity, _keys);
            } else {
                try {
                    _keys = CypherRSA.loadKeyPair(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return _keys;
    }


    public static KeyPair getKeys(Context context) {
        if (_keys == null) {
            if (!CypherRSA.areKeysPresent(context)) {
                _keys = CypherRSA.generateKey();
                CypherRSA.saveKeyPair(context, _keys);
            } else {
                try {
                    _keys = CypherRSA.loadKeyPair(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return _keys;
    }
}