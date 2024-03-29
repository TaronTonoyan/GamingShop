package com.samsung.gamingshop.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.samsung.gamingshop.DBShop;
import com.samsung.gamingshop.R;
import com.samsung.gamingshop.adapters.GalleryAdapter;
import com.samsung.gamingshop.models.Product;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private DBShop DBConnector;
    private ListView list;
    private GalleryAdapter adapter;
    private Button category;
    private int categoryCode;
    private View loadingScreen;
    private long id;

    private final int CATEGORY_REQUEST_CODE = 1;
    private final int CART_ACTIVITY_REQUEST_CODE = 2;
    private final int ACCOUNT_SETTINGS_ACTIVITY_REQUEST_CODE = 3;
    private final int LOG_OUT_CONFIRMATION_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initViews();
    }

    private void initViews() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        list = findViewById(R.id.listView);
        category = findViewById(R.id.category);
        DBConnector = new DBShop(this);
        loadingScreen = findViewById(R.id.loading_screen);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click);
        id = getIntent().getLongExtra(getString(R.string.account), -1);
        categoryCode = 0;

        if (id > 0) {
            actionBar.setTitle("Gallery");
        } else if (id == -2){
            actionBar.setTitle("Delete Product");
        } else {
            actionBar.setTitle("Update Product");
        }
        category.setOnClickListener(v -> {
            mediaPlayer.start();
            Intent intent = new Intent(GalleryActivity.this, ProductCategoryActivity.class);
            intent.putExtra(getString(R.string.sort), true);
            //noinspection deprecation
            startActivityForResult(intent, CATEGORY_REQUEST_CODE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingScreen.setVisibility(View.VISIBLE);
        new LoadProductsTask().execute();
    }

    private Product[] getProducts() {
        ArrayList<Product> productArr = DBConnector.selectAllProducts();
        ArrayList<Product> sortedArr = new ArrayList<>();
        for (int i = 0; i < productArr.size(); i++) {
            switch (categoryCode) {
                case 0:
                    category.setText(getString(R.string.category));
                    sortedArr.add(productArr.get(i));
                    break;
                case 1:
                    category.setText(getString(R.string.console));
                    if (productArr.get(i).getCategory().equals(getString(R.string.console))) {
                        sortedArr.add(productArr.get(i));
                    }
                    break;
                case 2:
                    category.setText(getString(R.string.accessory));
                    if (productArr.get(i).getCategory().equals(getString(R.string.accessory))) {
                        sortedArr.add(productArr.get(i));
                    }
                    break;
                case 3:
                    category.setText(getString(R.string.game));
                    if (productArr.get(i).getCategory().equals(getString(R.string.game))) {
                        sortedArr.add(productArr.get(i));
                    }
                    break;
            }
        }
        Product[] arr = new Product[sortedArr.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = sortedArr.get(i);
        }
        return arr;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -2) {
            goToLoginPage();
        }
        if (requestCode == CATEGORY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                categoryCode = data.getIntExtra(getString(R.string.category), 0);
            }
        }
        if (requestCode == CART_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), getString(R.string.order_delivered), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == ACCOUNT_SETTINGS_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                goToLoginPage();
            }
        }
        if (requestCode == LOG_OUT_CONFIRMATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                goToLoginPage();
            }
        }
    }

    private void goToLoginPage() {
        Intent intent = new Intent();
        setResult(-2, intent);
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int WISHLIST_ACTIVITY_REQUEST_CODE = 5;
        int HISTORY_ACTIVITY_REQUEST_CODE = 6;
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.cart:
                intent = new Intent(GalleryActivity.this, CartActivity.class);
                intent.putExtra(getString(R.string.cart), id);
                //noinspection deprecation
                startActivityForResult(intent, CART_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.wishlist:
                intent = new Intent(GalleryActivity.this, WishlistActivity.class);
                intent.putExtra(getString(R.string.wishlist), id);
                //noinspection deprecation
                startActivityForResult(intent, WISHLIST_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.history:
                intent = new Intent(GalleryActivity.this, HistoryActivity.class);
                intent.putExtra(getString(R.string.account), id);
                //noinspection deprecation
                startActivityForResult(intent, HISTORY_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.accountSettings:
                intent = new Intent(GalleryActivity.this, AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.account), id);
                //noinspection deprecation
                startActivityForResult(intent, ACCOUNT_SETTINGS_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.logOut:
                intent = new Intent(GalleryActivity.this, SureActivity.class);
                intent.putExtra(getString(R.string.yes), id);
                //noinspection deprecation
                startActivityForResult(intent, LOG_OUT_CONFIRMATION_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (id < 1) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.gallery_menu, menu);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.filter(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class LoadProductsTask extends AsyncTask<Void, Void, Product[]> {
        @Override
        protected Product[] doInBackground(Void... voids) {
            // Load products in background thread
            return getProducts();
        }

        @Override
        protected void onPostExecute(Product[] products) {
            // Update adapter with loaded products and hide loading screen
            adapter = new GalleryAdapter(GalleryActivity.this, products, id);
            list.setAdapter(adapter);
            loadingScreen.setVisibility(View.GONE);
        }
    }

}
