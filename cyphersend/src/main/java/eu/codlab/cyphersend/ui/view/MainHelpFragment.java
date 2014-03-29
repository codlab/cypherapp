package eu.codlab.cyphersend.ui.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
