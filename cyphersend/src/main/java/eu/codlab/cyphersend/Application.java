package eu.codlab.cyphersend;

import android.content.Context;

import eu.codlab.pin.IPinEntryListener;
import eu.codlab.pin.IPinUpdateListener;

/**
 * Created by kevinleperf on 15/05/2014.
 */
public class Application extends android.app.Application implements IPinUpdateListener, IPinEntryListener{
    private static Application _instance;
    private boolean _has_pin_entered;

    public static Application getInstance(){
        return _instance;
    }

    private int _pin = -1;

    @Override
    public void onCreate(){
        super.onCreate();

        _has_pin_entered = false;
        _instance = this;

        _pin = this.getSharedPreferences("__INTERNAL__",0).getInt("code",-1);
    }

    @Override
    public boolean onPinEntered(int pin) {
        return pin == _pin;
    }

    public boolean hasPinEntered(){
        return _has_pin_entered;
    }
    @Override
    public void onOk() {
        _has_pin_entered = true;
    }

    @Override
    public boolean onPinChanged(int pin) {
        _has_pin_entered = false;
        getSharedPreferences("__INTERNAL__",0).edit().putInt("code",pin).commit();
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
}
