package com.example.android.groceries2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.groceries2.R;
import com.example.android.groceries2.activities.MainActivity;

import static com.example.android.groceries2.activities.MainActivity.db;
import static com.example.android.groceries2.db.GroceriesDbHelper.ID_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.ITEMS_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_AMOUNT_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.LIST_ITEM_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.MEASURE_TABLE_NAME;
import static com.example.android.groceries2.db.GroceriesDbHelper.NAME_COLUMN;
import static com.example.android.groceries2.db.GroceriesDbHelper.PRICE_COLUMN;

/**
 * Created by takeoff on 021 21 Jun 17.
 */

public class ListLogCursorAdapter extends CursorAdapter {
    public ListLogCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in item_list.xml
        return LayoutInflater.from(context).inflate(R.layout.item_activity_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        //Create itemPrice textView object
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.activity_list_item_price);
        //Set price
        itemPriceTextView.setText(MainActivity.formatPrice(cursor
                .getFloat(cursor.getColumnIndex(PRICE_COLUMN))));

        //Create checkBox object
        TextView itemName = (TextView) view.findViewById(R.id.activity_list_item_checkbox);
        //Get item's code
        String itemNameString = cursor.getString(cursor.getColumnIndex(LIST_ITEM_COLUMN));

        //Get cursor with NAME_COLUMN and ITEMS_MEASURE_COLUMN columns for required ID


        itemName.setText(itemNameString);

        //Get code of the items measure
        int measureInt = cursor
                .getInt(cursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close itemsTableCursor cursor


        //Get cursor with MEASURE_MEASURE_COLUMN text from Measure_table
        Cursor measureTableCursor = db.query(MEASURE_TABLE_NAME,
                new String[]{MEASURE_COLUMN},
                ID_COLUMN + "=?", new String[]{Integer.toString(measureInt)},
                null, null, null);

        //Move cursor to 1st row
        measureTableCursor.moveToFirst();

        //Save string with proper measure
        String measure = measureTableCursor
                .getString(measureTableCursor.getColumnIndexOrThrow(MEASURE_COLUMN));

        //Close measureTableCursor cursor
        measureTableCursor.close();

        //Create itemAmount textView object
        TextView itemAmountTextView = (TextView) view.findViewById(R.id.activity_list_item_amount);

        float itemAmount = cursor.getFloat(cursor.getColumnIndexOrThrow(LIST_AMOUNT_COLUMN));
        //Set itemAmount + measure as text to itemAmount textView


        //Get the rounded value
        int itemAmountRound = Math.round(itemAmount);

        //Check if it is round
        if (itemAmount == itemAmountRound) {
            //It is round

            //Check if it is 1 item
            if (measure.equals("items") && itemAmountRound == 1) {
                //it is 1 item

                //set appropriate text
                itemAmountTextView.setText("" + itemAmountRound + " item");

            } else {
                //It is round but not 1
                itemAmountTextView.setText(itemAmountRound + " " + measure);

            }


        } else {
            //it is not round
            itemAmountTextView.setText(Float.toString(itemAmount) + " " + measure);
        }


    }
}
