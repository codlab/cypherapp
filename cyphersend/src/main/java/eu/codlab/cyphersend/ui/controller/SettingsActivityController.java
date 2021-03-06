package eu.codlab.cyphersend.ui.controller;

import android.content.Context;
import android.preference.PreferenceManager;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.config.controller.ConfigController;
import eu.codlab.cyphersend.dbms.config.model.Config;
import eu.codlab.cyphersend.utils.RandomStrings;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SettingsActivityController {
    public static boolean isDeviceNameSet(Context context){
        return !"device".equals(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.device), "device")) &&
                PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.device), "device").length() > 0;
    }
    public static void setDeviceName(Context context, String name){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.device), name).commit();
    }
    public static String getDeviceName(Context activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.device), android.os.Build.MODEL);
    }

    public static void setDeviceUrl(Context context, String url){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.website), url).commit();
    }
    public static String getDeviceURLOrEmpty(Context activity){
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.website), "");
    }
    public static String getDeviceURL(Context activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.website), "https://cypher.codlab.eu/");
    }

    public static boolean getGCMAccepted(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.gcm_settings), false);
    }

    public static void setDeviceIdentifier(Context context, String identifier){
        ConfigController.getInstance(context).setConfig(context.getString(R.string.identifier), identifier);
    }
    public static String getDeviceIdentifier(Context activity) {
        Config config = ConfigController.getInstance(activity).getConfig(activity.getString(R.string.identifier));
        String identifier;
        if(config == null || !config.isContentSet()){
            identifier = createRandomIdentifier(activity, 256);
        }else{
            identifier = config.getContent();
        }

        return identifier;
    }

    public static void setDevicePass(Context context, String pass){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pass), pass).commit();
    }

    public static String getDevicePass(Context activity) {
        Config config = ConfigController.getInstance(activity).getConfig(activity.getString(R.string.pass));
        String pass;
        if(config == null || !config.isContentSet()){
            pass = createRandomPass(activity, 256);
        }else{
            pass = config.getContent();
        }

        return pass;
    }

    public static String resetDeviceIdentifier(Context activity) {
        String identifier = createRandomIdentifier(activity, 256);
        return identifier;
    }
    public static String resetDevicePass(Context activity) {
        String pass = createRandomPass(activity, 256);
        return pass;
    }

    public static String createRandomIdentifier(Context activity, int length){
        String identifier = RandomStrings.generate(256);
        ConfigController.getInstance(activity).setConfig(activity.getString(R.string.identifier), identifier);
        return identifier;
    }

    public static String createRandomPass(Context activity, int length){
        String pass = RandomStrings.generate(256);
        ConfigController.getInstance(activity).setConfig(activity.getString(R.string.pass), pass);
        return pass;
    }

}