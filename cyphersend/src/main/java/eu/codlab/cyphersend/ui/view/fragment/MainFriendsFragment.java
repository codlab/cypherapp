package eu.codlab.cyphersend.ui.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.ui.view.main.activity.CypherMainActivity;

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
        View header = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.header_view_share, null, false);
        list.addHeaderView(header);
        _adapter = new DeviceAdapter(getActivity(), this, DeviceAdapter.SHARE_ONLY);
        if(_adapter.getCount() == 0){
            list.setVisibility(View.GONE);
            v.findViewById(R.id.main_friends_empty).setVisibility(View.VISIBLE);
        }else{
            list.setVisibility(View.VISIBLE);
            v.findViewById(R.id.main_friends_empty).setVisibility(View.GONE);
        }
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

    @Override
    public void onRequestWebSend(Device device) {
        if(getActivity() != null){
            ((CypherMainActivity) getActivity()).onRequestWebSend(device);
        }
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
