package eu.codlab.cyphersend.ui.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.codlab.cyphersend.R;

/**
 * Created by kevinleperf on 07/04/2014.
 */
public class VaultNoFragment extends Fragment {


    public VaultNoFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.main_vault_no_pin, container, false);
    }
}
