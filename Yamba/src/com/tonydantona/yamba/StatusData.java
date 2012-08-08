package com.tonydantona.yamba;

import winterwell.jtwitter.Twitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

// responsible for data related functionality
public class StatusData
{
	private static final String TAG = StatusData.class.getSimpleName();
	
	public static final String C_ID = BaseColumns._ID; // Special for id
	public static final String C_CREATED_AT = "yamba_createdAt";
	public static final String C_USER = "yamba_user";
	public static final String C_TEXT = "yamba_text";

	Context context;
	DbHelper dbHelper;
	
	public StatusData(Context context)
	{
		this.context = context;
		dbHelper = new DbHelper();
	}
	
	// this will be called by yamba app onTerminate() to help the system clean up
	public void close()
	{
		dbHelper.close();
	}
	
	/** 
	 * Inserts into database using Status data
	 */
	public long insert(Status status)
	{
		// write the statuses to the db. Using insert string is inefficient and vulnerable so use content values.
		// ContentValues use a key/value pair that can be used as a column,value to insert data into a db
		// (looks like you can just keep adding key/value pairs)
		ContentValues values = new ContentValues();
		
		// and when passed in an insert statement it creates the mapping for us
		values.put(C_ID, status.id);
		values.put(C_CREATED_AT, status.createdAt.getTime());
		values.put(C_USER, status.user.name);
		values.put(C_TEXT, status.text);
		
		return this.insert(values);
	}
	
	/**
	 * Inserts into database
	 */
	public long insert(ContentValues values)
	{
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		// insert into db (OnConflict will insert the new row having a dup prim key by deleting the existing row)
//		db.insertWithOnConflict(DbHelper.TABLE, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		
		// he changed to use this insert instead because of broadcast/receiver
		long ret = 1;
		
		try
		{
			db.insertOrThrow(DbHelper.TABLE, null, values);
		}
		catch (SQLException e)
		{
			// it's an old value
			ret = -1;
		}
		finally
		{
			// close database
			db.close();
		}
		

		return ret;
	}
	
	/**
	 * deletes all the data
	 */
	public void delete()
	{
		// Open database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		// delete the data
		db.delete(DbHelper.TABLE, null, null);
		
		// close database
		db.close();
	}

	public Cursor query()
	{
		// Open database
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// get the data
		// Select * from statuses Where id=?? Having.... Group By ... Order By...
		// below the null as 2nd null (it's the columns arg) means give me all the columns i.e. *
		// selection (3rd arg) is the left side of the where clause (null if not using)
		// selectionArgs = right side of the where clause (null if not using)
		// same for groupby and having, null if not using
		// the last arg is for orderby, don't forget space before DESC
		// so below is equialent to Select * from statuses Order By yamba_createdAt DESC; 
		Cursor cursor = db.query(DbHelper.TABLE, null, null, null, null, null, C_CREATED_AT + " DESC");
		
		/* If you close the database this doesn't work, Marko says it's something to do with
		 * the Garbage collector cleaning it up.  Guess it's ok, since we opened this readonly anwyay
		 */
		// close database
		//db.close();	
		
		return cursor;
	}
	
	/**
	 * Class to help open/create/upgrade database
	 */
	private class DbHelper extends SQLiteOpenHelper
	{
		public static final String DB_NAME = "timeline.db"; // any name i want
		public static final int DB_VERSION = 1; // this is our versioning, we can put anything
		public static final String TABLE = "statuses";
	 	
		public DbHelper()
		{
			//super(context, name, factory, version);
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// called once and only once
			
			// we could have defined this in string.xml.  if so, you would have to put the context from the constructor above
			// in an instance variable: this.context = context;
			// and then reference it here: String sql = context.getString(R.string.sql);
			
			// first create sql (fyi, wikepedia has a nice reference to the sql)
			String sql = String.format("create table %s (%s INT primary key, %s INT, %s TEXT, %s TEXT) ", TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT );
			
			Log.d(TAG, "onCreate sql: " + sql);
			
			db.execSQL(sql);
		}

		@Override
		// this is called when we have deployed a new apk and have changed our DB_VERSION number
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// typically you would do ALTER TABLE.. here to upgrade the db schema
			
			db.execSQL("drop table if exists " + TABLE);
			
			Log.d(TAG, "onUpdate dropped table " + TABLE);
			
			this.onCreate(db);		
		}

	}

}
