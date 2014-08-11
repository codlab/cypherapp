package eu.codlab.cyphersend.ui.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.security.PublicKey;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.controller.DevicesController;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.dbms.discution.controller.DiscutionController;
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
import eu.codlab.cyphersend.settings.controller.GCMServerRegister;
import eu.codlab.cyphersend.settings.listener.GCMServerRegisterListener;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;
import eu.codlab.cyphersend.ui.view.SwipableViewPager;
import eu.codlab.cyphersend.utils.MD5;
import eu.codlab.cyphersend.utils.RandomStrings;
import eu.codlab.cyphersend.utils.UrlsHelper;
import eu.codlab.pin.PinEntrySupportFragment;

public class CypherMainActivity extends ActionBarActivity
        implements
        MessageSenderListener,
        MessageReceiveListener, GCMServerRegisterListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SwipableViewPager _pager;
    private String regId;
    GoogleCloudMessaging gcm;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "85511368887";

    private final static String CURRENT_SELECTED_TAB = "ActionbarSelected";

    private MainActivityController _controller;
    private MainActivityDialogController _dialog_controller;
    private final static String CALLER = "me";
    static String CALLER_VALUE;
    private boolean _need_refresh_pager;

    //TODO set jit instruction to optimize loader
    //approximation : 25ms
    private synchronized MainActivityController getController() {
        if (_controller == null) _controller = new MainActivityController(this);
        return _controller;
    }

    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null) _dialog_controller = new MainActivityDialogController(this);
        return _dialog_controller;
    }

    private AlertDialog _alert;

    private void registerNfc() {
        if (Build.VERSION.SDK_INT >= 14) {
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

    public void onRequestShare() {
        Intent intent = getShareIntent();
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

    }

    @Override
    public void onRegisterOk() {

    }

    @Override
    public void onRegisterKo() {

    }

    @Override
    public void onRegisterTimeout() {

    }

    private void showNoDevices() {
        if (_alert == null) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle(R.string.main_send_no_friends_title);

            // set dialog message
            alertDialogBuilder
                    .setTitle(getString(R.string.main_send_no_friends_title))
                    .setMessage(getString(R.string.main_send_no_friends_message))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            _alert = null;
                        }
                    });

            // create alert dialog
            _alert = alertDialogBuilder.create();
            _alert.show();
        }
    }

    public void onRequestSend() {
        if (DevicesController.getInstance(this).hasDevices() == false) {
            showNoDevices();
            return;
        }
        createDialogRequestMessage(false);
    }

    public void onRequestWebSend() {
        if (DevicesController.getInstance(this).hasWebDevices() == false) {
            showNoDevices();
            return;
        }
        createDialogRequestMessage(true);
    }

    public void onPagerPinOk() {
        _need_refresh_pager = true;
        _pager.getAdapter().notifyDataSetChanged();

    }

    private class UpdateShareClass {
        public void onUpdate(Intent intent) {

        }
    }

    private class UpdateShareClassv14 extends UpdateShareClass {
        private ShareActionProvider _provider;

        public void setShareActionProvider(ShareActionProvider provider) {
            _provider = provider;
        }

        public ShareActionProvider getShareActionProvider() {
            return _provider;
        }

        @Override
        public void onUpdate(Intent intent) {
            if (_provider != null)
                _provider.setShareIntent(intent);
        }
    }

    private UpdateShareClass _share_class;

    private UpdateShareClass getShareClass() {
        if (_share_class == null) {
            if (isv14Sup())
                _share_class = new UpdateShareClassv14();
            else
                _share_class = new UpdateShareClass();
        }
        return _share_class;
    }

    private Intent getShareIntent() {
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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }

        @Override
        public int getItemPosition(Object object) {
            return _need_refresh_pager && object instanceof PinEntrySupportFragment ? POSITION_NONE : POSITION_UNCHANGED;
        }

    }

    Pager pager;

    NfcAdapter.CreateNdefMessageCallback ndefCallback;
    NfcAdapter.OnNdefPushCompleteCallback ndefCompleteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _need_refresh_pager = false;
        Crashlytics.start(this);

        manageExtras(getIntent());

        if (!MainActivityController.hasKey(this)) {
            Intent intent = new Intent(this, GeneratingActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 14) {
            ndefCallback = new NfcAdapter.CreateNdefMessageCallback() {
                @Override
                public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                    NdefMessage nfcmessage = new NdefMessage(new NdefRecord[]{NdefRecord.createUri(getController().createUri(CypherMainActivity.this))});
                    return nfcmessage;
                }
            };
            ndefCompleteCallback = new NfcAdapter.OnNdefPushCompleteCallback() {
                @Override
                public void onNdefPushComplete(NfcEvent nfcEvent) {
                    mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
                }
            };
        }
        pager =
                new Pager(
                        getSupportFragmentManager());
        _pager = (SwipableViewPager) findViewById(R.id.pager);
        _pager.setOffscreenPageLimit(3);
        _pager.setAdapter(pager);

        Thread t = new Thread() {
            public void run() {
                getController().getKeys(CypherMainActivity.this);
                sendNfc();
                getShareClass().onUpdate(getShareIntent());
                CypherMainActivity.this.supportInvalidateOptionsMenu();
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
                _pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        ////setText(R.string.title_section_default)
        //bar.addTab(bar.newTab().setIcon(R.drawable.ic_action_tab_main)
        //        .setTabListener(tabListener));
        ////setText(R.string.title_section_friends)
        ////setText(R.string.title_section_web)
        bar.addTab(bar.newTab().setIcon(R.drawable.ic_action_tab_web)
                .setTabListener(tabListener));
        bar.addTab(bar.newTab().setIcon(R.drawable.ic_action_tab_share)
                .setTabListener(tabListener));
        ////setText(R.string.title_section_help)
        ////bar.addTab(bar.newTab().setIcon(R.drawable.ic_action_tab_notes)
        ////         .setTabListener(tabListener));

        _pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
                Crouton.cancelAllCroutons();
                switch (position) {
                    case 2:
                        Crouton.makeText(CypherMainActivity.this, R.string.vault_popup, Application.INFO).show();
                        break;
                    case 0:
                        Crouton.makeText(CypherMainActivity.this, R.string.discution_popup, Application.INFO).show();
                        break;
                    case 1:
                        Crouton.makeText(CypherMainActivity.this, R.string.share_popup, Application.INFO).show();
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    public void setViewPagerSwipable(boolean swipable){
        if(_pager != null){
            _pager.setPagingEnabled(swipable);
        }
    }
    private void manageExtras(Intent intent) {
        if (intent.hasExtra(MainActivityController.LOAD_WEB_MESSAGES)) {
            intent.removeExtra(MainActivityController.LOAD_WEB_MESSAGES);
            getWebMessages();
        }
    }


    public boolean isv14Sup() {
        return Build.VERSION.SDK_INT >= 14;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isv14Sup()) {
            getMenuInflater().inflate(R.menu.cypher_main, menu);
            ((UpdateShareClassv14) getShareClass()).onUpdate(getShareIntent());
            //MenuItem item = menu.findItem(R.id.action_item_share);
            //((UpdateShareClassv14) getShareClass()).setShareActionProvider((ShareActionProvider) item.getActionProvider());
            /*if (getController().getKeys(this) != null) {
                ((UpdateShareClassv14) getShareClass()).onUpdate(getShareIntent());
            }*/
        } else {
            getMenuInflater().inflate(R.menu.cypher_main_simple, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_guided:
                Intent helps = new Intent(this, HelpMainActivity.class);
                startActivity(helps);
                return true;
            case R.id.action_friends:
                getWebMessages();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        registerNfc();
        super.onResume();

        if (_loading_web == true) {
            checkCreateProgressDialog();
        }

        if (SettingsActivityController.isDeviceNameSet(this) == false) {
            if (_alert == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);

                // set title
                // set dialog message
                alertDialogBuilder
                        .setTitle(getString(R.string.no_info_title))
                        .setMessage(getString(R.string.no_info_text))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(CypherMainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                _alert = null;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                                _alert = null;
                            }
                        });

                // create alert dialog
                _alert = alertDialogBuilder.create();
                _alert.show();
            }
        }

        /*FrameLayout fragment_main = (FrameLayout) findViewById(R.id.main_fragment);
        if(fragment_main != null){
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_fragment, _controller.getDefaultFragment());
        }*/

        try {
            getDialogController().onResume();
        } catch (Exception e) {
        }
        ;

        if (getIntent() != null && getIntent().getData() != null) {

            onNewUri(getIntent().getData());
        }


        Thread t = new Thread() {
            public void run() {
                getController().getKeys(CypherMainActivity.this);
                sendNfc();
                getShareClass().onUpdate(getShareIntent());
                CypherMainActivity.this.supportInvalidateOptionsMenu();
            }

        };
        t.run();

        checkAndOrStartGCM();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onNewUri(Uri uri) {

        int saved = getController().onNewUri(this, uri);

        if (MainActivityController.SAVED == saved) {
            Toast.makeText(getApplicationContext(), R.string.successfully_saved, Toast.LENGTH_LONG).show();
        } else if (MainActivityController.NOT_SAVED == saved) {
            //nothing
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_saved, Toast.LENGTH_LONG).show();
        }
    }


    private void checkAndOrStartGCM() {
        if (SettingsActivityController.getGCMAccepted(this) && checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(this);

            if ("".equals(regId)) {
                registerInBackground();
            } else {
                sendRegistrationIdToBackend();
            }
        } else {
        }
    }

    @Override
    public void onPause() {
        if (_loading_web) {
            closeProgressDialog();
        }
        if (_alert != null) {
            _alert.dismiss();
            _alert = null;
        }
        super.onPause();


        try {
            getDialogController().onPause();
        } catch (Exception e) {
        }
        ;
    }

    private Handler _handler;

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);

        manageExtras(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                Intent.ACTION_VIEW.equals(intent.getAction())) {
            processIntent(intent);


            _handler = new Handler(Looper.getMainLooper());

            //todo alert with ask for new name
            //todo alert with ask for new url

            int saved = getController().onNewUri(this, getIntent().getData());

            if (MainActivityController.SAVED == saved) {
                Toast.makeText(getApplicationContext(), R.string.successfully_saved, Toast.LENGTH_LONG).show();
            } else if (MainActivityController.NOT_SAVED == saved) {
                //nothing
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

        if (decoded instanceof MessageString) {
            msg = ((MessageString) decoded).getMessage();
        }

        DevicesController controller = DevicesController.getInstance(this);
        Device device = controller.getDeviceFromSignature(signature, msg);

        DiscutionController controller_discution = null;


        if (device != null) {
            if(decoded.isIncognito() == false) {
                controller_discution = DiscutionController.createNewInstance(this, device);
                controller_discution.addMessage(msg, false, System.currentTimeMillis());
            }
            getDialogController().createDialogReceivedMessage(device.getName(), msg, decoded.isIncognito());
        } else {
            if (MD5.encode(msg).equals(CypherRSA.decrypt(Base64Coder.decode(signature), MainActivityController.getKeys(this).getPublic()))) {

                if(message.isIncognito() == false) {
                    controller_discution = DiscutionController.createNewInstance(this, SettingsActivityController.getDeviceIdentifier(this), SettingsActivityController.getDeviceIdentifier(this));
                    controller_discution.addMessage(msg, false, System.currentTimeMillis());
                }

                getDialogController().createDialogReceivedMessage(SettingsActivityController.getDeviceName(this), msg, decoded.isIncognito());
            } else {
                getDialogController().createDialogReceivedMessage("Unknown!", msg, true);
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


    private void getWebMessages() {
        MessageReceiver receiver = new MessageReceiver(this,
                SettingsActivityController.getDeviceURL(this),
                Base64Coder.encodeString(SettingsActivityController.getDeviceIdentifier(this)),
                Base64Coder.encodeString(SettingsActivityController.getDevicePass(this))
        );
        _loading_web = true;
        checkCreateProgressDialog();
        receiver.retrieve();
    }

    @Override
    public void onPostExecute() {
        _loading_web = false;
        closeProgressDialog();
    }

    private ProgressDialog _loading_progress_dialog;
    private boolean _loading_web;

    private void checkCreateProgressDialog() {
        if (_loading_progress_dialog == null) {
            _loading_progress_dialog = new ProgressDialog(this);
            _loading_progress_dialog.setIndeterminate(true);
            _loading_progress_dialog.setTitle(R.string.progress_dialog_load_messages_title);
            _loading_progress_dialog.setMessage(getString(R.string.progress_dialog_load_messages_message));
            _loading_progress_dialog.show();
        } else if (!_loading_progress_dialog.isShowing()) {
            _loading_progress_dialog.show();
        }
    }

    private void closeProgressDialog() {
        if (_loading_progress_dialog != null && _loading_progress_dialog.isShowing()) {
            _loading_progress_dialog.dismiss();
        }
        _loading_progress_dialog = null;
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

    public void onRequestWebSend(Device device) {
        getDialogController().createDialogRequestSend(this, device, true);
    }

    public void onRequestSend(Device device) {
        getDialogController().createDialogRequestSend(this, device, false);
    }

    /*


    http://cypher.codlab.eu/service/add/iEv2M2uz1llSvUbyEK7OHpmUB9pP15LnGp4VyG16lAxmWxHnop9Pzd0B1FJP92vkLdRF8Ubl_YyMWJkJhTiGNx70Ff7bFhjOPICFSy5UolFYpgp_PAPM5hV0yjNC4OhQ3FhkTvLe__EZyhomlNTuwUiiDRowgvUfmcp6tF6dUTd4Ytz3zTHPsKHp8ssCgrPbVKwdDpFD1nHqG_rMjA7y5G9vCwUcU6plyKRu1NvNigkRLyhBBVihEyJQO9IuNdzyW2GuIBWTBnC0uR4M66T69_juSBIoQGsFqYg0UdRZs5N4SGtLX9x3tcGEG_l0T6EFauDZHL_BtE0eesWKmvomDA==/
     n   eUtzdGswRnh0cjNNRTVmNzFzQ0lPbHJNcG5ZZmNEZ2k2cVIxbHFTRzVCcTdSTlNxaDI3SGNsa1o2ZUtLWE9VVXBpbmFCdlVNQ1M5ZWhRNWJqMEVESFAwalpST1NvWDJTcGl2aVNUUjU0Rzh6dXJlWUNMbXBiT3F3bUVRR2puRmdpdlhRNEVRYzdiWjVZTlB5MTRuNGJzRTdwcUlMQjhDeE85aDVXS0pJczF5ZEk4bFhJUUxHdUJpMlpCd2RGTzFYMll6ZzdSbGwwVnVFTmw3eXRVYUNhM2lOMXBjclhFMWN2TVgwSHA2M255OHpWUHlOUjFpQmwyVTh4em5FN29vS3g=/JF_7Ud_2cfRbMMuXusDXFxduoiE_9kC_ufFlWALsTJd_3zw_iZ0NOKCYyAgGZNPrwHecUhiziDvz8nwjJUrZbM4KZysooxomALdMUCDRJHlWzhX2liNrlSJGv5ISlDVP4ZoegO2hgjW9kCKPM8Cvbn5SFHkZS-UHBM4j8DkEM4WdMae5gst74nBhugGklHa8V7kYlNEIgz2awUVhn-NNhf2fYvXICOgNZIEpxCSC7zbhd8TXCd-D4vZ8A-UdD2L9gT8jzE6DcSmLc6jN4nQnX8B2oBTDd08MDKBnrddM6dJcCYd_ipWWz6WKF3co23sKCeMlNqrEnr7pa64yo54vLQ==

     */
    public void createDialogRequestMessage(final boolean use_web_service) {
        final EditText edit_text = new EditText(this);
        _alert = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_request_send_title)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        _alert.dismiss();
                        _alert = null;
                    }
                })
                .setMessage(use_web_service ? R.string.dialog_request_send_message_web : R.string.dialog_request_send_message)
                .setView(edit_text)
                .setPositiveButton(R.string.ok, null).create();
        _alert.show();
        _alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_text.getText().toString().length() > 0) {
                    if (use_web_service) {
                        Intent intent = new Intent(CypherMainActivity.this, ShareFriendsActivity.class);
                        intent.putExtra("message", edit_text.getText().toString());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CypherMainActivity.this, ShareThirdPartyFriendsActivity.class);
                        intent.putExtra("message", edit_text.getText().toString());
                        startActivity(intent);
                    }
                    _alert.dismiss();
                    _alert = null;
                } else {
                }
            }
        });
    }

    public void createDialogRequestSend(final CypherMainActivity parent, final Device device, final boolean use_web_service) {
        final EditText edit_text = new EditText(parent);
        _alert = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_request_send_title)
                .setMessage(use_web_service ? R.string.dialog_request_send_message_web : R.string.dialog_request_send_message)
                .setView(edit_text)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        _alert = null;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (use_web_service) {
                            parent.onValidateMessageWebSend(device, edit_text.getText().toString());
                        } else {
                            parent.onValidateMessageSend(device, edit_text.getText().toString());
                        }
                        _alert = null;
                        dialog.dismiss();
                    }
                }).create();
        _alert.show();
    }

    public void onValidateMessageSend(Device device, String message) {
        PublicKey key = device.getPublicKey();//
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());


        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        write.encodeMessage(message, true);
        //http://254.254.254.254/
        String share_string = UrlsHelper.getDecodeURL(write);
        sendTextIntent(this, share_string);
    }

    static void sendTextIntent(Activity activity, String string) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(CALLER, createRandomString());
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getString(R.string.personnal_data_subject_third_party));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, string);
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_via)));
    }

    static private String createRandomString() {
        CALLER_VALUE = RandomStrings.generate(256);
        return CALLER_VALUE;
    }

    static boolean isCallerMyself(Intent intent) {
        return intent.hasExtra(CALLER);
    }

    static String getStringIntent(Intent intent) {
        return intent.hasExtra(Intent.EXTRA_TEXT) ? intent.getStringExtra(Intent.EXTRA_TEXT)
                : "";
    }

    public void onValidateMessageWebSend(Device device, String message) {
        String idReceiver = Base64Coder.encodeString(device.getIdentifier());
        PublicKey key = device.getPublicKey();//

        MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(this).getPrivate(), idReceiver);
        MessageSender sender = new MessageSender(this, device.getWebsite(), write, message);
        sender.send();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();*/
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if ("".equals(registrationId) || registrationId == null || registrationId.trim().length() == 0) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(this.getPackageName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(CypherMainActivity.this);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regId - no need to register again.
                    storeRegistrationId(CypherMainActivity.this, regId);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String v) {
                return;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        String regId = this.regId;

        String identifier = Base64Coder.encodeString(SettingsActivityController.getDeviceIdentifier(this));
        String pass = Base64Coder.encodeString(SettingsActivityController.getDevicePass(this));
        String website = SettingsActivityController.getDeviceURL(this);
        GCMServerRegister register = new GCMServerRegister(this, website, identifier, pass, regId);
        register.send();
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
