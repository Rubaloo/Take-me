package com.example.ruben.takeme.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ruben.takeme.data.TakeMeContract.AnimalEntry;
import com.example.ruben.takeme.data.TakeMeContract.BreedEntry;
import com.example.ruben.takeme.data.TakeMeContract.CategoryEntry;
import com.example.ruben.takeme.data.TakeMeContract.NewEntry;

/**
 * Created by ruben on 14/6/15.
 */
public class TakeMeDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "takeme.db";

    public TakeMeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL " +
                " );";

        final String SQL_CREATE_ANIMAL_TABLE = "CREATE TABLE " + AnimalEntry.TABLE_NAME + " (" +
                AnimalEntry._ID + " INTEGER PRIMARY KEY," +
                AnimalEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                AnimalEntry.COLUMN_MODEL + " TEXT UNIQUE NOT NULL, " +
                AnimalEntry.COLUMN_INFO + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_BREED_TABLE = "CREATE TABLE " + BreedEntry.TABLE_NAME + " (" +
                BreedEntry._ID + " INTEGER PRIMARY KEY," +
                BreedEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                BreedEntry.COLUMN_INFO + " TEXT NOT NULL, " +
                BreedEntry.COLUMN_ANIMAL_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + BreedEntry.COLUMN_ANIMAL_ID + ") REFERENCES " +
                AnimalEntry.TABLE_NAME + " (" + AnimalEntry._ID + "));";

        final String SQL_CREATE_NEW_TABLE = "CREATE TABLE " + NewEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                NewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                NewEntry.COLUMN_ANIMAL_ID + " INTEGER NOT NULL, " +
                NewEntry.COLUMN_BREED_ID + " INTEGER NOT NULL, " +
                NewEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +

                NewEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewEntry.COLUMN_ANIMALS_TAKED + " INTEGER NOT NULL, " +
                NewEntry.COLUMN_ANIMALS_TOTAL + " INTEGER NOT NULL, " +

                NewEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                NewEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to animal table.
                " FOREIGN KEY (" + NewEntry.COLUMN_ANIMAL_ID+ ") REFERENCES " +
                AnimalEntry.TABLE_NAME + " (" + AnimalEntry._ID + "), " +

                // Set up the location column as a foreign key to animal table.
                " FOREIGN KEY (" + NewEntry.COLUMN_CATEGORY_ID+ ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + ")"+
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ANIMAL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BREED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_NEW_TABLE);
}

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BreedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AnimalEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private void createTables() {


    }
}
