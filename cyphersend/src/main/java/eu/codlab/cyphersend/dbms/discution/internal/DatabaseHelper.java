package eu.codlab.cyphersend.dbms.discution.internal;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.dbms.devices.internal.SGBD;
import eu.codlab.cyphersend.utils.MD5;
import eu.codlab.cyphersend.utils.SHA;

/**
 * Created by kevinleperf on 28/06/13.
 */
class DatabaseHelper {
    private SQLiteDatabase _database;

    private DatabaseHelper(Context context, String identifier, String user_id) {
        SQLiteDatabase.loadLibs(context);

        _database = Application.getInstance().createDatabase("chat"+ SHA.encode(MD5.encode(identifier))+SHA.encode(identifier)+".db", user_id + identifier);
    }

    SQLiteDatabase getWritableDatabase() {
        return _database;
    }

    private static ArrayList<String> _database_string = new ArrayList<String>();
    private static ArrayList<DatabaseHelper> _databases = new ArrayList<DatabaseHelper>();
    public static DatabaseHelper getDatabaseHelper(Context context, String identifier, String user_id) {
        int i=0;
        for(String str : _database_string){
            if((identifier+"_"+user_id).equals(str)){
                return _databases.get(i);
            }
            i++;
        }

        DatabaseHelper helper = new DatabaseHelper(context, identifier, user_id);
        _databases.add(helper);
        _database_string.add(identifier+"_"+user_id);
        return helper;
    }
}