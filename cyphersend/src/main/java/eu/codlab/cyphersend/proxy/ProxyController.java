package eu.codlab.cyphersend.proxy;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import eu.codlab.cyphersend.R;
import info.guardianproject.onionkit.ui.OrbotHelper;

/**
 * Created by kevinleperf on 14/08/14.
 */
public class ProxyController {
    private String CONSTANT_URL = "127.0.0.1";
    private int CONSTANT_PORT = 8118;
    private OrbotHelper _helper;
    private Context _context;

    public ProxyController(Context context) {
        _context = context;
        _helper = new OrbotHelper(context);
    }
    private Proxy getProxy(){
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CONSTANT_URL, CONSTANT_PORT));
        return proxy;
    }
    public URLConnection openConnection(URL url) throws IOException {
        if(_helper.isOrbotRunning()){
            return url.openConnection(getProxy());
        } else {
            return url.openConnection();
        }
    }

    public boolean isOrbotInstalled(){
        return _helper.isOrbotInstalled();
    }

    public boolean isOrbotRunning(){
        return _helper.isOrbotRunning();
    }

    public int getResourceText(){
        if(isOrbotRunning()){
            return R.string.orbot_running;
        } else if(!isOrbotRunning() && isOrbotInstalled()) {
            return R.string.orbot_not_running;
        } else {
            return R.string.orbot_not_installed;
        }
    }

    public void promptToInstall(Activity activity) {
        _helper.promptToInstall(activity);
    }

    public void requestOrbotStart(Activity activity) {
        _helper.requestOrbotStart(activity);
    }

    public int getStatusText() {
        if(!isOrbotRunning() && isOrbotInstalled()) {
            return R.string.orbot_activate;
        } else {
            return R.string.orbot_install;
        }
    }

    public int getBackgroundResource() {
        if(isOrbotRunning()){
            return R.drawable.rounded_bottom_green;
        } else if(!isOrbotRunning() && isOrbotInstalled()) {
            return R.drawable.rounded_bottom_orange;
        } else {
            return R.drawable.rounded_bottom_orange;
        }
    }
}
