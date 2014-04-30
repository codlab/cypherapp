package eu.codlab.cyphersend.ui.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.ui.view.fragment.MainFriendsFragment;
import eu.codlab.cyphersend.utils.Color;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context _context;
    private ArrayList<Device> _devices;
    private RequestSendListener _listener;
    private int _mode;

    public final static int BOTH=0;
    public final static int WEB_ONLY=1;
    public final static int SHARE_ONLY=2;

    private ArrayList<Device> getDevices() {
        if (_devices == null) return new ArrayList<Device>();
        return _devices;
    }

    public DeviceAdapter(Context context, RequestSendListener listener, int mode) {
        DevicesController controller = DevicesController.getInstance(context);
        ArrayList<Device> devices = controller.getDevices();
        _devices = new ArrayList<Device>();
        if (WEB_ONLY == mode) {
            for (int i = devices.size() - 1; i >= 0; i--) {
                if (devices.get(i).hasWebSite()) {
                    _devices.add(devices.get(i));
                }
            }
        } else {
            _devices.addAll(devices);
        }
        _mode = mode;

        _listener = listener;
        setContext(context);
    }

    private void setContext(Context context) {
        _context = context;
    }

    public Context getContext() {
        return _context;
    }

    @Override
    public int getCount() {
        return getDevices().size();
    }

    @Override
    public Object getItem(int i) {
        return getDevices().get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((Device) getItem(i)).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = ((LayoutInflater) (getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.device_item_list, viewGroup, false);

        View color = v.findViewById(R.id.color);
        color.setBackgroundColor(Color.getColor(i));

        final Device d = (Device) getItem(i);
        TextView name = (TextView) v.findViewById(R.id.device_name);
        if (name != null) name.setText(d.getName());

        TextView publickey = (TextView) v.findViewById(R.id.device_publickey);
        if (publickey != null) publickey.setText(d.getPublic());

        Button web_send = (Button) v.findViewById(R.id.device_web_send);
        if (d.hasWebSite() == false || _mode == SHARE_ONLY) {
            web_send.setVisibility(View.GONE);
        } else {
            web_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_listener != null) {
                        _listener.onRequestWebSend(d);
                    }
                }
            });
        }
        Button send = (Button) v.findViewById(R.id.device_send);
        if (_mode == WEB_ONLY) {
            send.setVisibility(View.GONE);
        } else {
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_listener != null) {
                        _listener.onRequestSend(d);
                    }
                }
            });
        }


        return v;
    }
}
