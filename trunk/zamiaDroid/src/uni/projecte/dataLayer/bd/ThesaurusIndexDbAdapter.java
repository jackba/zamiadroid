/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package uni.projecte.dataLayer.bd;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple DataType database access helper class. 
 * recommended).
 */
public class ThesaurusIndexDbAdapter {

	public static final String KEY_ROWID = "_id";
    public static final String THNAME = "thName";
    public static final String DBTABLE = "DBtable";
    public static final String ITEMS = "items";
    
  
    private static final String TAG = "DataTypeDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table thIndex ("
            + KEY_ROWID + " INTEGER PRIMARY KEY,"
            + THNAME + " TEXT UNIQUE,"
            + DBTABLE + " TEXT,"
            + ITEMS + " INTEGER"
            + ");";
    

    private static final String DATABASE_NAME = "thIndexes";
    private static final String DATABASE_TABLE = "thIndex";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
           

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ThesaurusIndexDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ThesaurusIndexDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new DataType using the name, description and type provided. If the DataType is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param type the type of the DataType
     * @param dtName the name of the DataType
     * @param dtDesc the description of the DataType
     * @return rowId or -1 if failed
     */
    public long createTh (String thName, String dbTable, int numItems) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(THNAME, thName);
        initialValues.put(DBTABLE , dbTable);
        initialValues.put(ITEMS , numItems);  

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    
 

    /**
     * Delete the DataType with the given dtId
     * 
     * @param dtId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteDT(long dtId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + dtId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all DT's in the database
     * 
     * @return Cursor over all DT's
     */
    public Cursor fetchAllTH() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, THNAME,
                DBTABLE,ITEMS}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the DT that matches the given name
     * 
     * @param name of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    
  public Cursor fetchThbyName(String name) throws SQLException {
    	
 	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, THNAME,
                DBTABLE,ITEMS}, THNAME + "= \"" + name +"\"", null, null, null, null);
 
 }

	public boolean removeThbyName(String thName) {


        return mDb.delete(DATABASE_TABLE, THNAME + "=\"" + thName+"\"", null) > 0;

		
		
	}

	public boolean updateThCount(long thId, int numElem) {

		ContentValues vals = new ContentValues();
		vals.put(ITEMS,numElem);
		
		return mDb.update(DATABASE_TABLE,vals,KEY_ROWID+" = "+thId,null) >0;
		
		
	}
	
	
    
    /**
     * Return a Cursor positioned at first DT that matches the given Type
     * 
     * @param type of DT's to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    
  /*  public Cursor fetchDTbyType(int type) throws SQLException {
    	
    	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, TYPE,
                   NAME,DESC}, TYPE + "=" + type, null, null, null, null);
    
    } */
    
}