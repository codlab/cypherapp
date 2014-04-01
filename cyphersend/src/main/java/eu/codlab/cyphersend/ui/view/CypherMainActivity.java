package eu.codlab.cyphersend.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.messages.controller.MessageReceiver;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageReceiveListener;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageRead;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.messages.model.content.MessageContent;
import eu.codlab.cyphersend.messages.model.content.MessageString;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.utils.AppNfc;
import eu.codlab.cyphersend.utils.MD5;
import eu.codlab.cyphersend.utils.RandomStrings;

public class CypherMainActivity extends ActionBarActivity
    implements
        MessageSenderListener,
        MessageReceiveListener {
    private final static String CURRENT_SELECTED_TAB = "ActionbarSelected";

    private MainActivityController _controller;
    private MainActivityDialogController _dialog_controller;

    //TODO set jit instruction to optimize loader
    //approximation : 25ms
    private synchronized MainActivityController getController() {
        if (_controller == null) _controller = new MainActivityController();
        return _controller;
    }

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }

    private void registerNfc() {
        if(Build.VERSION.SDK_INT >= 14){
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) return;  // NFC not available on this device
            nfcAdapter.setNdefPushMessageCallback(ndefCallback, this);
            nfcAdapter.setOnNdefPushCompleteCallback(ndefCompleteCallback, this);
        }
    }

    private void sendNfc() {
        if (getController().hasKeys() && Build.VERSION.SDK_INT >= 14) {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) return;  // NFC not available on this device

            /*NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{AppNfc.createUri(
                    createUri())});*/

            NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(getController().createUri(this))});
            nfcAdapter.setNdefPushMessage(nfcmessage, this);
        }
    }

    public void onRequestShare(){
        Intent intent = getShareIntent();
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

    }

    private class UpdateShareClass{
        public void onUpdate(Intent intent){

        }
    }
    private class UpdateShareClassv14 extends UpdateShareClass{
        private ShareActionProvider _provider;
        public void setShareActionProvider(ShareActionProvider provider){
            _provider = provider;
        }

        public ShareActionProvider getShareActionProvider(){
            return _provider;
        }

        @Override
        public void onUpdate(Intent intent){
            if(_provider != null)
                _provider.setShareIntent(intent);
        }
    }
    private UpdateShareClass _share_class;
    private UpdateShareClass getShareClass(){
        if(_share_class == null){
            if(isv14Sup())
                _share_class = new UpdateShareClassv14();
            else
                _share_class = new UpdateShareClass();
        }
        return _share_class;
    }

    private Intent getShareIntent(){
        String share_string = getController().createUriString(this);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.personnal_data_subject));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, share_string);
        return intent;
    }

    public class Pager extends FragmentStatePagerAdapter {
        public Pager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return getController().getFragment(i);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    Pager pager;
    ViewPager mViewPager;

    NfcAdapter.CreateNdefMessageCallback ndefCallback;
    NfcAdapter.OnNdefPushCompleteCallback ndefCompleteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 14){
            ndefCallback = new NfcAdapter.CreateNdefMessageCallback(){
                @Override
                public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                    NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(getController().createUri(CypherMainActivity.this))});
                    return nfcmessage;
                }
            };
            ndefCompleteCallback = new NfcAdapter.OnNdefPushCompleteCallback(){
                @Override
                public void onNdefPushComplete(NfcEvent nfcEvent) {
                    mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
                }
            };
        }
        pager =
                new Pager(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pager);

        Thread t = new Thread() {
            public void run() {
                getController().getKeys(CypherMainActivity.this);
                sendNfc();
                getShareClass().onUpdate(getShareIntent());
                CypherMainActivity.this.invalidateOptionsMenu();
            }

        };
        t.run();

        //instantiate tab elements

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        bar.setDisplayHomeAsUpEnabled(false);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        bar.addTab(bar.newTab().setText(R.string.title_section_default)
                .setTabListener(tabListener));
        bar.addTab(bar.newTab().setText(R.string.title_section_friends)
                .setTabListener(tabListener));
        bar.addTab(bar.newTab().setText(R.string.title_section_help)
                .setTabListener(tabListener));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public boolean isv14Sup(){
        return Build.VERSION.SDK_INT >= 14;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isv14Sup()){
            getMenuInflater().inflate(R.menu.cypher_main, menu);
            ((UpdateShareClassv14)getShareClass()).onUpdate(getShareIntent());
            MenuItem item = menu.findItem(R.id.action_item_share);
            ((UpdateShareClassv14)getShareClass()).setShareActionProvider((ShareActionProvider) item.getActionProvider());
            if(getController().getKeys(this) != null){
                ((UpdateShareClassv14)getShareClass()).onUpdate(getShareIntent());
            }
        }else{
            getMenuInflater().inflate(R.menu.cypher_main_simple, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_friends:

                Log.d("information",MainActivityController.getDeviceIdentifier(this));
                MessageReceiver receiver = new MessageReceiver(this,
                        MainActivityController.getDeviceURL(this),
                        Base64Coder.encodeString(MainActivityController.getDeviceIdentifier(this)),
                        Base64Coder.encodeString(MainActivityController.getDevicePass(this))
                );
                receiver.retrieve();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        registerNfc();
        super.onResume();

        try {
            getDialogController().onResume();
        } catch (Exception e) {
        }
        ;

        if (getIntent() != null && getIntent().getData() != null) {

            boolean saved = getController().onNewUri(this, getIntent().getData());

            if (saved) {
                Toast.makeText(getApplicationContext(), R.string.successfully_saved, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_saved, Toast.LENGTH_LONG).show();
            }
        }


        Thread t = new Thread() {
            public void run() {
                getController().getKeys(CypherMainActivity.this);
                sendNfc();
                getShareClass().onUpdate(getShareIntent());
                CypherMainActivity.this.invalidateOptionsMenu();
            }

        };
        t.run();
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            getDialogController().onPause();
        } catch (Exception e) {
        }
        ;
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                Intent.ACTION_VIEW.equals(intent.getAction())) {
            processIntent(intent);


            boolean saved = getController().onNewUri(this, getIntent().getData());

            if (saved) {
                Toast.makeText(getApplicationContext(), R.string.successfully_saved, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_saved, Toast.LENGTH_LONG).show();
            }

        }
    }

    private static final int MESSAGE_SENT = 1;

    /**
     * This handler receives a message from onNdefPushComplete
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), R.string.successfully_shared, Toast.LENGTH_LONG).show();

                    break;
            }
        }
    };

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        //if (isSmartphone() && savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
        getSupportActionBar().setSelectedNavigationItem(
                savedInstanceState.getInt(CURRENT_SELECTED_TAB));
        //}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(CURRENT_SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());
    }

    //receive

    @Override
    public void onMessageReceived(MessageRead message) {
        String signature = message.getSenderIdentifier();
        MessageContent decoded = message.decode(MainActivityController.getKeys(this).getPrivate());
        String msg = "";

        if(decoded instanceof MessageString){
            msg = ((MessageString) decoded).getMessage();
        }
        Log.d("received message", message.toString());
        Log.d("received message", message.getMessage());
        Log.d("identifier", signature);
        Log.d("received message", msg);

        DevicesController controller = DevicesController.getInstance(this);
        Device device = controller.getDeviceFromSignature(signature, msg);

        if (device != null) {
            getDialogController().createDialogReceivedMessage(device.getName(), msg);
        } else {
            if (MD5.encode(msg).equals(CypherRSA.decrypt(Base64Coder.decode(signature), MainActivityController.getKeys(this).getPublic()))) {
                getDialogController().createDialogReceivedMessage(MainActivityController.getDeviceName(this), msg);
            }else{
                getDialogController().createDialogReceivedMessage("Unknown!", msg);
            }
        }

    }

    @Override
    public void onReceiveError() {
        getDialogController().createDialogReceivedErrorNull();
    }

    @Override
    public void onEmpty() {
        getDialogController().createDialogReceivedNoNewMessage();
    }


    //send
    @Override
    public void onSendError() {
        getDialogController().createDialogSendError();
    }

    @Override
    public void onOk() {
        getDialogController().createDialogSendOk();
    }

    public void onRequestSend(Device device) {
        getDialogController().createDialogRequestSend(this, device);
    }

    /*


    http://cypher.codlab.eu/service/add/iEv2M2uz1llSvUbyEK7OHpmUB9pP15LnGp4VyG16lAxmWxHnop9Pzd0B1FJP92vkLdRF8Ubl_YyMWJkJhTiGNx70Ff7bFhjOPICFSy5UolFYpgp_PAPM5hV0yjNC4OhQ3FhkTvLe__EZyhomlNTuwUiiDRowgvUfmcp6tF6dUTd4Ytz3zTHPsKHp8ssCgrPbVKwdDpFD1nHqG_rMjA7y5G9vCwUcU6plyKRu1NvNigkRLyhBBVihEyJQO9IuNdzyW2GuIBWTBnC0uR4M66T69_juSBIoQGsFqYg0UdRZs5N4SGtLX9x3tcGEG_l0T6EFauDZHL_BtE0eesWKmvomDA==/
     n   eUtzdGswRnh0cjNNRTVmNzFzQ0lPbHJNcG5ZZmNEZ2k2cVIxbHFTRzVCcTdSTlNxaDI3SGNsa1o2ZUtLWE9VVXBpbmFCdlVNQ1M5ZWhRNWJqMEVESFAwalpST1NvWDJTcGl2aVNUUjU0Rzh6dXJlWUNMbXBiT3F3bUVRR2puRmdpdlhRNEVRYzdiWjVZTlB5MTRuNGJzRTdwcUlMQjhDeE85aDVXS0pJczF5ZEk4bFhJUUxHdUJpMlpCd2RGTzFYMll6ZzdSbGwwVnVFTmw3eXRVYUNhM2lOMXBjclhFMWN2TVgwSHA2M255OHpWUHlOUjFpQmwyVTh4em5FN29vS3g=/JF_7Ud_2cfRbMMuXusDXFxduoiE_9kC_ufFlWALsTJd_3zw_iZ0NOKCYyAgGZNPrwHecUhiziDvz8nwjJUrZbM4KZysooxomALdMUCDRJHlWzhX2liNrlSJGv5ISlDVP4ZoegO2hgjW9kCKPM8Cvbn5SFHkZS-UHBM4j8DkEM4WdMae5gst74nBhugGklHa8V7kYlNEIgz2awUVhn-NNhf2fYvXICOgNZIEpxCSC7zbhd8TXCd-D4vZ8A-UdD2L9gT8jzE6DcSmLc6jN4nQnX8B2oBTDd08MDKBnrddM6dJcCYd_ipWWz6WKF3co23sKCeMlNqrEnr7pa64yo54vLQ==

     */

    public void onValidateMessageSend(Device device, String message) {
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());
        PublicKey key = device.getPublicKey();//

        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        MessageSender sender = new MessageSender(this, device.getWebsite(), write, message);
        sender.send();

    }
}
