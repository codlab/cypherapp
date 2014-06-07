package eu.codlab.cyphersend.dbms.discution.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import eu.codlab.cyphersend.Application;
import eu.codlab.cyphersend.dbms.devices.model.Device;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SGBD
{
    static final String DATABASE_NAME = "data";
    static final String TABLE_CHAT = "chat";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_CHAT = "create table if not exists "+TABLE_CHAT+" (_id integer primary key autoincrement, timestamp integer, content text, sent integer, sender integer)";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SGBD(Context ctx, String identifier, String user_id)
    {
        this.context = ctx;
        if(DBHelper == null)
            DBHelper = DatabaseHelper.getDatabaseHelper(context, identifier, user_id);
    }

    //---opens the database---
    public SGBD open() throws SQLException {
        if(db == null || !db.isOpen()){
            db = DBHelper.getWritableDatabase();
            db.execSQL(CREATE_CHAT);
        }

        return this;
    }

    //---closes the database---
    public void close()
    {
        //db.close();
        //SQLiteDatabase.releaseMemory();

        db = null;
    }

    public int getIntFrom(boolean state){
        return state ? 1 : 0;
    }

    public boolean getBooleanFrom(int state){
        return state > 0;
    }
    public long addMessage(String content, boolean sender, long timestamp){
        ContentValues initialValues = new ContentValues();
        initialValues.put("content", content);
        initialValues.put("sender", getIntFrom(sender));
        initialValues.put("timestamp", timestamp);
        return db.insert(TABLE_CHAT, null, initialValues);
    }

    public Cursor getChat(){
        Cursor mCursor = db.query(true, TABLE_CHAT, new String[] {
                "_id",
                "content",
                "sender",
                "timestamp"},
                null,
                null,null,null,"timestamp ASC",null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }
    public void deleteChat(long id){
        db.delete(TABLE_CHAT, "_id=" + id, null);
    }

    public void updateSent(long id, boolean sent) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("sent", getIntFrom(sent));
        db.update(TABLE_CHAT, initialValues, "_id="+id, null);
    }
}
