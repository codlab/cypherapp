package eu.codlab.cyphersend.utils;

import android.app.Activity;

import java.security.PublicKey;

import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;

/**
 * Created by kevinleperf on 27/04/2014.
 */
public class UrlsHelper {

    public static String getPublicInfoURL(Activity activity, String key){
        return "https://cypher.codlab.eu/me/"
                + Base64Coder.encodeString(SettingsActivityController.getDeviceName(activity))
                + "/"
                + Base64Coder.encodeString(SettingsActivityController.getDeviceURLOrEmpty(activity))
                + "/"
                + Base64Coder.encodeString(SettingsActivityController.getDeviceIdentifier(activity))
                + "/"
                + key
                + "/";
    }

    public static String getDecodeURL(MessageWrite write){
        return "https://cypher.codlab.eu/decode/"+write.getSenderIdentifier()+"/"+write.getEncodedMessage();
    }
}
