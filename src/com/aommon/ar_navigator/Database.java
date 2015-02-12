package com.aommon.ar_navigator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

	public static final String TAG = "InDatabase";
	private static final String DB_NAME = "KMITL_Location";  
    private static final int DB_VERSION = 1;  
     
    public static final String TABLE_NAME = "Location";  
   
    public static final String COL_NAME  = "name";  
    public static final String COL_LATITUDE = "latitude";  
    public static final String COL_LONGITUDE = "longitude";  	    
     
    Context context;  
    
    public class DatabaseHelper extends SQLiteOpenHelper {    
	    public DatabaseHelper(Context ctx) {  
	    	super(ctx,DB_NAME, null, DB_VERSION);
	        context = ctx;  
	    }  
	   
	    public void onCreate(SQLiteDatabase db) {  
	        db.execSQL("CREATE TABLE " + TABLE_NAME 
	                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "   
	                + COL_NAME  + " TEXT, " + COL_LATITUDE + " DOUBLE, " 
	                + COL_LONGITUDE + " DOUBLE);");  
	           
	        try {  
	            BufferedReader br = new BufferedReader(
	                    new InputStreamReader(context.getAssets().open(
	                    "Database_v1_ms.csv")));  
	            String readLine = null;  
	            readLine = br.readLine();  
	
	            try {  
	                while ((readLine = br.readLine()) != null) {  
	                	//Log.i("Data Input", readLine);
	                    String[] str = readLine.split(",");  
	                    db.execSQL("INSERT INTO " + TABLE_NAME 
	                        + " (" + COL_NAME  + ", " + COL_LATITUDE 
	                        + ", " + COL_LONGITUDE  
	                        + ") VALUES ('" + str[0] 
	                        + "', '" + str[1] + "', '" 
	                        + str[2] + "');");   
	                    
	                    
	                }  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    
	    public void onUpgrade(SQLiteDatabase db, int oldVersion
	            , int newVersion) {  
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);  
	        onCreate(db);  
	    }  
    }
       
    private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Activity mActivity;
    
	public Database(Activity activity) {
        this.mActivity = activity;
        mDbHelper = this.new DatabaseHelper(activity);
        mDb = mDbHelper.getWritableDatabase();
    }
	
	public void close() {
        mDbHelper.close();
    }
    
    public Cursor getMatchingLocate(String constraint) throws SQLException {
		String queryString = "SELECT * FROM " + TABLE_NAME;
		if (constraint != null) {
			// Query for any rows where the state name begins with the
			// string specified in constraint.
			//
			// NOTE:
			// If wildcards are to be used in a rawQuery, they must appear
			// in the query parameters, and not in the query string proper.
			// See http://code.google.com/p/android/issues/detail?id=3153

			queryString += " WHERE " + COL_NAME + " LIKE '%" + constraint + "%'";
			queryString += " ORDER BY " + COL_NAME + " ASC";
			Log.e(TAG, "Call like");
		}
		//String params[] = { constraint };
		Log.e(TAG, "Call getMatching");

		/*if (constraint == null) {
			// If no parameters are used in the query,
			// the params arg must be null.
			params = null;
		}*/
		try {
			//Cursor cursor = mDb.rawQuery(queryString, params);
			Cursor cursor = mDb.rawQuery(queryString, null);
			if (cursor != null) {
				Log.e(TAG, "Cursor moveToFirst");
				cursor.moveToFirst();
				Log.e(TAG, "Return Cursor");
				return cursor;
			}
		} catch (SQLException e) {
			Log.e(TAG, "ERROR");
			Log.e("AutoCompleteDbAdapter", e.toString());
			throw e;
		}

		return null;
	}
    
	public DatabaseHelper getmDbHelper() {
		return mDbHelper;
	}

	public void setmDbHelper(DatabaseHelper mDbHelper) {
		this.mDbHelper = mDbHelper;
	}

}
