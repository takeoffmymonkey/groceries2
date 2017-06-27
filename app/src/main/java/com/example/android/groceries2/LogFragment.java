package com.example.android.groceries2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.groceries2.data.LogCursorAdapter;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.MainActivity.dbHelper;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_CODE_COLUMN;
import static com.example.android.groceries2.data.GroceriesDbHelper.LOG_TABLE_NAME;

/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class LogFragment extends Fragment {


    View logView;
    static LogCursorAdapter logCursorAdapter;


    ProgressBar progressBar;

    public LogFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        logView = inflater.inflate(R.layout.tab_log, container, false);

        progressBar = (ProgressBar) logView.findViewById(R.id.log_progress_bar);
        progressBar.setVisibility(View.GONE);


        // Find the ListView which will be populated with the pet data
        final ListView logListView = (ListView) logView.findViewById(R.id.log_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = logView.findViewById(R.id.log_empty_view);

        logListView.setEmptyView(emptyView);

        TextView logEmptyText = (TextView) logView.findViewById(R.id.log_empty_text);
        logEmptyText.setText("No created lists");

        TextView logEmptyTextSub = (TextView) logView.findViewById(R.id.log_empty_text_sub);
        logEmptyTextSub.setText("Please form a list in ITEMS");

        class LogBackgroundCursor extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... params) {
                //Create cursor
                Cursor cursor = db.query(LOG_TABLE_NAME, null,
                        null, null, null, null, LOG_CODE_COLUMN + " DESC");
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
            case R.id.settings_log_delete_all_lists:

                //Create alert dialog object
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                                        progressBar.setVisibility(View.VISIBLE);

                                        new LogBackgroundTasks().execute();


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


                // Respond to a click on the "Delete all entries" menu option

        }
        return super.onOptionsItemSelected(item);
    }


    class LogBackgroundTasks extends AsyncTask<Integer, Void, Boolean> {

        //Actions to perform on background thread
        @Override
        protected Boolean doInBackground(Integer... params) {
            dbHelper.deleteAll(0);
            LogFragment.refreshLogCursor(null, null, 0);
            ListFragment.refreshListCursor(null, null, 0);
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.GONE);
        }
    }


    public static void refreshLogCursor(@Nullable Context context,
                                        @Nullable String toast, @Nullable final int length) {

        class NewLogCursor extends AsyncTask<Integer, Void, Cursor> {

            Context context;
            String toast;
            int length;

            public NewLogCursor() {
                super();
            }


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
                        LOG_CODE_COLUMN + " DESC");
                return cursor;

            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                logCursorAdapter.changeCursor(cursor);
                if (toast != null) {
                    Toast.makeText(context, toast, length).show();
                }
            }
        }

        new NewLogCursor(context, toast, length).execute(1);

    }

}
