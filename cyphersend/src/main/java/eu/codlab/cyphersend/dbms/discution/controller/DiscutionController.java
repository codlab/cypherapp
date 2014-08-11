package eu.codlab.cyphersend.dbms.discution.controller;

import android.content.Context;
import android.database.Cursor;

import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.dbms.discution.internal.SGBD;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class DiscutionController {
    private Context _context;
    private SGBD _sgbd;

    private DiscutionController(Context context, String identifier, String user_id) {
        _context = context;
        _sgbd = new SGBD(context, identifier, user_id);
        _sgbd.open();
    }

    public static DiscutionController createNewInstance(Context context, Device device){
        return createNewInstance(context, device.getIdentifier(), SettingsActivityController.getDeviceIdentifier(context));

    }

    public static DiscutionController createNewInstance(Context context, String identifier, String user_id){
        return new DiscutionController(context, identifier, user_id);
    }

    public Cursor getChat() {
        return _sgbd.getChat();
    }


    public long addMessage(String content, boolean sender, long timestamp){
        return _sgbd.addMessage(content, sender, timestamp);
    }

    public void deleteChat(long id){
       _sgbd.deleteChat(id);
    }

    public void updateSent(long id, boolean sent) {
        _sgbd.updateSent(id, sent);
    }

    public void close() {
        _sgbd.close();
    }
}
