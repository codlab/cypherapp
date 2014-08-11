package eu.codlab.cyphersend.messages.listeners;

/**
 * Created by kevinleperf on 29/06/13.
 */
public interface MessageSenderListener {
    public void onSendError();
    public void onOk();
}
