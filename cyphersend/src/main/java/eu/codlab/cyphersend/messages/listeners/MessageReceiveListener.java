package eu.codlab.cyphersend.messages.listeners;

import eu.codlab.cyphersend.messages.model.MessageRead;

/**
 * Created by kevinleperf on 29/06/13.
 */
public interface MessageReceiveListener {
    void onMessageReceived(MessageRead message);
    void onReceiveError();
    void onEmpty();

    /**
     * Call before any other callback to tell the listener that the content was finally loaded
     */
    void onPostExecute();
}
