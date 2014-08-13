package eu.codlab.cyphersend.ui.view.share.activity;

import android.widget.EditText;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.view.share.activity.internal.ShareActivityAbstract;
import eu.codlab.cyphersend.ui.view.main.controller.MainActivityController;
import eu.codlab.cyphersend.ui.view.main.activity.CypherMainActivity;
import eu.codlab.cyphersend.utils.UrlsHelper;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class ShareThirdPartyFriendsActivity extends ShareActivityAbstract{

    public int getContentView() {
        return R.layout.activity_share_cyphered;
    }

    public int getMode(){
        return DeviceAdapter.SHARE_ONLY;
    }

    public EditText getEditText(){
        return new EditText(this);
    }

    @Override
    public void onRequestWebSend(Device device) {

    }

    @Override
    public void onRequestSend(Device device) {
        PublicKey key = device.getPublicKey();//
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());


        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        write.encodeMessage(getMessage(), true);
        //http://254.254.254.254/
        String share_string = UrlsHelper.getDecodeURL(write);
        CypherMainActivity.sendTextIntent(this, share_string);
    }
}
