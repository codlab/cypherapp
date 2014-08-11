package eu.codlab.cyphersend.ui.view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import eu.codlab.cyphersend.R;

/**
 * Created by kevinleperf on 31/05/2014.
 */
public class DiscutionActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_discution);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*if (findViewById(R.id.discution_text) != null) {
            outState.putString(TEXT_CONTENT_KEY, ((EditText) findViewById(R.id.discution_text)).getText().toString());
        }*/

        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //manageSaved(savedInstanceState);
    }
}
