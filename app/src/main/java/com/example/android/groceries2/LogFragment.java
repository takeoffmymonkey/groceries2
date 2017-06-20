package com.example.android.groceries2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.example.android.groceries2.data.GroceriesDbHelper;
import com.example.android.groceries2.data.ItemsCursorAdapter;
import com.example.android.groceries2.data.LogCursorAdapter;


import static com.example.android.groceries2.ItemsFragment.itemsCursorAdapter;
import static com.example.android.groceries2.ListFragment.listCursorAdapter;
import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.data.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_TABLE_NAME;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class LogFragment extends Fragment {


    View logView;
    static LogCursorAdapter logCursorAdapter;


    public LogFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        logView = inflater.inflate(R.layout.tab_log, container, false);


        // Find the ListView which will be populated with the pet data
        final ListView logListView = (ListView) logView.findViewById(R.id.log_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = logView.findViewById(R.id.log_empty_view);

        logListView.setEmptyView(emptyView);

        class LogBackgroundCursor extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... params) {
                //Create cursor
                Cursor cursor = db.query(LOG_TABLE_NAME, null,
                        null, null, null, null, null);
                //Create cursor adapter object and pass cursor there
                logCursorAdapter = new LogCursorAdapter(getContext(), cursor, 0);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                //Set adapter to the grid view
                logListView.setAdapter(logCursorAdapter);
            }
        }

        new LogBackgroundCursor().execute();


        setHasOptionsMenu(true);

        return logView;


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_delete_all_lists:
                return true;
            // Respond to a click on the "Delete all entries" menu option

        }
        return super.onOptionsItemSelected(item);
    }


    public static void refreshLogCursor() {

        class NewLogCursor extends AsyncTask<Integer, Void, Cursor> {

            //Actions to perform in main thread before background execusion
            @Override
            protected void onPreExecute() {
            }

            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Cursor cursor = db.query(LOG_TABLE_NAME, null, null, null, null, null, null);
                return cursor;

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                logCursorAdapter.changeCursor(cursor);
            }
        }

        new NewLogCursor().execute(1);

    }

}
