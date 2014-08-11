package eu.codlab.cyphersend.dbms.discution.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import eu.codlab.cyphersend.utils.MD5;
import eu.codlab.cyphersend.utils.SHA;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SGBD{
    static final String TABLE_CHAT = "chat";

    private String _table_chat_cypher;

    static final String CREATE_CHAT = "create table if not exists %s (_id integer primary key autoincrement, timestamp integer, content text, sent integer, sender integer)";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SGBD(Context ctx, String identifier, String user_id)
    {
        this.context = ctx;

        _table_chat_cypher = TABLE_CHAT
                .concat(SHA.encode(MD5.encode(identifier)) + SHA.encode(identifier));
        _table_chat_cypher = _table_chat_cypher.replaceAll("[^a-zA-Z0-9]", "");

        if(DBHelper == null)
            DBHelper = DatabaseHelper.getDatabaseHelper(context);
    }

    //---opens the database---
    public SGBD open() throws SQLException {
        if(db == null || !db.isOpen()){
            db = DBHelper.getWritableDatabase();
        }

        db.execSQL(CREATE_CHAT.replace("%s", _table_chat_cypher));

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
        return db.insert(_table_chat_cypher, null, initialValues);
    }

    public Cursor getChat(){
        Cursor mCursor = db.query(true, _table_chat_cypher, new String[] {
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
        db.delete(_table_chat_cypher, "_id=" + id, null);
    }

    public void updateSent(long id, boolean sent) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("sent", getIntFrom(sent));
        db.update(_table_chat_cypher, initialValues, "_id="+id, null);
    }
}
