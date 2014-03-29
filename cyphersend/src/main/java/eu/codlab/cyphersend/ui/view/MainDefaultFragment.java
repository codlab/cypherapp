package eu.codlab.cyphersend.ui.view;

import android.app.Fragment;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.utils.AppNfc;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class MainDefaultFragment extends Fragment {


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.setHasOptionsMenu(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View v = inflater.inflate(R.layout.main_default, parent, false);

        Button button = (Button)v.findViewById(R.id.main_send);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(getActivity() != null){
                    ((CypherMainActivity)getActivity()).onRequestShare();
                }
            }
        });

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
