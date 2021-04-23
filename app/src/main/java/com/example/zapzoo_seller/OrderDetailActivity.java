package com.example.zapzoo_seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapzoo_seller.adapter.OrderProductListAdapter;
import com.example.zapzoo_seller.models.Product;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private List<Product> productList;
    private Toolbar toolbar;
    private String o_id;
    private RecyclerView recyclerView;
    private OrderProductListAdapter adapter;
    private ImageView imageView;
    private TextView orderid, date, status, name, mobileNumber, email;
    private LinearLayout acceptreject;
    private RelativeLayout threeSeekBar;
    private SeekBar seekBar;
    private OkHttpClient client;
    private SharedPreferences preferences;
    private CardView actions;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();
        preferences = getSharedPreferences("data", MODE_PRIVATE);

        Intent intent = getIntent();
        o_id = intent.getStringExtra("orderid");
        username = intent.getStringExtra("username");
        productList = (List<Product>) intent.getExtras().getSerializable("products");

        getSupportActionBar().setTitle("Respond to orders");

        imageView = findViewById(R.id.imageView);
        orderid = findViewById(R.id.orderid);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        name = findViewById(R.id.name);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        acceptreject = findViewById(R.id.acceptreject);
        threeSeekBar = findViewById(R.id.threeSeekBar);
        seekBar = findViewById(R.id.seekBar);
        actions = findViewById(R.id.actionsCV);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        //Date d = new Date(o_id);
        Timestamp timeStampDate = new Timestamp(Long.parseLong(o_id));

        orderid.setText("Order ID:"+o_id);
        date.setText("Date: "+timeStampDate.toString());
        status.setText("⚫ "+intent.getStringExtra("status"));

        adapter = new OrderProductListAdapter(productList, this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Toast.makeText(OrderDetailActivity.this, "Position "+i+" bool "+b, Toast.LENGTH_SHORT).show();
                if(b && i == 0)
                {
                    changeStatus("Accepted");
                } else if(b && i == 1)
                {
                    changeStatus("In Transit");
                } else if(b && i == 2)
                {
                    changeStatus("Delivered");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(status.getText().toString().contains("Accepted") || status.getText().toString().contains("Paid"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
        } else if(status.getText().toString().contains("Rejected"))
        {
            actions.setVisibility(View.GONE);
        } else if(status.getText().toString().contains("In Transit"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(1);
        } else if(status.getText().toString().contains("Delivered"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(2);
        }

        fetchCustomerInfo();
    }

    private void changeStatus(String s) {

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", String.valueOf(Long.parseLong(o_id)))
                .add("username", username)
                .add("status", ""+s)
                .build();

        Request request = new Request.Builder()
                .url("https://zapzoo.devsourabh.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            acceptreject.setVisibility(View.GONE);
                            threeSeekBar.setVisibility(View.VISIBLE);
                            status.setText("⚫ "+s);
                        }
                    }
                });
            }
        });

    }

    private void fetchCustomerInfo() {
        //https://dummyimage.com/600x400/000/fff&text=AB
        Glide.with(this)
                .load("https://dummyimage.com/130x130/26c4c1/fff&text=AB")
                .into(imageView);

        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .build();

        Request request = new Request.Builder()
                .url("https://zapzoo.devsourabh.repl.co/getProfile")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            try {
                                JSONObject object = new JSONObject(res);
                                name.setText("Name: "+object.getString("fullname"));
                                mobileNumber.setText("Customer Mobile: "+object.getString("phoneNumber"));
                                email.setText("Customer Email: "+object.getString("email"));
                                Glide.with(OrderDetailActivity.this)
                                        .load("https://dummyimage.com/130x130/26c4c1/fff&text="+object.getString("fullname").toString().substring(0, 2))
                                        .into(imageView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public void onAccepted(View view) {

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", String.valueOf(Long.parseLong(o_id)))
                .add("username", username)
                .add("status", "Accepted")
                .build();

        Request request = new Request.Builder()
                .url("https://zapzoo.devsourabh.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            acceptreject.setVisibility(View.GONE);
                            threeSeekBar.setVisibility(View.VISIBLE);
                            status.setText("⚫ Accepted");
                        }
                    }
                });
            }
        });
    }

    public void onRejected(View view) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", o_id)
                .add("username", username)
                .add("status", "Rejected")
                .build();

        Request request = new Request.Builder()
                .url("https://zapzoo.devsourabh.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            actions.setVisibility(View.GONE);
                            status.setText("⚫ Rejected");
                            //acceptreject.setVisibility(View.GONE);
                            //threeSeekBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}