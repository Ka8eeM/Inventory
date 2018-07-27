package com.example.karim.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.example.karim.inventory.database.InventorHelper;

import static com.example.karim.inventory.database.InventoryContract.InventoryEntry;

public class ProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listProducts;
    private InventorCursorAdapter cursorAdapter;
    private static final int INVENTOR_LOADER = 0;
    private InventorHelper helper;


    // TODO 1  update the quantity in this activity
    // TODO 2  insert picture
    // TODO 3  on back confirmation


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        FloatingActionButton fab = findViewById(R.id.add_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        listProducts = findViewById(R.id.list_view);
        helper = new InventorHelper(this);
        cursorAdapter = new InventorCursorAdapter(this, null);
        listProducts.setAdapter(cursorAdapter);

        // initialize getting data from database in background
        getLoaderManager().initLoader(INVENTOR_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY
        };
        return new CursorLoader(
                this,
                InventoryEntry.CONTETN_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}
