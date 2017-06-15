package com.example.android.groceries2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.groceries2.R;

import static android.R.attr.version;
import static com.example.android.groceries2.MainActivity.dbHelper;
import static java.security.AccessController.getContext;

/**
 * Created by takeoff on 001 01 Jun 17.
 * <p>
 * Info:
 * A helper class to manage database creation and version management.
 * <p>
 * You create a subclass implementing onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int)
 * and optionally onOpen(SQLiteDatabase), and this class takes care of opening the database if it
 * exists, creating it if it does not, and upgrading it as necessary. Transactions are used to make
 * sure the database is always in a sensible checkBoxState.
 * <p>
 * This class makes it easy for ContentProvider implementations to defer opening and upgrading the
 * database until first use, to avoid blocking application startup with long-running database upgrades.
 * <p>
 * Note: this class assumes monotonically increasing version numbers for upgrades.
 */

public class GroceriesDbHelper extends SQLiteOpenHelper {

    private Context context;

    private int listsCount = 0;

    private boolean activeList = false;

    private final String LIST_TABLE_NAME_part_1 = "LIST_table_";

    //Database name
    public static final String DB_NAME = "GROCERIES_db";
    //Database version
    public static final int DB_VERSION = 1;
    //id column for all tables
    public static final String ID_COLUMN = "_id";
    //name column for all tables
    public static final String NAME_COLUMN = "name";
    //checked state column
    public static final String CHECKED_COLUMN = "checked";
    //checked column
    public static final String AMOUNT_COLUMN = "amount";


    /*ITEMS table*/
    public static final String ITEMS_TABLE_NAME = "ITEMS_table";
    //price column
    public static final String ITEMS_PRICE_COLUMN = "price";
    //measure column
    public static final String ITEMS_MEASURE_COLUMN = "measure";

    //table create command
    public static final String ITEMS_TABLE_CREATE_COMMAND = "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            ITEMS_PRICE_COLUMN + " REAL NOT NULL DEFAULT 0, " +
            ITEMS_MEASURE_COLUMN + " INTEGER NOT NULL DEFAULT 0, " +
            AMOUNT_COLUMN + " REAL, " +
            CHECKED_COLUMN + " INTEGER);";
    //table drop command
    public static final String ITEMS_TABLE_DROP_COMMAND = "DROP TABLE " + ITEMS_TABLE_NAME + ";";


    /*MEASURE table*/
    public static final String MEASURE_TABLE_NAME = "MEASURE_table";
    //measure column
    public static final String MEASURE_MEASURE_COLUMN = "measure";
    //table create command
    public static final String MEASURE_TABLE_CREATE_COMMAND = "CREATE TABLE " + MEASURE_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MEASURE_MEASURE_COLUMN + " TEXT NOT NULL UNIQUE);";
    //table drop command
    public static final String MEASURE_TABLE_DROP_COMMAND = "DROP TABLE " + MEASURE_TABLE_NAME + ";";


    /*LOG table*/
    public static final String LOG_TABLE_NAME = "LOG_table";
    //creation date column
    public static final String LOG_DATE_CREATED_COLUMN = "created";
    //completion date column
    public static final String LOG_DATE_COMPLETE_COLUMN = "complete";
    //table create command
    public static final String LOG_TABLE_CREATE_COMMAND = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME_COLUMN + " TEXT NOT NULL UNIQUE, " +
            LOG_DATE_CREATED_COLUMN + " INTEGER NOT NULL UNIQUE, " +
            LOG_DATE_COMPLETE_COLUMN + " INTEGER UNIQUE);";
    //table drop command
    public static final String LOG_TABLE_DROP_COMMAND = "DROP TABLE " + LOG_TABLE_NAME + ";";


    /*LIST table*/
    // TODO: 013 13 Jun 17 auto increment list table's name dynamically
    private String listTableName;
    //item column
    public static final String LIST_ITEM_COLUMN = "item";
    //table create command
    //table drop command


    /**
     * Reqired implementation of a constructor.
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public GroceriesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * Requred implementation of an abstract method.
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ITEMS_TABLE_CREATE_COMMAND);
        db.execSQL(LOG_TABLE_CREATE_COMMAND);
        db.execSQL(MEASURE_TABLE_CREATE_COMMAND);

        //Get the array with measurement values
        String[] measures = context.getResources().getStringArray(R.array.array_measurement_options);

        //Insert measurement values to MEASURE_table
        for (int i = 0; i < measures.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEASURE_MEASURE_COLUMN, measures[i]);
            db.insert(MEASURE_TABLE_NAME, null, contentValues);
        }


    }

    /**
     * Requred implementation of an abstract method.
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Db stays version 1, nothing to do here
    }


    public String createOrUpdateActiveListTable(SQLiteDatabase db) {
        //There is no active list -> create
        if (!activeList) {

            listsCount += 1; //New list in the family
            listTableName = LIST_TABLE_NAME_part_1 + listsCount; //Update active list name

            //Create SQL command to execute
            String LIST_TABLE_CREATE_COMMAND = "CREATE TABLE " + listTableName + " (" +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
                    AMOUNT_COLUMN + " REAL, " +
                    CHECKED_COLUMN + " INTEGER);";

            db.execSQL(LIST_TABLE_CREATE_COMMAND);//Create new list table

            //Create cursor for checked items in ITEMS_table
            Cursor checkedRowsInItemsCursor = db.query(ITEMS_TABLE_NAME,
                    new String[]{ID_COLUMN, AMOUNT_COLUMN},//columns to choose from
                    CHECKED_COLUMN + "=?", /*WHERE value, should be an expression
                        (and # of ? should match # if selectionArgs[])*/
                    new String[]{"1"}, // is 1
                    null, null, null);

            //cursor rows must be > 0 before pasting them to the active list
            if (checkedRowsInItemsCursor.getCount() > 0) {

                //Get ID_COLUMN index
                int idColumnIndex = checkedRowsInItemsCursor.getColumnIndex(ID_COLUMN);
                //Get AMOUNT_COLUMN index
                int amountColumnIndex = checkedRowsInItemsCursor.getColumnIndex(AMOUNT_COLUMN);
                //Move cursor to first row
                checkedRowsInItemsCursor.moveToFirst();

                do {

                    //Get ID_COLUMN value
                    int itemId = checkedRowsInItemsCursor.getInt(idColumnIndex);
                    //Get AMOUNT_COLUMN value
                    float itemAmount = checkedRowsInItemsCursor.getFloat(amountColumnIndex);
                    //Create contentValues var to store these values
                    ContentValues contentValues = new ContentValues();
                    //Put values into contentValues
                    contentValues.put(LIST_ITEM_COLUMN, itemId);
                    contentValues.put(AMOUNT_COLUMN, itemAmount);
                    //Put contentValues into new list table
                    db.insert(listTableName, null, contentValues);

                    //stop when reach the end of cursor
                } while (checkedRowsInItemsCursor.moveToNext());

                //close the cursor
                checkedRowsInItemsCursor.close();
            }

            //Create contentValues var to store values of new list record of the LOG_TABLE
            ContentValues contentValues = new ContentValues();
            //Put name value of new list table
            contentValues.put(NAME_COLUMN, listTableName);
            //Put creation date of new list table in ms
            contentValues.put(LOG_DATE_CREATED_COLUMN, System.currentTimeMillis());
            //Update LOG_TABLE with new list record
            db.insert(LOG_TABLE_NAME, null, contentValues);//add new record to LOG_table

            //list is active now
            activeList = true;

            return listTableName + "successfully created!";

            //There is active list -> update
        } else {

            //Create cursor for checked items in ITEMS_table
            Cursor checkedRowsInItemsCursor = db.query(ITEMS_TABLE_NAME,
                    new String[]{ID_COLUMN, AMOUNT_COLUMN},//columns to choose from
                    CHECKED_COLUMN + "=?", /*WHERE value, should be an expression
                        (and # of ? should match # if selectionArgs[])*/
                    new String[]{"1"}, // is 1
                    null, null, null);

            //cursor rows must be > 0 before pasting them to the active list
            if (checkedRowsInItemsCursor.getCount() > 0) {

                //Get ID_COLUMN index
                int idColumnIndex = checkedRowsInItemsCursor.getColumnIndex(ID_COLUMN);
                //Get AMOUNT_COLUMN index
                int amountColumnIndex = checkedRowsInItemsCursor.getColumnIndex(AMOUNT_COLUMN);
                //Move cursor to first row
                checkedRowsInItemsCursor.moveToFirst();

                do {

                    //Get ID_COLUMN value
                    int itemId = checkedRowsInItemsCursor.getInt(idColumnIndex);
                    //Get AMOUNT_COLUMN value
                    float itemAmount = checkedRowsInItemsCursor.getFloat(amountColumnIndex);
                    //Create contentValues var to store these values
                    ContentValues contentValues = new ContentValues();
                    //Put values into contentValues
                    contentValues.put(LIST_ITEM_COLUMN, itemId);
                    contentValues.put(AMOUNT_COLUMN, itemAmount);
                    //Put contentValues into active list table
                    db.insert(listTableName, null, contentValues);

                    //stop when reach the end of cursor
                } while (checkedRowsInItemsCursor.moveToNext());

                //close the cursor
                checkedRowsInItemsCursor.close();
            }

            return listTableName + "successfully updated!";
        }


    }



/*    public String createListTable(SQLiteDatabase db) {
        // TODO: 015 15 Jun 17 narrow to LOG_DATE_COMPLETE_COLUMN
        Cursor cursor = db.query(LOG_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();

        if (listsCount == 0
                || cursor.getString(cursor.getColumnIndex(LOG_DATE_COMPLETE_COLUMN)) != null) {

            cursor.close();
            listsCount += 1;
            listTableName = LIST_TABLE_NAME_part_1 + listsCount;
            String LIST_TABLE_CREATE_COMMAND = "CREATE TABLE " + listTableName + " (" +
                    ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LIST_ITEM_COLUMN + " INTEGER NOT NULL UNIQUE, " +
                    AMOUNT_COLUMN + " REAL, " +
                    CHECKED_COLUMN + " INTEGER);";
            db.execSQL(LIST_TABLE_CREATE_COMMAND);
            return listTableName + " created successfully";
        } else return "Error while creating proper listTable";
    }


    public boolean dropActiveListTable(SQLiteDatabase db) {
        // TODO: 015 15 Jun 17 narrow to LOG_DATE_COMPLETE_COLUMN
        Cursor cursor = db.query(LOG_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToLast();

        if (listsCount > 0 &&
                cursor.getString(cursor.getColumnIndex(LOG_DATE_COMPLETE_COLUMN)) == null) {

            listTableName = LIST_TABLE_NAME_part_1 + listsCount;
            String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + listTableName + ";";
            db.execSQL(LIST_TABLE_DROP_COMMAND);
            db.delete(LOG_TABLE_NAME, ID_COLUMN + "=?", new String[]{Integer.toString(listsCount)});
            listsCount -= 1;
            return true;
        } else return false;
    }*/

/*    public  dropListTable(SQLiteDatabase db) {


        listTableName = LIST_TABLE_NAME_part_1 + listsCount;
        String LIST_TABLE_DROP_COMMAND = "DROP TABLE " + listTableName + ";";
        listsCount -= 1;


        return LIST_TABLE_DROP_COMMAND;
    }*/

    public void updateListsCount(SQLiteDatabase db) {
        Cursor cursor = db.query(LOG_TABLE_NAME, null, null, null, null, null, null);
        listsCount = cursor.getCount();
    }


    public int getListsCount() {
        return listsCount;
    }

    public String getActiveListTableName() {
        return listTableName;
    }
}
