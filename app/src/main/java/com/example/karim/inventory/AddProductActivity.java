package com.example.karim.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.karim.inventory.database.InventorHelper;
import com.example.karim.inventory.database.InventoryContract;

public class AddProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InventorCursorAdapter cursorAdapter;
    private ImageView productImage;
    private EditText productName, productSupplier, productPrice, productQuantity;
    private Button addNew;
    private InventorHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle(getString(R.string.add_product));
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.ed_pname);
        productPrice = findViewById(R.id.ed_pprice);
        productQuantity = findViewById(R.id.ed_pquantity);
        productSupplier = findViewById(R.id.ed_psupplier);
        addNew = findViewById(R.id.add_new);
        helper = new InventorHelper(this);
        cursorAdapter = new InventorCursorAdapter(this, null);
        productImage.setImageResource(R.mipmap.ic_launcher);
    }


    public void addNewProduct(View view) {
        String name = productName.getText().toString().trim();
        String price = productPrice.getText().toString().trim();
        String quantity = productQuantity.getText().toString().trim();
        String supplier = productSupplier.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(quantity)
                || TextUtils.isEmpty(supplier)) {
            Toast.makeText(this, "Please fill all the data", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME, name);
            int pricee = Integer.parseInt(price);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE, pricee);
            int quan = Integer.parseInt(quantity);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY, quan);
            values.put(InventoryContract.InventoryEntry.COLUMN_INVENTORY_SUPPLIERS, supplier);
            getContentResolver().insert(InventoryContract.InventoryEntry.CONTETN_URI, values);
            Log.e(AddProductActivity.class.getSimpleName(), "Values size is: " + values.size());
            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void uploadPicture(View view) {
        productImage.setImageResource(R.mipmap.ic_launcher);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY
        };
        return new CursorLoader(
                this,
                InventoryContract.InventoryEntry.CONTETN_URI,
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
