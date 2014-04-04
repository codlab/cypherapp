package eu.codlab.cyphersend.settings.listener;

/**
 * Created by kevinleperf on 30/06/13.
 */
public interface GCMServerRegisterListener {
    public void onRegisterOk();
    public void onRegisterKo();
    public void onRegisterTimeout();
}
