package com.example.android.groceries2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.R;
import com.example.android.groceries2.adapters.HistoryCursorAdapter;
import com.example.android.groceries2.fragments.ItemsFragment;
import com.example.android.groceries2.fragments.ListFragment;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.activities.MainActivity.dbHelper;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_DATE_CREATED_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.LOG_VERSION_COLUMN;

/**
 * Created by takeoff on 007 07 Jul 17.
 */

public class HistoryActivity extends AppCompatActivity {

    static HistoryCursorAdapter historyCursorAdapter;

    public static ProgressBar historyProgressBar;
    public static View historyProgressBarBg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);


        Log.w("WARNING: ", "IN ONCREATEVIEW OF LOG FRAGMENT");


        historyProgressBar = (ProgressBar) findViewById(R.id.history_progress_bar2);
        historyProgressBar.setVisibility(View.VISIBLE);


        historyProgressBarBg = findViewById(R.id.history_progress_bar_bg);
        historyProgressBarBg.setVisibility(View.VISIBLE);

        // Find the ListView which will be populated with the pet data
        final ListView historyListView = (ListView) findViewById(R.id.history_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.history_empty_view);

        historyListView.setEmptyView(emptyView);

        TextView historyEmptyText = (TextView) findViewById(R.id.history_empty_text);
        historyEmptyText.setText("No created lists");

        TextView historyEmptyTextSub = (TextView) findViewById(R.id.history_empty_text_sub);
        historyEmptyTextSub.setText("Please form a list in ITEMS");

        class HistoryBackgroundCursor extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... params) {
                //Create cursor
                Cursor cursor = db.query(LOG_TABLE_NAME, null,
                        null, null, null, null, LOG_DATE_CREATED_COLUMN + " DESC");
                //Create cursor adapter object and pass cursor there
                historyCursorAdapter = new HistoryCursorAdapter(HistoryActivity.this, cursor, 0);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                //Set adapter to the grid view
                historyListView.setAdapter(historyCursorAdapter);

                historyProgressBar.setVisibility(View.GONE);
                historyProgressBarBg.setVisibility(View.GONE);
                historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Cursor cursorLogTable = db.query(LOG_TABLE_NAME,
                                new String[]{LOG_VERSION_COLUMN},
                                ID_COLUMN + "=?", new String[]{Long.toString(id)},
                                null, null, null);

                        if (cursorLogTable.getCount() == 1) {

                            cursorLogTable.moveToFirst();
                            int listVersion = cursorLogTable.getInt(cursorLogTable
                                    .getColumnIndex(LOG_VERSION_COLUMN));

                            cursorLogTable.close();

                            String listName = "List_" + listVersion;
                            Intent intent = new Intent(HistoryActivity.this, ListInfoActivity.class);
                            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra("listName", listName);
                            intent.putExtra("listVersion", listVersion);
                            view.getContext().startActivity(intent);


                        } else {

                            cursorLogTable.close();
                            Log.e("WARNING: ", "cursor count: "
                                    + cursorLogTable.getCount() + ", must be: 1");

                        }


                    }
                });
            }
        }

        new HistoryBackgroundCursor().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case android.R.id.home:
                //Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("tab", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);

                //startActivity(intent);
                return true;

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_log_delete_all_lists:

                Cursor cursorCheckItemsTable = db.query(LOG_TABLE_NAME,
                        new String[]{ID_COLUMN},
                        ID_COLUMN + "=?", new String[]{Integer.toString(1)},
                        null, null, null);

                if (cursorCheckItemsTable.getCount() > 0) {
                    //table has at least 1 item
                    cursorCheckItemsTable.close();

                    //Create alert dialog object
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    //Set title of the dialog
                    builder.setTitle("Delete all lists")
                            //Set custom view of the dialog
                            .setMessage("Are you sure you want to delete all lists?")
                            //Set ability to press back
                            .setCancelable(true)
                            //Set Ok button with click listener
                            .setPositiveButton("Delete all",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            historyProgressBar.setVisibility(View.VISIBLE);
                                            historyProgressBarBg.setVisibility(View.VISIBLE);

                                            new HistoryBackgroundTasks(HistoryActivity.this, "Lists deleted",
                                                    Toast.LENGTH_SHORT).execute();

                                            dialog.cancel();

                                        }
                                    })

                            //Set cancel button with click listener
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Close the dialog window
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    //close cursor
                    cursorCheckItemsTable.close();
                    //Inform user
                    Toast.makeText(HistoryActivity.this, "No lists to delete!", Toast.LENGTH_LONG).show();
                }

                // Respond to a click on the "Delete all entries" menu option

        }
        return super.onOptionsItemSelected(item);
    }


    class HistoryBackgroundTasks extends AsyncTask<Integer, Void, Boolean> {

        Context context;
        String toast;
        int length;

        public HistoryBackgroundTasks() {
            super();
        }


        public HistoryBackgroundTasks(Context context, String toast, int length) {
            super();
            this.context = context;
            this.toast = toast;
            this.length = length;
        }


        //Actions to perform on background thread
        @Override
        protected Boolean doInBackground(Integer... params) {
            dbHelper.deleteAll(0);
            HistoryActivity.refreshHistoryCursor(null, null, 0);
            ListFragment.refreshListCursor(null, null, 0);
            ItemsFragment.refreshItemsCursor(null, null, 0);
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            historyProgressBar.setVisibility(View.GONE);
            historyProgressBarBg.setVisibility(View.GONE);

            if (toast != null) {
                Toast.makeText(context, toast, length).show();
            }
        }
    }


    public static void refreshHistoryCursor(@Nullable Context context,
                                            @Nullable String toast, @Nullable final int length) {

        class NewLogCursor extends AsyncTask<Integer, Void, Cursor> {

            Context context;
            String toast;
            int length;

            public NewLogCursor(Context context, String toast, int length) {
                super();
                this.context = context;
                this.toast = toast;
                this.length = length;
            }

            //Actions to perform in main thread before background execusion
            @Override
            protected void onPreExecute() {
            }

            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Cursor cursor = db.query(LOG_TABLE_NAME, null, null, null, null, null,
                        LOG_DATE_CREATED_COLUMN + " DESC");
                return cursor;

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                historyCursorAdapter.changeCursor(cursor);
                historyProgressBar.setVisibility(View.GONE);
                historyProgressBarBg.setVisibility(View.GONE);
                if (toast != null) {
                    Toast.makeText(context, toast, length).show();
                }
            }
        }

        new NewLogCursor(context, toast, length).execute(1);

    }

}
