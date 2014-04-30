package eu.codlab.cyphersend.ui.listener;

import eu.codlab.cyphersend.dbms.model.Device;

/**
 * Created by kevinleperf on 30/06/13.
 */
public interface RequestSendListener {
    public void onRequestWebSend(Device device);
    public void onRequestSend(Device device);
}
