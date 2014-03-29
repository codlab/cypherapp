package eu.codlab.cyphersend.dbms.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import eu.codlab.cyphersend.dbms.model.Device;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class SGBD
{
    static final String DATABASE_NAME = "data";
    static final String TABLE_DEVICES = "devices";
    static final int DATABASE_VERSION = 1;

    /*
     * EXTENSION
     * id INT primary key
     * nom VARCHAR 30
     *
     * CARTE
     *
     */
    static final String CREATE_DEVICES = "create table if not exists "+TABLE_DEVICES+" (id integer primary key autoincrement, device text, identifier text, website text, public text)";

    private final Context context;

    private static DatabaseHelper DBHelper;
    private static SQLiteDatabase db;

    public SGBD(Context ctx)
    {
        this.context = ctx;
        if(DBHelper == null)
            DBHelper = new DatabaseHelper(context);
    }

    //---opens the database---
    public SGBD open() throws SQLException {
        if(db == null || !db.isOpen()){
            db = DBHelper.getWritableDatabase();
            db.execSQL(CREATE_DEVICES);
        }

        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
        db = null;
    }

    public long addDevice(String device, String identifier, String publickey, String website){
        ContentValues initialValues = new ContentValues();
        initialValues.put("device", device);
        initialValues.put("identifier", identifier);
        initialValues.put("website", website);
        initialValues.put("public", publickey);
        return db.insert(TABLE_DEVICES, null, initialValues);
    }

    public Device[] getInterfaces(){
        Cursor mCursor = db.query(true, TABLE_DEVICES, new String[] {
                "id",
                "identifier",
                "device",
                "website",
                "public"},
                null,
                null,null,null,null,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        Device [] names = null;
        if(mCursor.getCount()>0){
            names = new Device[mCursor.getCount()];
            String name = "";
            int index = 0;
            String publickey = "";
            String identifier="";
            long id;
            String website = "";
            while(!mCursor.isAfterLast()){
                id = mCursor.getLong(mCursor.getColumnIndex("id"));
                name =mCursor.getString(mCursor.getColumnIndex("device"));
                identifier =mCursor.getString(mCursor.getColumnIndex("identifier"));
                publickey =mCursor.getString(mCursor.getColumnIndex("public"));
                website =mCursor.getString(mCursor.getColumnIndex("website"));
                mCursor.moveToNext();
                names[index] = new Device(id, name, identifier,publickey, website);
                index++;
            }
        }
        mCursor.close();


        return names;
    }
    public void deleteDevice(long id){
        db.delete(TABLE_DEVICES, "id="+id, null);
    }

}
