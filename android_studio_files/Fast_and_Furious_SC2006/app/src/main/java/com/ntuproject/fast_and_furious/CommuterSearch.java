package com.ntuproject.fast_and_furious;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
//import android.widget.SearchView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
//import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//import java.util.Arrays;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ntuproject.fast_and_furious.databinding.ActivityReadDataBinding;

public class CommuterSearch extends HomeMainActivity {

    ListView listView;
    List<String> area;
    String[] mrt = { "AMK", "Bedok", "Choa Chu Kang", "Jurong", "Boon Keng", "Boon Lay", "Marina", "Suntec", "Yishun", "Khatib", "Woodlands", "Clementi", "Changi", "Pasir Ris" };
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter1;

    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    ActivityReadDataBinding binding;
    //DatabaseReference ref;
    StringBuilder temps = new StringBuilder();
    final String TAG = "MyActivity";

    FloatingActionButton AddFab, NearbyFab, SearchFab, FavouriteFab;
    TextView NearbyText,SearchText,FavouriteText;
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.commuter_search_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("FastAndFurious");

        listView = findViewById(R.id.listview);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mrt);
        listView.setAdapter(arrayAdapter);

        AddFab = findViewById(R.id.add_fab);
        NearbyFab = findViewById(R.id.nearby_button);
        SearchFab = findViewById(R.id.search_button);
        FavouriteFab = findViewById(R.id.favourite_button);

        NearbyText = findViewById(R.id.nearby_text);
        SearchText = findViewById(R.id.search_text);
        FavouriteText = findViewById(R.id.favourite_text);

        NearbyFab.setVisibility(View.GONE);
        SearchFab.setVisibility(View.GONE);
        FavouriteFab.setVisibility(View.GONE);
        NearbyText.setVisibility(View.GONE);
        SearchText.setVisibility(View.GONE);
        FavouriteText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        AddFab.setOnClickListener(view -> {
            if(!isAllFabsVisible){
                NearbyFab.show();
                SearchFab.show();
                FavouriteFab.show();
                NearbyText.setVisibility(View.VISIBLE);
                SearchText.setVisibility(View.VISIBLE);
                FavouriteText.setVisibility(View.VISIBLE);
                isAllFabsVisible = true;
            }
            else{
                NearbyFab.hide();
                SearchFab.hide();
                FavouriteFab.hide();
                NearbyText.setVisibility(View.GONE);
                SearchText.setVisibility(View.GONE);
                FavouriteText.setVisibility(View.GONE);
                isAllFabsVisible = false;
            }
        });

        SearchFab.setOnClickListener(view -> Toast.makeText(CommuterSearch.this, "You are already at search page", Toast.LENGTH_SHORT
        ).show());

        NearbyFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //startActivity(new Intent(CommuterSearch.this, NavigationPage.class));
                startActivity(new Intent(CommuterSearch.this, CommuterNearby.class));
            }
        });

        FavouriteFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(CommuterSearch.this, CommuterFavourite.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        MenuItem refresh = menu.findItem(R.id.refresh);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter a MRT station");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter1.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        binding = ActivityReadDataBinding.inflate(getLayoutInflater());
        //temps = null;
        //temps.replace(0,0," ");
        //setContentView(binding.getRoot());

        db1.collection("BusStop")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CompletableFuture.runAsync(()->{
                                    binding.testViewfiredb.setText(document.getData().toString());
                                    temps.append(document.getData().toString());
                                    System.out.println(head+document.getData().toString());
                                });
                                // System.out.println(head+document.getId() + " => " + document.getData());
                                // binding.testViewfiredb.setText(document.getData().toString());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        area = new ArrayList<String>();

        try {
            if(temps!=null){
                System.out.println(head+"here");
                JSONObject x = new JSONObject(String.valueOf(temps));
                System.out.println(head+"here1");
                JSONArray array = x.getJSONArray("value");
                System.out.println(head+"here2");
                System.out.println(head+ "hello "+ array.getJSONObject(0));
                System.out.println(head+" hello "+ array.getJSONObject(0).get("Description"));
                System.out.println(head+ "json array length: " + array.length());
                for(int i =0;i<array.length();i++) {
                    //if(array.getJSONObject(i).get("Description").toString().length()>2)
                    area.add(array.getJSONObject(i).get("Description").toString());
                    //System.out.println(head+ "hello "+ array.getJSONObject(i).get("CarParkID"));
                }
                System.out.println(head+"here3");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(head+ "area array: " + area);
        System.out.println(head+ "area array length: " + area.size());

        arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,area);
        listView.setAdapter(arrayAdapter1);

        Toast.makeText(this,"page refreshed",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
