package eu.codlab.cyphersend.ui.view;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.security.PrivateKey;
import java.security.PublicKey;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.controller.DevicesController;
import eu.codlab.cyphersend.dbms.model.Device;
import eu.codlab.cyphersend.messages.controller.MessageReceiver;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageReceiveListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.security.CypherRSA;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainFriendsFragment extends Fragment implements RequestSendListener {

    DeviceAdapter _adapter;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        this.setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle){
        View v = inflater.inflate(R.layout.main_friends, parent, false);

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){

        ListView list = (ListView)v.findViewById(R.id.main_friends_list);
        _adapter = new DeviceAdapter(getActivity(), this);
        list.setAdapter(_adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.cypher_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
        }
        return false;
    }

    public void onRequestSend(Device device){
        if(getActivity() != null){
            ((CypherMainActivity) getActivity()).onRequestSend(device);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
