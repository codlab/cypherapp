package eu.codlab.cyphersend.dbms.discution.internal;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.UUID;

import eu.codlab.cyphersend.Application;

/**
 * Created by kevinleperf on 28/06/13.
 */
class DatabaseHelper {
    private final static String CONTEXT_SHARED = "shared";
    private SQLiteDatabase _database;

    private DatabaseHelper(Context context) {
        SQLiteDatabase.loadLibs(context);

        String serial = null;

        if (hasPassword(context)) {
            serial = getPassword(context);
        } else {
            serial = createPassword(context);
        }


        _database = Application.getInstance().createDatabase("chat.db", serial);
    }


    boolean hasPassword(Context context) {
        return getPassword(context) != null;
    }

    String getPassword(Context context) {
        return context.getSharedPreferences(CONTEXT_SHARED, 0).getString(CONTEXT_SHARED, null);
    }

    String createPassword(Context context) {
        context.getSharedPreferences(CONTEXT_SHARED, 0).edit()
                .putString(CONTEXT_SHARED, UUID.randomUUID().toString()).commit();
        return getPassword(context);
    }


    SQLiteDatabase getWritableDatabase() {
        return _database;
    }

    private static ArrayList<String> _database_string = new ArrayList<String>();
    private static ArrayList<DatabaseHelper> _databases = new ArrayList<DatabaseHelper>();
    public static DatabaseHelper getDatabaseHelper(Context context) {
        int i=0;
        DatabaseHelper helper = new DatabaseHelper(context);
        return helper;
    }
}