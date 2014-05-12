package eu.codlab.cyphersend.ui.view.activity;

import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.ui.view.fragment.GuidedTour;

public class HelpMainActivity extends FragmentActivity {
    public class Pager extends FragmentStatePagerAdapter {
        private final int [] res = {
                R.drawable.device,
                R.drawable.paste,
                R.drawable.list,
                R.drawable.certification,
                R.drawable.warning,
                R.drawable.share,
                R.drawable.cloud,
                R.drawable.propagate
        };

        private final int [] str = {
                R.string.guided_tour_1,
                R.string.guided_tour_2,
                R.string.guided_tour_3,
                R.string.guided_tour_4,
                R.string.guided_tour_5,
                R.string.guided_tour_6,
                R.string.guided_tour_7,
                R.string.guided_tour_8
        };

        public Pager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            GuidedTour guided = new GuidedTour();
            Bundle b = new Bundle();
            b.putInt("res",res[i % res.length]);
            Log.d("MainActivity", "set arguments " + str[i % str.length]);
            b.putString("str",getString(str[i % str.length]));
            guided.setArguments(b);

            return guided;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    Pager pager;
    ViewPager mViewPager;

    NfcAdapter.CreateNdefMessageCallback ndefCallback;
    NfcAdapter.OnNdefPushCompleteCallback ndefCompleteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        pager =
                new Pager(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pager);

        com.viewpagerindicator.CirclePageIndicator indicator = (com.viewpagerindicator.CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public boolean isv14Sup(){
        return Build.VERSION.SDK_INT >= 14;
    }

}
