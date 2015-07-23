package ee23.ee23android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zapo on 22/07/15.
 */
public class DBManager {

    private static final String DB_NAME = "EE23DB";
    private static final int DB_VERSION = 1;
    private static final String PAGE_TABLE = "page";
    private static final String[] PAGE_COLS = new String[] { "_id" , "url" };

    private class DBOpenHelper extends SQLiteOpenHelper {

        private int version;

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.version = version;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            switch (this.version) {
            case 1:
                onCreateV1(db);
            }
        }

        private void onCreateV1(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE page (_id INTEGER PRIMARY KEY, url TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private SQLiteDatabase db;
    private final DBOpenHelper dbOpenHelper;

    public DBManager (Context c){
        dbOpenHelper = new DBOpenHelper(c, DB_NAME, null, DB_VERSION);
        db = dbOpenHelper.getWritableDatabase();
    }

    public DBManager(Context c, int version) {
        dbOpenHelper = new DBOpenHelper(c, DB_NAME, null, version);
        db = dbOpenHelper.getWritableDatabase();
    }

    public void close(){
        if (this.db != null)
            this.db.close();
    }

    public List<UserPageInfo> listPages() {
        Cursor c = null;
        List<UserPageInfo> result;
        try {
            c = db.query(PAGE_TABLE, PAGE_COLS, null, null, null, null, null);
            result = new ArrayList<>(c.getCount());
            while (c.moveToNext()) {
                UserPageInfo info = getPageInfoFromCursor(c);
                result.add(info);
            }
        } finally {
            if (c != null && !c.isClosed()){
                c.close();
            }
        }
        return result;
    }

    private UserPageInfo getPageInfoFromCursor(Cursor c) {
        UserPageInfo i = new UserPageInfo();
        i.id = c.getInt( c.getColumnIndex("_id") );
        i.url = c.getString( c.getColumnIndex("url") );
        return i;
    }

    public void storeUrl(UserPageInfo i) {
        ContentValues values = new ContentValues();
        values.put("url", i.url);
        long id = db.insert(PAGE_TABLE,null,values);
        i.id = id;
    }

}
