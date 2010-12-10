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
public class ThesaurusItemsDbAdapter {

	public static final String KEY_ROWID = "_id";
    public static final String GENUS = "Genus";
    public static final String SPECIE = "Specie";
    public static final String SUBSPECIE = "Subspecie";
    public static final String ICODE = "iCode";
    public static final String NAMECODE = "NameCode";
    public static final String AUTHOR = "Author";

    
  
    private static final String TAG = "ThesaurusItemDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    
    /**
     * Database creation sql statement
     */
  

    private static final String DATABASE_NAME = "thDB";
    private static final int DATABASE_VERSION = 2;
	private static String DATABASE_TABLE;

    private final Context mCtx;
    

    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        private String DATABASE_CREATE;


        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        	
        	DATABASE_CREATE =
                "create table if not exists "+ DATABASE_TABLE + " ("
                + KEY_ROWID + " INTEGER PRIMARY KEY,"
                + GENUS + " TEXT,"
                + SPECIE + " TEXT,"
                + SUBSPECIE + " TEXT,"
                + ICODE + " TEXT,"
                + NAMECODE + " TEXT,"
                + AUTHOR + " TEXT"
                + ");";

            db.execSQL(DATABASE_CREATE);
           

        }
        
     public void onOpen(SQLiteDatabase db) {
        	
        	DATABASE_CREATE =
                "create table if not exists "+ DATABASE_TABLE + " ("
                + KEY_ROWID + " INTEGER PRIMARY KEY,"
                + GENUS + " TEXT,"
                + SPECIE + " TEXT,"
                + SUBSPECIE + " TEXT,"
                + ICODE + " TEXT,"
                + NAMECODE + " TEXT,"
                + AUTHOR + " TEXT"
                + ");";

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
    public ThesaurusItemsDbAdapter(Context ctx) {
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
    public ThesaurusItemsDbAdapter open(String dbName) throws SQLException {
        
    	DATABASE_TABLE=dbName;
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
    public long addThesaurusItem(String genus, String specie, String subspecie, String iCode, String nameCode, String author) {
 
    	
    	ContentValues initialValues = new ContentValues();
        initialValues.put(GENUS, genus);
        initialValues.put(SPECIE , specie);
        initialValues.put(SUBSPECIE ,subspecie);
        initialValues.put(ICODE , iCode);  
        initialValues.put(NAMECODE , nameCode);  
        initialValues.put(AUTHOR , author);  


        return mDb.insert(DATABASE_TABLE, null, initialValues);
        
    

    }
    
    
    
    public void startTransaction(){
    	
    	 mDb.beginTransaction();
    	
    	
    	
    }
    public void endTransaction(){
    	
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    	
    	
    }
    
    public long fastInsert(String genus, String specie, String subspecie, String iCode, String nameCode, String author) {
 
    	
    	ContentValues initialValues = new ContentValues();
        initialValues.put(GENUS, genus);
        initialValues.put(SPECIE , specie);
        initialValues.put(SUBSPECIE ,subspecie);
        initialValues.put(ICODE , iCode);  
        initialValues.put(NAMECODE , nameCode);  
        initialValues.put(AUTHOR , author);  


        return mDb.insert(DATABASE_TABLE, null, initialValues);
        
    

    }

    /**
     * Delete the DataType with the given dtId
     * 
     * @param dtId id of note to delete
     * @return true if deleted, false otherwise
     */
/*    public boolean deleteDT(long dtId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + dtId, null) > 0;
    }
  */  
    /**
     * Return a Cursor over the list of all DT's in the database
     * 
     * @return Cursor over all DT's
     */
  /*  public Cursor fetchAllDTs() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, TYPE,
                NAME,DESC}, null, null, null, null, null);
    }*/

    /**
     * Return a Cursor positioned at the DT that matches the given name
     * 
     * @param name of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    
    public Cursor fetchTbhItem(String genus,String specie, String subspecie) throws SQLException {
    	
 	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, GENUS,
               SPECIE ,SUBSPECIE,AUTHOR,ICODE,NAMECODE}, GENUS + "= \"" + genus +"\" and "+ SPECIE + "= \"" + specie +"\" and "+ SUBSPECIE + "= \""+ subspecie+"\"", null, null, null, null);
 
 }
    
    /**
     * Return a Cursor positioned at first DT that matches the given Type
     * 
     * @param type of DT's to retrieveORDER BY Genus
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    
   public Cursor fetchAllItems() throws SQLException {
    	
    	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID}, null, null, null, null, null,"0, 10");
    
    }
   
   public Cursor fetchNumAllItems() throws SQLException {
   	
	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID}, null, null, null, null, null);

}
   
   public Cursor fetchSynonymous(String icode) throws SQLException {
   	
	   return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, GENUS,
              SPECIE,SUBSPECIE,AUTHOR}, ICODE + "= \"" + icode +"\"", null, null, null, null);

}

	public Cursor fetchNext(String selection) {
		   //" ORDER BY Genus Specie Subspecie ASC"
		
			return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, GENUS,
	                  SPECIE ,SUBSPECIE,AUTHOR}, selection, null, null,null,SPECIE);
	}
	
	public void dropTable(String tbName){
		
		mDb.execSQL("drop table if exists "+tbName);
		
	}
    
}
