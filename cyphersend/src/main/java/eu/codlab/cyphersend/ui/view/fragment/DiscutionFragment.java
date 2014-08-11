package eu.codlab.cyphersend.ui.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.controller.DevicesController;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.dbms.discution.controller.DiscutionController;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.DiscutionDialogController;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;

/**
 * Created by kevinleperf on 07/04/2014.
 */
public class DiscutionFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, MessageSenderListener {

    public static DiscutionFragment instantiate(Device device) {
        Log.d("DISCUTION","instantiate "+device.getIdentifier());
        DiscutionFragment fragment = new DiscutionFragment();
        Bundle arguments = new Bundle();
        arguments.putString(TARGET_IDENTIFIER, device.getIdentifier());
        fragment.setArguments(arguments);

        return fragment;
    }

    public DiscutionFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_discution, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("DISCUTION", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);


        _loading_web = false;
        if (!getArguments().containsKey(TARGET_IDENTIFIER)) {
            getActivity().finish();
            return;
        }
        _device = DevicesController.getInstance(getActivity()).getDevice(getArguments().getString(TARGET_IDENTIFIER));
        Log.d("DISCUTION", "_device " + (_device == null));
        if (_device == null || !_device.hasWebSite()) {
            getActivity().finish();
            return;
        }
        _discution_list = (ListView) view.findViewById(R.id.discution_list);
        //_discution_list.setStackFromBottom(true);
        _chat = getDiscutionController().getChat();
        _discution_list.setStackFromBottom(true);
        getActivity().setTitle(_device.getName());
        manageSaved(savedInstanceState);

        _incognito_mode = (CheckBox) view.findViewById(R.id.incognito_check);
        _incognito_mode2 = (CheckBox) view.findViewById(R.id.incognito_check_2);
        _incognito_text = (TextView) view.findViewById(R.id.incognito_text);
        _incognito_text.setOnEditorActionListener(this);

        Button send = (Button) view.findViewById(R.id.sender_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        view.findViewById(R.id.incognito_layer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _incognito_mode.setChecked(!_incognito_mode.isChecked());
            }
        });

        _incognito_mode.setOnCheckedChangeListener(this);


        _incognito_mode2.setOnCheckedChangeListener(this);

        _incognito_mode.setChecked(true);
        _input_text = ((EditText) view.findViewById(R.id.discution_text));
    }

    @Override
    public void onPause() {
        //closeProgressDialog();

        try {
            getDiscutionDialogController().onPause();
            getDialogController().onPause();
        } catch (Exception e) {
        }

        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            getDiscutionDialogController().onResume();
            getDialogController().onResume();
        } catch (Exception e) {
        }


        if (_discution_list.getAdapter() == null) {
            _discution_list.setAdapter(new DiscutionAdapter(getActivity(), _chat, true));
        } else {
            ((DiscutionAdapter) _discution_list.getAdapter()).reset();
        }
        _discution_list.smoothScrollToPosition(_discution_list.getAdapter().getCount() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_chat != null && !_chat.isClosed()) {
            _chat.close();
        }
    }


    public final static String TARGET_IDENTIFIER = "device";
    private final static String TEXT_CONTENT_KEY = "DiscutionActivity:TEXT_CONTENT_KEY";
    private ListView _discution_list;
    private CheckBox _incognito_mode;
    private CheckBox _incognito_mode2;
    private TextView _incognito_text;
    private EditText _input_text;
    private Device _device;
    private MainActivityDialogController _dialog_controller;

    //private ProgressDialog _loading_progress_dialog;
    private boolean _loading_web;

    private String _identifier;

    private Cursor _chat;
    private DiscutionDialogController _discution_dialog_controller;

    private DiscutionDialogController getDiscutionDialogController() {
        if (_discution_dialog_controller == null)
            _discution_dialog_controller = new DiscutionDialogController(getActivity());
        return _discution_dialog_controller;
    }

    private DiscutionController _discution_controller;

    private DiscutionController getDiscutionController() {
        if (_discution_controller == null) {
            _discution_controller = DiscutionController.createNewInstance(getActivity(), _device.getIdentifier(), SettingsActivityController.getDeviceIdentifier(getActivity()));
        }
        return _discution_controller;
    }


    private synchronized MainActivityDialogController getDialogController() {
        if (_dialog_controller == null)
            _dialog_controller = new MainActivityDialogController(getActivity());
        return _dialog_controller;
    }

    private class DiscutionAdapter extends CursorAdapter {

        public DiscutionAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public DiscutionAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View newView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.discution_view, viewGroup, false);

            String message = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
            int isSender = cursor.getInt(cursor.getColumnIndexOrThrow("sender"));
            if (isSender > 0) {
                newView.findViewById(R.id.writer).setVisibility(View.GONE);
                newView.findViewById(R.id.sender).setVisibility(View.VISIBLE);
                ((TextView) newView.findViewById(R.id.sender_text)).setText(message);
            } else {
                newView.findViewById(R.id.sender).setVisibility(View.GONE);
                newView.findViewById(R.id.writer).setVisibility(View.VISIBLE);
                ((TextView) newView.findViewById(R.id.writer_text)).setText(message);
            }
            return newView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }


        void reset() {
            this.changeCursor(getDiscutionController().getChat());
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (view == null) {
                view = newView(context, cursor, null);
            }

            String message = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
            int isSender = cursor.getInt(cursor.getColumnIndexOrThrow("sender"));
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timestamp);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String text = format1.format(calendar.getTime());

            if (cursor.getPosition() > 0) {
                view.findViewById(R.id.header).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.header).setVisibility(View.VISIBLE);
            }

            if (isSender > 0) {
                view.findViewById(R.id.writer).setVisibility(View.GONE);
                view.findViewById(R.id.sender).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.sender_text)).setText(text + "\n" + message);
            } else {
                view.findViewById(R.id.sender).setVisibility(View.GONE);
                view.findViewById(R.id.writer).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.writer_text)).setText(text + "\n" + message);
            }
        }
    }


    private void manageSaved(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(TEXT_CONTENT_KEY)) {
            ((EditText) getView().findViewById(R.id.discution_text)).setText(savedInstanceState.getString(TEXT_CONTENT_KEY));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
        _incognito_mode.setOnCheckedChangeListener(null);
        _incognito_mode2.setOnCheckedChangeListener(null);
        _incognito_text.setText(state ? R.string.incognito_on : R.string.incognito_off);
        _incognito_mode.setChecked(state);
        _incognito_mode2.setChecked(state);
        _incognito_mode.setOnCheckedChangeListener(this);
        _incognito_mode2.setOnCheckedChangeListener(this);

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        switch (i) {
            case EditorInfo.IME_ACTION_SEND:
                sendMessage();
            default:
                return false;
        }
    }

    private void sendMessage() {
        if (_input_text.getText().toString().length() > 0) {
            onValidateMessageWebSend(_input_text.getText().toString());
            _input_text.setText("");
        }
    }

    public void onValidateMessageWebSend(String message) {
        if (getActivity() != null) {
            if (!_incognito_mode.isChecked()) {
                getDiscutionController().addMessage(message, true, System.currentTimeMillis());
                ((DiscutionAdapter) _discution_list.getAdapter()).reset();
            }

            String idReceiver = Base64Coder.encodeString(_device.getIdentifier());
            PublicKey key = _device.getPublicKey();//

            MessageWrite write = new MessageWrite(key, MainActivityController.getKeys(getActivity()).getPrivate(), idReceiver);
            MessageSender sender = new MessageSender(this, _device.getWebsite(), write, message, _incognito_mode.isChecked());
            _loading_web = true;
            checkCreateProgressDialog();
            sender.send();
        }
    }

    private void checkCreateProgressDialog() {
    }

    private void closeProgressDialog() {
    }

    @Override
    public void onSendError() {
        _loading_web = false;
        getDiscutionDialogController().createDialogSendError();
    }

    @Override
    public void onOk() {
        _loading_web = false;
        getDiscutionDialogController().createDialogSendOk();
    }

}
