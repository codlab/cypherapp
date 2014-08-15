package eu.codlab.cyphersend.ui.view.fragment;

import android.app.Activity;
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
import eu.codlab.cyphersend.ui.view.main.activity.CypherMainActivity;

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


        Button share_send = (Button) v.findViewById(R.id.main_share_sendsend);

        share_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    ((CypherMainActivity) getActivity()).onRequestSend();
                }
            }
        });


        Button button = (Button) v.findViewById(R.id.main_send);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    ((CypherMainActivity) getActivity()).onRequestShare();
                }
            }
        });

        Button twitter = (Button) v.findViewById(R.id.main_follow);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=codlab"));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/codlab")));
                }
            }
        });

        Button codlab = (Button) v.findViewById(R.id.codlab);
        if(codlab != null){
            codlab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = getActivity();
                    if(activity != null && activity instanceof CypherMainActivity){
                        ((CypherMainActivity) activity).onNewUri(Uri.parse("https://cypher.codlab.eu/me/Y29kbGFi/aHR0cHM6Ly9jeXBoZXIuY29kbGFiLmV1Lw==/RzJnM0RSd0tvemlpN2RnU1B1RDdQblFNTG45UWJ4eGk4Qm5FSzJYMk92eGtaNHdUbUpFdjExVTZ1MGUyck9aRkxpZVpucEJWaGxYMGlhcW55Nk5qNDlPcU94TkxncXVadzdPQjJiTGV6cDF1NHNwSVdhZkQ5R0Q0ZUxlY3VnZXRNbXlub21uWkRpazBNMlpDbU9LdkdSZkFaWVNURnp4UG9mc3FRVTFUeHFRNWJZVmIzNUk0clNTNkNRQk1QVUdMYjRMTWF0ODdzU1RPVkJndFB2Q1Nza09ObzI4djJtQzJuU0pHUmJwMUJhNlNiRGtVNWpnYmU0NUhZQVRLc0k3WTQ=/MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2y2_sh-adutxEhjsBcrdUnXmBX3vTi5-5Us_wVN4Jzru0U_r74b6aC0O5jBEY_AzsZYMQBc6WosHGE9NUyEhMx9R-a_M2kbX2BQXFWedSawLxGoEfOpAaTXNjBIcEpPCDTS1NhncTn08NEZofN9h3kDFzsJlIQTpfLKzQmgg2Wc2am3RdMecT0pjZ7HB7WzaTVTjhxrc_10gGk-hrrMonc_0Eatw9xu3iLeAR-5ZKlWHiJRY9sR2yKs15_FucWLndjgq6Tk_iwVUS5Rki74lI12irMzLJzJT_Akroj7mydnFjflblHazCr_K8eGc4iQiVgPim2mDHYVrZzoKWFdyowIDAQAB/"));
                    }
                }
            });
        }
        Button donate = null;//(Button) v.findViewById(R.id.main_donate);
        if(donate != null)
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    BitcoinIntegration.requestForResult(MainDefaultFragment.this.getActivity(), 42, BuildConfig.DONATION_BITCOIN);
                } catch (Exception e) {
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
