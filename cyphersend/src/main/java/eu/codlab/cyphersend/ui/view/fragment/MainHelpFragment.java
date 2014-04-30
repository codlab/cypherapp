package eu.codlab.cyphersend.ui.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codlab.cyphersend.R;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainHelpFragment extends Fragment {


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.setHasOptionsMenu(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View v = inflater.inflate(R.layout.main_help, parent, false);


        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
