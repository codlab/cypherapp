package eu.codlab.cyphersend.dbms.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kevinleperf on 28/06/13.
 */
class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context)
    {
        super(context, SGBD.DATABASE_NAME, null, SGBD.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SGBD.CREATE_DEVICES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion)
    {
        onCreate(db);
    }
}