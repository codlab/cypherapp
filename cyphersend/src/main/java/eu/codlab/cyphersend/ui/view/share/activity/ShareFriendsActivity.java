package eu.codlab.cyphersend.ui.view.share.activity;

import android.view.ContextThemeWrapper;
import android.widget.EditText;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.view.share.activity.internal.ShareActivityAbstract;
import eu.codlab.cyphersend.ui.view.main.controller.MainActivityController;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ShareFriendsActivity extends ShareActivityAbstract {

    public int getContentView() {
        return R.layout.activity_friends;
    }

    public int getMode(){
        return DeviceAdapter.WEB_ONLY;
    }

    public EditText getEditText(){
        return new EditText(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
    }

    @Override
    public void onRequestWebSend(Device device) {
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());
        PublicKey key = device.getPublicKey();

        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        MessageSender sender = new MessageSender(this, device.getWebsite(), write, getMessage());
        sender.send();
    }

    @Override
    public void onRequestSend(Device device){
        //TODO implement simple share
    }
}
