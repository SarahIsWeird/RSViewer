package com.sarahisweird.rsviewer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sarahisweird.rsviewer.adapters.ItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ItemAdapter itemAdapter;

    private void update(final Context context, final RecyclerView recyclerView) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://cvps.sarahisweird.com/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                itemAdapter.items = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        itemAdapter.items.add(new Item(jsonObject.getString("name"),
                                jsonObject.getString("label"),
                                jsonObject.getLong("size"),
                                jsonObject.getInt("maxSize"),
                                jsonObject.getInt("damage")));
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                itemAdapter.items.sort(new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return (int) (o2.size - o1.size);
                    }
                });

                itemAdapter.filterItems("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;
        final RecyclerView recyclerView = findViewById(R.id.itemRecyclerView);

        itemAdapter = new ItemAdapter(context, new ArrayList<Item>());
        recyclerView.setAdapter(itemAdapter);

        update(this, recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        EditText searchBar = findViewById(R.id.editSearchBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                itemAdapter.filterItems(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdapter.filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                itemAdapter.filterItems(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                update(this, (RecyclerView) findViewById(R.id.itemRecyclerView));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}