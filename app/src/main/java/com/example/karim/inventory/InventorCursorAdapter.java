package com.example.karim.inventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.karim.inventory.database.InventoryContract.InventoryEntry;

public class InventorCursorAdapter extends CursorAdapter {
    private Context context;

    public InventorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        cursor.moveToFirst();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                Log.e(ProductsActivity.class.getSimpleName(), "Ok ok ok ");
                long id = cursor.getPosition();
                Log.e(InventorCursorAdapter.class.getSimpleName(), "id is: " + id);
                Uri curUri = ContentUris.withAppendedId(InventoryEntry.CONTETN_URI, id);
                intent.setData(curUri);
                context.startActivity(intent);
            }
        });
        return view;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "it is ok Sale.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productName = view.findViewById(R.id.tv_product_name);
        TextView productPrice = view.findViewById(R.id.tv_price);
        TextView availableQuantity = view.findViewById(R.id.tv_quantity);
        Button btnSale = view.findViewById(R.id.btn_sale);
        String pName = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME));
        String pPrice = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE));
        String avQuantity = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_AVAILAVLE_QUANTITY));
        productName.setText(pName);
        Log.e(InventorCursorAdapter.class.getSimpleName(), "price is: " + pPrice);
        productPrice.setText(pPrice + "$");
        availableQuantity.setText(avQuantity);
        btnSale.setOnClickListener(listener);
    }
}