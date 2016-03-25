package mosis.myplaces;

/**
 * Created by Neca on 6.3.2016..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MyPlacesDBAdapter {
    public static final String DATABASE_NAME = "MyPlacesDb";
    public static final String DATABASE_TABLE = "MyPlaces";
    public static final int DATABASE_VERSION = 1;

    public static final String PLACE_ID = "ID";
    public static final String PLACE_NAME = "Name";
    public static final String PLACE_DESCRIPTION = "Desc";
    public static final String PLACE_LONG = "Long";
    public static final String PLACE_LAT = "Lat";

    private SQLiteDatabase db;

    private final Context context;
    private MyPlacesDatabaseHelper dbHelper;

    public MyPlacesDBAdapter(Context cont){
        context = cont;
        dbHelper = new MyPlacesDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Otvaranje konekcije
    public MyPlacesDBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // Zatvaranje konekcije
    public void close() {
        db.close();
    }

    // Dodavanje podatka u bazu
    public long insertEntry(MyPlace myPlace) {
       // dbHelper.onCreate(db);
        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_NAME, myPlace.getName());
        contentValues.put(PLACE_DESCRIPTION, myPlace.getDesc());
        contentValues.put(PLACE_LONG, myPlace.getLongitude());
        contentValues.put(PLACE_LAT, myPlace.getLatitude());
        long id = -1;
        db.beginTransaction();
        try {
            id = db.insert(DATABASE_TABLE, null, contentValues);
            db.setTransactionSuccessful();
        } catch (SQLiteException ec) {
            Log.w("MyPlaceDBAdapter", ec.getMessage());
        } finally {
            db.endTransaction();
        }
        return id;
    }

    // Brisanje podatka iz baze
    public boolean removeEntry(long id){
        boolean success = false;
        db.beginTransaction();
        try {
            success = db.delete(DATABASE_TABLE, PLACE_ID + "=" + id, null) > 0;
            db.setTransactionSuccessful();
        } catch (SQLiteException ec) {
            Log.w("MyPlaceDBAdapter", ec.getMessage());
        } finally {
            db.endTransaction();
        }
        return success;
    }

    // Vracanje svih podataka iz baze
    public ArrayList<MyPlace> getAllEntries() {
        ArrayList<MyPlace> myPlaces = null;
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(DATABASE_TABLE, null, null, null, null, null, null);
            db.setTransactionSuccessful();
        } catch (SQLiteException ec) {
            Log.v("MyPlacesDBAdapter", ec.getMessage());
        } finally {
            db.endTransaction();
        }

        if (cursor != null)
        {
            myPlaces = new ArrayList<MyPlace>();
            MyPlace myPlace = null;
            while (cursor.moveToNext()){
                myPlace = new MyPlace(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_NAME)));
                myPlace.setID(cursor.getLong(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_ID)));
                myPlace.setDescription(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_DESCRIPTION)));
                myPlace.setLongitude(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_LONG)));
                myPlace.setLatitude(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_LAT)));
                myPlaces.add(myPlace);
            }
        }
        return myPlaces;
    }

    // Vracanje 1 podatka iz baze
    public MyPlace getEntry(long id) {
        MyPlace myPlace = null;
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(DATABASE_TABLE, null, PLACE_ID + "=" + id, null, null, null, null);
            db.setTransactionSuccessful();
        } catch (SQLiteException ec) {
            Log.v("MyPlaceDBAdapter", ec.getMessage());
        } finally {
            db.endTransaction();
        }

        if (cursor != null)
        {
            if (cursor.moveToFirst()) {
                myPlace = new MyPlace(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_NAME)));
                myPlace.setID(cursor.getLong(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_ID)));
                myPlace.setDescription(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_DESCRIPTION)));
                myPlace.setLongitude(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_LONG)));
                myPlace.setLatitude(cursor.getString(cursor.getColumnIndex(MyPlacesDBAdapter.PLACE_LAT)));
            }
        }
        return myPlace;
    }

    // Izmena 1 podatka u bazi
    public int updateEntry(long id, MyPlace myPlace){
        String where = PLACE_ID + "=" + id;

        ContentValues contentValues = new ContentValues();

        contentValues.put(PLACE_NAME, myPlace.getName());
        contentValues.put(PLACE_DESCRIPTION, myPlace.getDesc());
        contentValues.put(PLACE_LONG, myPlace.getLongitude());
        contentValues.put(PLACE_LAT, myPlace.getLatitude());

        return db.update(DATABASE_TABLE, contentValues, where, null);
    }

}


