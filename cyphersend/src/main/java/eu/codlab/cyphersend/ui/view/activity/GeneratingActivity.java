package eu.codlab.cyphersend.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.crashlytics.android.Crashlytics;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.ui.controller.MainActivityController;

/**
 * Created by kevinleperf on 11/05/2014.
 */
public class GeneratingActivity extends Activity {
    private Handler _handler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_generating);

        _handler = new Handler(Looper.getMainLooper());

        Thread t = new Thread() {
            public void run() {
                MainActivityController.generateKey(GeneratingActivity.this);
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(GeneratingActivity.this, CypherMainActivity.class);
                        startActivity(i);
                        GeneratingActivity.this.finish();

                    }
                });
            }
        };
        t.start();
    }

    public void onBackPressed() {
        return;
    }
}
