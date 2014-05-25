package eu.codlab.cyphersend.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.security.KeyPair;

import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.view.activity.CypherMainActivity;
import eu.codlab.cyphersend.ui.view.fragment.MainDefaultFragment;
import eu.codlab.cyphersend.ui.view.fragment.MainFriendsFragment;
import eu.codlab.cyphersend.ui.view.fragment.MainHelpFragment;
import eu.codlab.cyphersend.ui.view.fragment.VaultNoFragment;
import eu.codlab.cyphersend.utils.UrlsHelper;
import eu.codlab.pin.IPinEntryListener;
import eu.codlab.pin.PinCheckHelper;
import eu.codlab.pin.PinEntrySupportFragment;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainActivityController implements IPinEntryListener {
    public static String LOAD_WEB_MESSAGES = "eu.codlab.cyphersend.LOAD_WEB_MESSAGES";
    private CypherMainActivity _main_activity;
    private MainDefaultFragment _default_fragment;
    private MainFriendsFragment _friends_fragment;
    private MainHelpFragment _help_fragment;
    private PinCheckHelper _helper;

    public MainActivityController() {
        _main_activity = null;
    }

    public MainActivityController(CypherMainActivity mainActivity) {
        _main_activity = mainActivity;
    }

    private Fragment getDefaultFragment() {
        if (_default_fragment == null) _default_fragment = new MainDefaultFragment();
        return _default_fragment;
    }

    private Fragment getFriendsFragment() {
        if (_friends_fragment == null) _friends_fragment = new MainFriendsFragment();
        return _friends_fragment;
    }

    private Fragment getHelpFragment() {
        if (_helper != null) _helper.unregister(Application.getInstance());
        _helper = new PinCheckHelper(this);
        _helper.register(Application.getInstance());
        if (Application.getInstance().hasPinEntered()) {
            //TODO show correct fragment
        } else if (Application.getInstance().hasPreviousPin() == true) {
            return new PinEntrySupportFragment();
        } else {
            //TODO show error no pin saved
            return new VaultNoFragment();
        }
        if (_help_fragment == null) _help_fragment = new MainHelpFragment();
        return _help_fragment;
    }

    public Fragment getFragment(int id) {
        switch (id) {
            case 1:
                return getFriendsFragment();
            case 2:
                return getHelpFragment();
            default:
                return getDefaultFragment();
        }
    }


    private static KeyPair _keys;


    public static String getDevicePublicKey(Activity activity) {
        return new String(Base64Coder.encode(CypherRSA.exportPublicKey(getKeys(activity).getPublic())));
    }

    public Uri createUri(Activity activity) {
        return Uri.parse(createUriString(activity));
    }

    public String createUriString(Activity activity) {
        return UrlsHelper.getPublicInfoURL(activity, getDevicePublicKey(activity));
    }

    public final static int SAVED = 0;
    public final static int NOT_SAVED = 1;
    public final static int ERROR = 2;

    public int onNewUri(Context context, Uri uri) {
        if (uri != null) {
            String[] splitted = uri.getPath().split("/");
            if (splitted.length > 0) {
                int idx_publick_key = splitted[splitted.length - 1].length() > 0 ? splitted.length - 1 : splitted.length - 2;

                if (idx_publick_key >= 4) {
                    String device_name = Base64Coder.decodeString(splitted[idx_publick_key - 3]);
                    String device_url = Base64Coder.decodeString(splitted[idx_publick_key - 2]);
                    String device_identifier = Base64Coder.decodeString(splitted[idx_publick_key - 1]);
                    String public_key = splitted[idx_publick_key];

                    if (DevicesController.getInstance(context).hasDevice(device_identifier)) {
                        //TODO ask for different infos to manage save or not
                        Device device = DevicesController.getInstance(context).getDevice(device_identifier);
                        //TODO manage elsewhere
                        boolean res = false;
                        res = DevicesController.getInstance(context).updateDeviceUrl(device, device_url);
                        res |= DevicesController.getInstance(context).updateDeviceName(device, device_name);
                        if (res)
                            return SAVED;
                        return NOT_SAVED;
                    } else {
                        DevicesController.getInstance(context).addDevice(device_name, device_identifier, public_key, device_url);
                        return SAVED;
                    }
                }
                return ERROR;
            }
        }
        return ERROR;
    }

    public static boolean hasKeys() {
        return _keys != null;
    }

    public static boolean hasKey(Activity activity) {
        return CypherRSA.areKeysPresent(activity);
    }

    public static KeyPair generateKey(Activity activity) {
        _keys = CypherRSA.generateKey();
        CypherRSA.saveKeyPair(activity, _keys);
        return _keys;
    }

    public static KeyPair getKeys(Activity activity) {
        if (_keys == null) {
            if (CypherRSA.areKeysPresent(activity)) {
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

    @Override
    public boolean onPinEntered(int pin) {
        return Application.getInstance().onPinEntered(pin);
    }

    @Override
    public void onOk() {
        Application.getInstance().onOk();
        if (_main_activity != null)
            _main_activity.onPagerPinOk();
    }

    @Override
    public void onExit() {
        //TODO remove fragment
    }

    @Override
    public Context getListenerContext() {
        return Application.getInstance();
    }
}