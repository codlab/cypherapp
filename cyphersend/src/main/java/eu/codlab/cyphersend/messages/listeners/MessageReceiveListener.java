package eu.codlab.cyphersend.messages.listeners;

import eu.codlab.cyphersend.messages.controller.MessageReceiver;
import eu.codlab.cyphersend.messages.model.MessageRead;

/**
 * Created by kevinleperf on 29/06/13.
 */
public interface MessageReceiveListener {
    public void onMessageReceived(MessageRead message);
    public void onReceiveError();
    public void onEmpty();
}
