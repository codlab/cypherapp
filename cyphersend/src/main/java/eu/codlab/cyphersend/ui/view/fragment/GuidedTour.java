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
public class GuidedTour extends Fragment {


    public GuidedTour(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_help_1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null && getArguments().containsKey("res") &&
                getArguments().containsKey("str")){
            TextView str = (TextView)view.findViewById(R.id.guided_text);
            ImageView img = (ImageView)view.findViewById(R.id.guided_img);
            str.setText(getArguments().getString("str"));
            Log.d("MainActivity", "have argument "+ getArguments().getString("str"));
            img.setImageResource(getArguments().getInt("res"));
        }
    }
}
