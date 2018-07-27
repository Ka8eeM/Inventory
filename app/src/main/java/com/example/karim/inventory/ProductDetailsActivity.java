package com.example.karim.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karim.inventory.database.InventorHelper;

import static com.example.karim.inventory.database.InventoryContract.InventoryEntry;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Intent intent;
    private Uri curUri;
    private InventorHelper helper;
    private ImageView productPic;
    private TextView productName, productPrice, availQuantity, supplier;
    private Button increase, order, confirmSetQuantity;
    private EditText setQuantity;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private boolean change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        setTitle(R.string.product_detail);
        helper = new InventorHelper(this);
        intent = getIntent();
        productPic = findViewById(R.id.product_pic);
        productName = findViewById(R.id.product_name_detail);
        productPrice = findViewById(R.id.product_price_detail);
        availQuantity = findViewById(R.id.tv_current_quantity);
        supplier = findViewById(R.id.tv_supplier);
        increase = findViewById(R.id.increase_quantity);
        confirmSetQuantity = findViewById(R.id.confirm_set_quantity);
        order = findViewById(R.id.order_product);
        setQuantity = findViewById(R.id.ed_set_quantaty);
        curUri = intent.getData();
        confirmSetQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmQuantity();
            }
        });
        change = false;
        setQuantity.setOnTouchListener(listener);
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            change = true;
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (!change) {
            super.onBackPressed();
            return;
        }
    }

    private void confirmQuantity() {
        String temp = setQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(temp))
            return;
        availQuantity.setText(temp);
        saveProduct();
        setQuantity.setText("");
    }

    private void saveProduct() {
        String temp = availQuantity.getText().toString().trim();

        Log.e(ProductDetailsActivity.class.getSimpleName(), "quantity is = " + temp + "from save product");
        if (curUri == null && TextUtils.isEmpty(temp))
            return;
        int curQuantity = Integer.parseInt(temp);
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY, curQuantity);
        int rowsAffected = getContentResolver().update(curUri, values, null, null);
        if (rowsAffected == 0)
            Toast.makeText(this, getString(R.string.update_quantity_failed), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getString(R.string.update_quantity_successful), Toast.LENGTH_SHORT).show();
    }

    public void makeChange(View view) {
        String temp = availQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(temp))
            return;
        Log.e(ProductDetailsActivity.class.getSimpleName(), "quantity is = " + temp);
        int curQuantity = Integer.parseInt(temp);
        if (view.getId() == increase.getId()) {
            curQuantity++;
        } else {
            if (curQuantity > 0) {
                curQuantity--;
            }
        }
        availQuantity.setText(String.valueOf(curQuantity));
        saveProduct();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (!change) {
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener listen = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    }
                };
                showUnsavedChangesDialog(listen);
                return true;
            case R.id.delete_action:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener click) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, click);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (curUri != null) {
            int rowsDeleted = getContentResolver().delete(curUri, null, null);
            if (rowsDeleted == 0)
                Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_PRODUCT_PICTURE,
                InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_SUPPLIERS
        };
        return new CursorLoader(
                this,
                curUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;
        if (data.moveToFirst()) {
            int nameIndexCol = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceIndexCol = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int supplierIndexCol = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIERS);
            int quantityIndexCol = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY);
            int pictureIndexCol = data.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRODUCT_PICTURE);

            productName.setText(data.getString(nameIndexCol));
            productPrice.setText(String.valueOf(data.getInt(priceIndexCol)) + "$");
            supplier.setText(data.getString(supplierIndexCol));
            availQuantity.setText(String.valueOf(data.getInt(quantityIndexCol)));
            //byte[] arrayPic = data.getBlob(pictureIndexCol);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(arrayPic, 0, arrayPic.length);
            //productPic.setImageBitmap(bitmap);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setQuantity.setText("");
    }
}