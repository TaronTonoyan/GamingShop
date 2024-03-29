package com.samsung.gamingshop.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.samsung.gamingshop.DBShop;
import com.samsung.gamingshop.R;
import com.samsung.gamingshop.models.Account;
import com.samsung.gamingshop.models.Order;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private DBShop DBConnector;
    private EditText address;
    private float total;
    private long id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initViews();
    }

    private void initViews() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Checkout");
        address = findViewById(R.id.confirmAddress);
        Button confirm = findViewById(R.id.checkout);
        DBConnector = new DBShop(this);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.click);
        id = getIntent().getLongExtra(getString(R.string.account), -1);
        Account acc = DBConnector.selectAcc(id);
        total = getIntent().getFloatExtra(getString(R.string.total_price), 0);
        String orders = getIntent().getStringExtra(getString(R.string.order));
        address.setText(DBConnector.selectAcc(id).getAddress());

        confirm.setOnClickListener(v -> {
            mediaPlayer.start();
            if (address.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Please write an address for us", Toast.LENGTH_SHORT).show();
            } else {
                DBConnector.insertHistory(acc.getId(), total, address.getText().toString(), orders);
                deleteCartOrders();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void deleteCartOrders() {
        ArrayList<Order> arr = DBConnector.selectAllOrders();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getOwner() == id && arr.get(i).getIsWishlist() == 0) {
                DBConnector.deleteOrder(arr.get(i).getId());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
