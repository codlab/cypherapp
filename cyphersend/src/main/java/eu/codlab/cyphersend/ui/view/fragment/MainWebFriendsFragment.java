package eu.codlab.cyphersend.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.dbms.discution.controller.DiscutionController;
import eu.codlab.cyphersend.ui.controller.DeviceAdapter;
import eu.codlab.cyphersend.ui.listener.RequestSendListener;
import eu.codlab.cyphersend.ui.view.activity.CypherMainActivity;
import eu.codlab.cyphersend.ui.view.activity.DiscutionActivity;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainWebFriendsFragment extends Fragment implements RequestSendListener, DrawerLayout.DrawerListener {
    private Handler _handler = new Handler(Looper.getMainLooper());
    private DeviceAdapter _adapter;
    private DrawerLayout _drawer_layout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View v = inflater.inflate(R.layout.main_friends_web, parent, false);

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        ListView list = (ListView) v.findViewById(R.id.main_friends_list);
        _adapter = new DeviceAdapter(getActivity(), this, DeviceAdapter.WEB_ONLY);
        if (_adapter.getCount() == 0) {
            list.setVisibility(View.GONE);
            v.findViewById(R.id.main_friends_empty).setVisibility(View.VISIBLE);
        } else {
            list.setVisibility(View.VISIBLE);
            v.findViewById(R.id.main_friends_empty).setVisibility(View.GONE);
        }


        _drawer_layout = (DrawerLayout) v.findViewById(R.id.drawer_web);

        _drawer_layout.setDrawerListener(this);

        list.setAdapter(_adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cypher_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return false;
    }

    @Override
    public void onRequestWebSend(Device device) {

        if (getView() != null && getView().findViewById(R.id.frame_discution) != null) {
            try {
                this.getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_discution, DiscutionFragment.instantiate(device))
                        .commit();
            } catch (Exception e) {

            }
        }
        /*if(getActivity() != null){

            Intent intent = new Intent(getActivity(), DiscutionActivity.class);
            intent.putExtra(DiscutionActivity.TARGET_IDENTIFIER, device.getIdentifier());
            getActivity().startActivity(intent);
        }*/

        if (_drawer_layout.isDrawerOpen(Gravity.RIGHT)) {
            _drawer_layout.closeDrawer(Gravity.RIGHT);
        }

        //TODO put the fragment instantiation in the onClose event if the drawer was opened


        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _drawer_layout.openDrawer(Gravity.RIGHT);
            }
        }, 300);
    }

    public void onRequestSend(Device device) {
        /*
        if(getActivity() != null){
            ((CypherMainActivity) getActivity()).onRequestSend(device);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        setSwipable(false);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        setSwipable(false);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        setSwipable(true);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    private void setSwipable(boolean state) {
        if (getActivity() != null && getActivity() instanceof CypherMainActivity) {
            try {
                ((CypherMainActivity) getActivity()).setViewPagerSwipable(state);
            } catch (Exception e) {

            }
        }
    }
}
