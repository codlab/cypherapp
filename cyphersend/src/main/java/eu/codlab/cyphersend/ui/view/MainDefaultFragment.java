package eu.codlab.cyphersend.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.schildbach.wallet.integration.android.BitcoinIntegration;
import eu.codlab.cyphersend.BuildConfig;
import eu.codlab.cyphersend.R;

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

        Button twitter = (Button)v.findViewById(R.id.main_follow);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=codlab"));
                    startActivity(intent);
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/#!/codlab")));
                }
            }
        });

        Button donate = (Button)v.findViewById(R.id.main_donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    BitcoinIntegration.requestForResult(MainDefaultFragment.this.getActivity(), 42, BuildConfig.DONATION_BITCOIN);
                }catch (Exception e) {
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
