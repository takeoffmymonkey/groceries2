package com.example.android.groceries2;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.groceries2.data.ListCursorAdapter;

import static com.example.android.groceries2.MainActivity.db;
import static com.example.android.groceries2.MainActivity.dbHelper;


/**
 * Created by takeoff on 002 02 Jun 17.
 */

public class ListFragment extends Fragment {

    View listView;

    static ListCursorAdapter listCursorAdapter;

    public ListFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        listView = inflater.inflate(R.layout.tab_list, container, false);

        FloatingActionButton fabCompleteList =
                (FloatingActionButton) listView.findViewById(R.id.fab_complete_list);

        //Set approve list action to fab
        fabCompleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.approveCurrentList();
                ListFragment.refreshListCursor();
                LogFragment.refreshLogCursor();
                ItemsFragment.refreshItemsCursor();
                //Inform user
                Toast.makeText(listView.getContext(), "List marked as complete", Toast.LENGTH_SHORT).show();

                MainActivity.selectTab(2);
            }
        });


        // Find the ListView which will be populated with the pet data
        ListView listListView = (ListView) listView.findViewById(R.id.list_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.

        View emptyView = listView.findViewById(R.id.list_empty_view);

        listListView.setEmptyView(emptyView);

        Cursor cursor = db.query(dbHelper.getLatestListName(), null,
                null, null, null, null, null);

        listCursorAdapter = new ListCursorAdapter(getContext(), cursor, 0);

        listListView.setAdapter(listCursorAdapter);

        setHasOptionsMenu(true);

        return listView;

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.settings_option_mark_as_complete:
                dbHelper.approveCurrentList();
                ListFragment.refreshListCursor();
                LogFragment.refreshLogCursor();
                ItemsFragment.refreshItemsCursor();
                //Inform user
                Toast.makeText(getContext(), "List marked as complete", Toast.LENGTH_SHORT).show();

                MainActivity.selectTab(2);

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.settings_option_delete_list:
                dbHelper.deleteListTable(dbHelper.getActiveListVersion());
                Toast.makeText(getContext(), "List deleted", Toast.LENGTH_SHORT).show();
                ListFragment.refreshListCursor();
                LogFragment.refreshLogCursor();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void refreshListCursor() {

        class NewListCursor extends AsyncTask<Integer, Void, Cursor> {

            //Actions to perform in main thread before background execusion
            @Override
            protected void onPreExecute() {
            }

            //Actions to perform on background thread
            @Override
            protected Cursor doInBackground(Integer... params) {
                Log.e("WARNING: ", "IN refreshListCursor, active list name: " + dbHelper.getActiveListName());
                Cursor cursor = db.query(dbHelper.getActiveListName(), null, null, null, null, null, null);
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                listCursorAdapter.changeCursor(cursor);
            }
        }

        new NewListCursor().execute(0);

    }

}
