package eu.codlab.cyphersend.dbms.config.internal;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.dbms.devices.internal.SGBD;
import eu.codlab.cyphersend.utils.MD5;
import eu.codlab.cyphersend.utils.SHA;

/**
 * Created by kevinleperf on 28/06/13.
 */
class DatabaseHelper {
    private final static String CONTEXT_SHARED = "shared";

    private SQLiteDatabase _database;
    private static DatabaseHelper _database_helper;

    private DatabaseHelper(Context context) {
        SQLiteDatabase.loadLibs(context);

        String serial = null;

        if (hasPassword(context)) {
            serial = getPassword(context);
        } else {
            serial = createPassword(context);
        }

        _database = Application.getInstance().createDatabase("config.db", serial);
    }

    SQLiteDatabase getWritableDatabase() {
        return _database;
    }

    public static DatabaseHelper getDatabaseHelper(Context context) {
        if (_database_helper == null)
            _database_helper = new DatabaseHelper(context);
        return _database_helper;
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
}