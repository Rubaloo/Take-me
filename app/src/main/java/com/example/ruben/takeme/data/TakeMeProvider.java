package com.example.ruben.takeme.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.PointF;
import android.net.Uri;

import com.example.ruben.takeme.Utils;

import java.util.ArrayList;

/**
 * Created by ruben on 14/6/15.
 */
public class TakeMeProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TakeMeDbHelper mOpenHelper;

    static final int NEWS = 100;
    static final int NEW_BY_ID = 101;
    static final int CATEGORIES = 200;
    static final int ANIMALS = 300;
    static final int BREEDS = 400;
    static final int BREEDS_WITH_ANIMAL_ID = 401;



    private static final SQLiteQueryBuilder sBreedsByAnimalQueryBuilder;

    static{
        sBreedsByAnimalQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sBreedsByAnimalQueryBuilder.setTables(
                TakeMeContract.BreedEntry.TABLE_NAME + " INNER JOIN " +
                        TakeMeContract.AnimalEntry.TABLE_NAME +
                        " ON " + TakeMeContract.BreedEntry.TABLE_NAME +
                        "." + TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID +
                        " = " + TakeMeContract.AnimalEntry.TABLE_NAME +
                        "." + TakeMeContract.AnimalEntry._ID);
    }

    private static final String sBreedAnimalSelection =
            TakeMeContract.BreedEntry.TABLE_NAME+
                    "." + TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID + " = ? ";

    //new._id = ?
    private static final String sNewIdSelection =
            TakeMeContract.NewEntry.TABLE_NAME+
                    "." + TakeMeContract.NewEntry._ID + " = ? ";
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TakeMeContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.

        matcher.addURI(authority, TakeMeContract.PATH_ANIMAL, ANIMALS);
        matcher.addURI(authority, TakeMeContract.PATH_BREED, BREEDS);
        matcher.addURI(authority, TakeMeContract.PATH_CATEGORY, CATEGORIES);
        matcher.addURI(authority, TakeMeContract.PATH_NEW, NEWS);
        matcher.addURI(authority, TakeMeContract.PATH_NEW+ "/*", NEW_BY_ID);
        matcher.addURI(authority, TakeMeContract.PATH_BREED + "/*", BREEDS_WITH_ANIMAL_ID);

        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new TakeMeDbHelper(getContext());


        Cursor animals = this.query(TakeMeContract.AnimalEntry.CONTENT_URI,null,null,null,null);
        //default values
        if(animals.getCount() == 0) initDefaultDBContent();
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case NEWS:
                return TakeMeContract.NewEntry.CONTENT_TYPE;
            case NEW_BY_ID:
                return TakeMeContract.NewEntry.CONTENT_ITEM_TYPE;
            case CATEGORIES:
                return TakeMeContract.CategoryEntry.CONTENT_TYPE;
            case ANIMALS:
                return TakeMeContract.AnimalEntry.CONTENT_TYPE;
            case BREEDS:
                return TakeMeContract.BreedEntry.CONTENT_TYPE;
            case BREEDS_WITH_ANIMAL_ID:
                return TakeMeContract.BreedEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getBreedsByAnimalId(Uri uri, String[] projection, String sortOrder) {
        int animalId = TakeMeContract.BreedEntry.getAnimalIdFromUri(uri);


        String[] selectionArgs;
        String selection;

        selectionArgs = new String[]{Integer.toString(animalId)};

        return sBreedsByAnimalQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sBreedAnimalSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getNewById(Uri uri, String[] projection, String sortOrder) {

        int newId = TakeMeContract.NewEntry.getNumberFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                TakeMeContract.NewEntry.TABLE_NAME,
                projection,
                sNewIdSelection,
                new String[]{Integer.toString(newId)},
                null,
                null,
                sortOrder
        );
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "news/#/*"
            case NEWS:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TakeMeContract.NewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NEW_BY_ID:
            {
                retCursor = getNewById(uri,projection,sortOrder);
                break;
            }
            // "categories"
            case CATEGORIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TakeMeContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "animals"
            case ANIMALS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TakeMeContract.AnimalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //breeds
            case BREEDS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TakeMeContract.BreedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //breeds/*
            case BREEDS_WITH_ANIMAL_ID: {
                retCursor = getBreedsByAnimalId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NEWS: {
                long _id = db.insert(TakeMeContract.NewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TakeMeContract.NewEntry.buildNewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORIES: {
                long _id = db.insert(TakeMeContract.CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TakeMeContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ANIMALS: {
                long _id = db.insert(TakeMeContract.AnimalEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TakeMeContract.AnimalEntry.buildAnimalUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BREEDS: {
                long _id = db.insert(TakeMeContract.BreedEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TakeMeContract.BreedEntry.buildBreedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case NEWS:
                rowsDeleted = db.delete(
                        TakeMeContract.NewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsDeleted = db.delete(
                        TakeMeContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ANIMALS:
                rowsDeleted = db.delete(
                        TakeMeContract.AnimalEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BREEDS:
                rowsDeleted = db.delete(
                        TakeMeContract.BreedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case NEWS:
                rowsUpdated = db.update(
                        TakeMeContract.NewEntry.TABLE_NAME,values,selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsUpdated = db.update(
                        TakeMeContract.CategoryEntry.TABLE_NAME,values,selection, selectionArgs);
                break;
            case ANIMALS:
                rowsUpdated = db.update(
                        TakeMeContract.AnimalEntry.TABLE_NAME,values,selection, selectionArgs);
                break;
            case BREEDS:
                rowsUpdated = db.update(
                        TakeMeContract.BreedEntry.TABLE_NAME,values,selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {

            case NEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TakeMeContract.NewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CATEGORIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TakeMeContract.CategoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ANIMALS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TakeMeContract.AnimalEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BREEDS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TakeMeContract.BreedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


    private void initDefaultDBContent()
    {

        String dogPK = "1";
        String catPK = "2";

        //Add Animals
        ContentValues a1 = new ContentValues();
        a1.put(TakeMeContract.AnimalEntry._ID, catPK);
        a1.put(TakeMeContract.AnimalEntry.COLUMN_NAME, "cat");
        a1.put(TakeMeContract.AnimalEntry.COLUMN_INFO, "The best man friend");
        a1.put(TakeMeContract.AnimalEntry.COLUMN_MODEL, "dog.3D");

        ContentValues a2 = new ContentValues();
        a2.put(TakeMeContract.AnimalEntry._ID, dogPK);
        a2.put(TakeMeContract.AnimalEntry.COLUMN_NAME, "dog");
        a2.put(TakeMeContract.AnimalEntry.COLUMN_INFO, "The almost best man friend");
        a2.put(TakeMeContract.AnimalEntry.COLUMN_MODEL, "cat.3D");

        ContentValues[] animalsV = {a1,a2};
        this.bulkInsert(TakeMeContract.AnimalEntry.CONTENT_URI, animalsV);

        //Add categories
        ContentValues c1 = new ContentValues();
        c1.put(TakeMeContract.CategoryEntry._ID, "1");
        c1.put(TakeMeContract.CategoryEntry.COLUMN_NAME, "abandoned");

        ContentValues c2 = new ContentValues();
        c2.put(TakeMeContract.CategoryEntry._ID, "2");
        c2.put(TakeMeContract.CategoryEntry.COLUMN_NAME, "newborn");

        ContentValues c3 = new ContentValues();
        c3.put(TakeMeContract.CategoryEntry._ID, "3");
        c3.put(TakeMeContract.CategoryEntry.COLUMN_NAME, "special treatment");

        ContentValues c4 = new ContentValues();
        c4.put(TakeMeContract.CategoryEntry._ID, "4");
        c4.put(TakeMeContract.CategoryEntry.COLUMN_NAME, "no time");

        ContentValues[] categories = {c1,c2,c3,c4};
        this.bulkInsert(TakeMeContract.CategoryEntry.CONTENT_URI, categories);

        //Add breeds

        String boxerDescription = "The boxer is playful, exuberant, inquisitive, attentive," +
                "demonstrative, devoted and outgoing, he is a perfect companion for an active family.";

        String goldenDescription = "Everybody's friend, the golden retriever is known for her" +
                " devoted and obedient nature as a family companion.";

        String germanShepardDescription = "Among the most intelligent of breeds, the German Shepherd" +
                " Dog is so intent on his mission whatever that may be and he is virtually unsurpass" +
                "ed in working versatility. ";

        String dobermanDescription = "The Doberman pinscher is an intelligent capable guardian, ever" +
                " on the alert and ready to protect her family or home.";

        String egyptianMauDescription = "While fanciers might at first be attracted to the Egyptian" +
                " Mau's beautiful spotted coat, most become enthusiasts because of the breed's " +
                "temperament and personality. ";

        String somaliDescription = "With all the virtues of the Abyssinian and adorned by a gorgeous" +
                " semi-long coat, the Somali is a beautiful and lively addition to any household.";

        String siberianDescription = "Siberians are affectionate cats with a good dose of personality" +
                " and playfulness. ";

        String russianBlueDescription="Russian Blues are gentle, genteel cats, and are usually" +
                " reserved, or absent, when strangers are around.";


//        ContentValues b1 = new ContentValues();
//        b1.put(TakeMeContract.BreedEntry._ID,"1");
//        b1.put(TakeMeContract.BreedEntry.COLUMN_NAME, "boxer");
//        b1.put(TakeMeContract.BreedEntry.COLUMN_INFO, boxerDescription);
//        b1.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, dogPK);
//
//        ContentValues b2 = new ContentValues();
//        b2.put(TakeMeContract.BreedEntry._ID, "2");
//        b2.put(TakeMeContract.BreedEntry.COLUMN_NAME, "golden retriever");
//        b2.put(TakeMeContract.BreedEntry.COLUMN_INFO, goldenDescription);
//        b2.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, dogPK);
//
//        ContentValues b3 = new ContentValues();
//        b3.put(TakeMeContract.BreedEntry._ID, "3");
//        b3.put(TakeMeContract.BreedEntry.COLUMN_NAME,"german shepard");
//        b3.put(TakeMeContract.BreedEntry.COLUMN_INFO,germanShepardDescription);
//        b3.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, dogPK);
//
//        ContentValues b4 = new ContentValues();
//        b4.put(TakeMeContract.BreedEntry._ID, "4");
//        b4.put(TakeMeContract.BreedEntry.COLUMN_NAME,"doberman");
//        b4.put(TakeMeContract.BreedEntry.COLUMN_INFO,dobermanDescription);
//        b4.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, dogPK);
//
//        ContentValues b5 = new ContentValues();
//        b5.put(TakeMeContract.BreedEntry._ID, "5");
//        b5.put(TakeMeContract.BreedEntry.COLUMN_NAME,"egyptian miau");
//        b5.put(TakeMeContract.BreedEntry.COLUMN_INFO,egyptianMauDescription);
//        b5.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, catPK);
//
//        ContentValues b6 = new ContentValues();
//        b6.put(TakeMeContract.BreedEntry._ID, "6");
//        b6.put(TakeMeContract.BreedEntry.COLUMN_NAME,"somali");
//        b6.put(TakeMeContract.BreedEntry.COLUMN_INFO,somaliDescription);
//        b6.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, catPK);
//
//        ContentValues b7 = new ContentValues();
//        b7.put(TakeMeContract.BreedEntry._ID, "7");
//        b7.put(TakeMeContract.BreedEntry.COLUMN_NAME,"siberian");
//        b7.put(TakeMeContract.BreedEntry.COLUMN_INFO,siberianDescription);
//        b7.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, catPK);
//
//        ContentValues b8 = new ContentValues();
//        b8.put(TakeMeContract.BreedEntry._ID, "8");
//        b8.put(TakeMeContract.BreedEntry.COLUMN_NAME,"russian blue");
//        b8.put(TakeMeContract.BreedEntry.COLUMN_INFO,russianBlueDescription);
//        b8.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, catPK);
//
//        ContentValues[] breeds = {b1,b2,b3,b4,b5,b6,b7,b8};
//        this.bulkInsert(TakeMeContract.BreedEntry.CONTENT_URI, breeds);

        //Adding News
        //Category 1,2,3,4
        // Animals 1,      2
        //Breeds 1,2,3,4,  5,6,7,8

        String[] dogNames = {"boxer","golden retriever","german shepard","doberman"};
        String[] catNames = {"egyptian miau","somali","siberian","russian blue"};
        String[] dogDescriptions = {boxerDescription,goldenDescription,germanShepardDescription,dobermanDescription};
        String[] catDescriptions = {egyptianMauDescription,somaliDescription,siberianDescription,russianBlueDescription};


        ArrayList<ContentValues> breeds = new ArrayList<ContentValues>();
        //Init dogs
        for (int i = 0; i < dogNames.length; ++i) {
            ContentValues b1 = new ContentValues();
            b1.put(TakeMeContract.BreedEntry._ID,Integer.toString(i+1));
            b1.put(TakeMeContract.BreedEntry.COLUMN_NAME, dogNames[i]);
            b1.put(TakeMeContract.BreedEntry.COLUMN_INFO, dogDescriptions[i]);
            b1.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, dogPK);
            breeds.add(b1);

        }
        //Init Cats
        for (int i = 0; i < catNames.length; ++i) {
            ContentValues b1 = new ContentValues();
            b1.put(TakeMeContract.BreedEntry._ID,Integer.toString(i+1+dogNames.length));
            b1.put(TakeMeContract.BreedEntry.COLUMN_NAME, catNames[i]);
            b1.put(TakeMeContract.BreedEntry.COLUMN_INFO, catDescriptions[i]);
            b1.put(TakeMeContract.BreedEntry.COLUMN_ANIMAL_ID, catPK);
            breeds.add(b1);

        }

        ContentValues[] breedsArray = new ContentValues[breeds.size()];
        breeds.toArray(breedsArray);
        this.bulkInsert(TakeMeContract.BreedEntry.CONTENT_URI,breedsArray);




        ArrayList<ContentValues> news = new ArrayList<ContentValues>();
        ContentValues cv;
        Integer animalId,breedId,categoryId,takedAnimals,totalAnimals;
        String description;
        for (int i = 0; i < 100; ++i)
        {
            PointF loc = Utils.getRandomCordinates();
            animalId = Utils.getRandomIntBetween(1,2);

            breedId = (animalId == Integer.valueOf(dogPK)) ? Utils.getRandomIntBetween(1,4) : Utils.getRandomIntBetween(5,8);
            categoryId = Utils.getRandomIntBetween(1,4);
            totalAnimals = Utils.getRandomIntBetween(1,10);
            takedAnimals = Utils.getRandomIntBetween(1,totalAnimals);
            description = (animalId == Integer.valueOf(dogPK)) ? dogDescriptions[breedId-1] : catDescriptions[breedId-5];

            cv = new ContentValues();
            cv.put(TakeMeContract.NewEntry.COLUMN_DESCRIPTION,description);
            cv.put(TakeMeContract.NewEntry.COLUMN_ANIMALS_TOTAL,totalAnimals);
            cv.put(TakeMeContract.NewEntry.COLUMN_ANIMALS_TAKED,takedAnimals);
            cv.put(TakeMeContract.NewEntry.COLUMN_COORD_LAT,loc.x);
            cv.put(TakeMeContract.NewEntry.COLUMN_COORD_LONG,loc.y);

            cv.put(TakeMeContract.NewEntry.COLUMN_ANIMAL_ID,animalId);
            cv.put(TakeMeContract.NewEntry.COLUMN_BREED_ID,breedId);
            cv.put(TakeMeContract.NewEntry.COLUMN_CATEGORY_ID,categoryId);

            news.add(cv);
        }


        ContentValues[] array = new ContentValues[news.size()];
        news.toArray(array);
        this.bulkInsert(TakeMeContract.NewEntry.CONTENT_URI,array);



    }
}