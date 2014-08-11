package eu.codlab.cyphersend.ui.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.codlab.cyphersend.R;
import eu.codlab.cyphersend.dbms.devices.controller.DevicesController;
import eu.codlab.cyphersend.dbms.devices.model.Device;
import eu.codlab.cyphersend.dbms.discution.controller.DiscutionController;
import eu.codlab.cyphersend.messages.controller.MessageSender;
import eu.codlab.cyphersend.messages.listeners.MessageSenderListener;
import eu.codlab.cyphersend.messages.model.MessageWrite;
import eu.codlab.cyphersend.security.Base64Coder;
import eu.codlab.cyphersend.ui.controller.DiscutionDialogController;
import eu.codlab.cyphersend.ui.controller.MainActivityController;
import eu.codlab.cyphersend.ui.controller.MainActivityDialogController;
import eu.codlab.cyphersend.ui.controller.SettingsActivityController;

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
