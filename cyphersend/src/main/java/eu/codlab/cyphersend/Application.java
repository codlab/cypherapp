package eu.codlab.cyphersend;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.codlab.cyphersend.dbms.config.controller.ConfigController;
import eu.codlab.cyphersend.dbms.config.model.Config;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityEnhancedController;
import eu.codlab.cyphersend.ui.view.activity.SettingsActivity;
import eu.codlab.pin.IPinEntryListener;
import eu.codlab.pin.IPinUpdateListener;

/**
 * Created by kevinleperf on 15/05/2014.
 */
public class Application extends android.app.Application implements IPinUpdateListener, IPinEntryListener {
    private static final Configuration c = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();
    private static final Configuration s = new Configuration.Builder().setDuration(Configuration.DURATION_SHORT).build();
    public static final Style INFO = new Style.Builder().setBackgroundColorValue(Style.holoBlueLight).setPaddingDimensionResId(R.dimen.default_margin).build();
    public static final Style ERROR_LONG = new Style.Builder().setConfiguration(c).setBackgroundColorValue(Style.holoRedLight).setPaddingDimensionResId(R.dimen.default_margin).build();
    public static final Style OK_SHORT = new Style.Builder().setConfiguration(s).setBackgroundColorValue(Style.holoGreenLight).setPaddingDimensionResId(R.dimen.default_margin).build();
    private static Application _instance;
    private boolean _has_pin_entered;

    public static Application getInstance() {

        return _instance;
    }

    private int _pin = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        _has_pin_entered = false;
        _instance = this;

        SettingsActivityEnhancedController.cleanValues(this);

        Config config = ConfigController.getInstance(this).getConfig("code");
        if(config != null && config.isContentSet()){
            try {
                _pin = Integer.parseInt(config.getContent());
            }catch(Exception e){

            }
        }
    }

    @Override
    public boolean onPinEntered(int pin) {
        return pin == _pin;
    }

    public boolean hasPinEntered() {
        return _has_pin_entered;
    }

    @Override
    public void onOk() {
        _has_pin_entered = true;
    }

    @Override
    public boolean onPinChanged(int pin) {
        _has_pin_entered = false;
        ConfigController.getInstance(this).setConfig("code", Integer.toString(pin, 10));
        _pin = pin;
        return true;
    }

    @Override
    public void onExit() {

    }

    @Override
    public Context getListenerContext() {
        return this;
    }

    @Override
    public boolean hasPreviousPin() {
        return _pin != -1;
    }

    public SQLiteDatabase createDatabase(String name, String password) {
        File path = getDatabasePath(name);
        if (!path.getParentFile().exists())
            path.getParentFile().mkdirs();
        return SQLiteDatabase.openOrCreateDatabase(path, password, null);
    }
}
