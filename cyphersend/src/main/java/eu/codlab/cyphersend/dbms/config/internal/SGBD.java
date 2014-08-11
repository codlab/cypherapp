package eu.codlab.cyphersend.dbms.config.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import eu.codlab.cyphersend.dbms.config.model.Config;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SGBD {
    static final String DATABASE_NAME = "data";
    static final String TABLE_CONFIG = "chat";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_CHAT = "create table if not exists " + TABLE_CONFIG + " (_id integer primary key autoincrement, title text, content text)";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SGBD(Context ctx) {
        this.context = ctx;
        if (DBHelper == null)
            DBHelper = DatabaseHelper.getDatabaseHelper(context);
    }

    public SGBD open() throws SQLException {
        if (db == null || !db.isOpen()) {
            db = DBHelper.getWritableDatabase();
            db.execSQL(CREATE_CHAT);
        }

        return this;
    }

    public void close() {
        db = null;
    }

    public synchronized int getIntFrom(boolean state) {
        return state ? 1 : 0;
    }

    public synchronized boolean getBooleanFrom(int state) {
        return state > 0;
    }

    public synchronized long addConfig(String title, String content) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", title);
        initialValues.put("content", content);
        return db.insert(TABLE_CONFIG, null, initialValues);
    }

    public synchronized Cursor getConfigs() {
        Cursor mCursor = db.query(true, TABLE_CONFIG, new String[]{
                        "_id",
                        "title",
                        "content"},
                null,
                null, null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public synchronized void deleteConfig(long id) {
        db.delete(TABLE_CONFIG, "_id=" + id, null);
    }

    public synchronized long updateConfig(long id, String content) {

        ContentValues initialValues = new ContentValues();
        initialValues.put("content", content);
        db.update(TABLE_CONFIG, initialValues, "_id=" + id, null);
        return id;
    }

    private Cursor getConfig(String title) {
        Cursor mCursor = db.query(true, TABLE_CONFIG, new String[]{
                        "_id",
                        "title",
                        "content"},
                "title=?", new String[]{title},
                null, null, null, null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Config getConfigFrom(String title){
        Cursor cursor = getConfig(title);
        Config config = null;
        try{
            if(cursor != null){
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String _title = cursor.getString(cursor.getColumnIndex("title"));
                String _content = cursor.getString(cursor.getColumnIndex("content"));

                config = new Config(id, _title, _content);
            }
        }catch(Exception e){
            config = null;
        }

        return config;
    }
}
