package com.example.zapzoo_seller.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zapzoo_seller.OrderDetailActivity;
import com.example.zapzoo_seller.R;
import com.example.zapzoo_seller.adapter.AllOrdersAdapter;
import com.example.zapzoo_seller.models.Order;
import com.example.zapzoo_seller.models.Product;
import com.example.zapzoo_seller.util.AllOrdersItemOnClickListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OkHttpClient client;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Order> orderList;
    private SharedPreferences preferences;
    private AllOrdersAdapter allOrdersAdapter;
    private AllOrdersItemOnClickListener listener;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .build();

        Request request = new Request.Builder()
                .url("https://zapzoo.devsourabh.repl.co/seller/getOrder/allorders")
                .post(formBody)
                .build();

        listener = new AllOrdersItemOnClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("orderid", ""+orderList.get(position).getTimestamp().getTime());
                intent.putExtra("username", ""+orderList.get(position).getUsername());
                intent.putExtra("status", ""+orderList.get(position).getStatus());
                intent.putExtra("products", (Serializable) orderList.get(position).getProductList());
                startActivity(intent);
            }
        };

        orderList = new ArrayList<>();
        allOrdersAdapter = new AllOrdersAdapter(getActivity(), orderList, listener);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allOrdersAdapter);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
                Log.d("#####", ""+res);

                try {
                    JSONArray jsonArray = new JSONArray(res);
                    for(int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Order order = new Order();
                        order.setId(jsonObject.getString("_id"));
                        order.setUsername(jsonObject.getString("username"));
                        order.setTimestamp(new Timestamp(jsonObject.getLong("timestamp")));
                        order.setShop_id(jsonObject.getString("shop_id"));
                        order.setStatus(jsonObject.getString("status"));

                        JSONArray productsArray = jsonObject.getJSONArray("products");
                        List<Product> products = new ArrayList<>();

                        for(int j=0; j<productsArray.length(); j++)
                        {
                            JSONObject productObject = productsArray.getJSONObject(j);
                            Product product = new Product();
                            product.setId(productObject.getString("id"));
                            product.setName(productObject.getString("name"));
                            product.setImage_url(productObject.getString("image_url"));
                            product.setPrice(productObject.getInt("price"));
                            product.setQuantity(productObject.getInt("quantity"));

                            products.add(product);
                        }

                        order.setProductList(products);
                        orderList.add(order);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allOrdersAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        return root;
    }
}