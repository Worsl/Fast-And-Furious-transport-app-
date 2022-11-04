package com.ntuproject.fast_and_furious;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.ntuproject.fast_and_furious.databinding.ActivityReadDataBinding;

public class ParkingPage extends HomeMainActivity {

    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
    ActivityReadDataBinding binding;
    //DatabaseReference ref;
    StringBuilder temps = new StringBuilder();
    final String TAG = "MyActivity";

    public JSONObject carpark = null;
    public JSONArray carparko = null;

    ListView listView;
    List<String> area;
    List<String> numberOfLots;
    List<Integer> nooflots;
    Boolean ascending;
    String[] mrt = {"AMK", "Bedok", "Choa Chu Kang", "Jurong", "Boon Keng", "Boon Lay", "Marina", "Suntec", "Yishun", "Khatib", "Woodlands", "Clementi", "Changi", "Pasir Ris"};
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;

    HashMap<String, Integer> map = new HashMap<String, Integer>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkingpage);

        if(getIntent().hasExtra("carpark")) {
            try {
                carpark = new JSONObject( getIntent().getStringExtra("carpark") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Carpark");

        binding = ActivityReadDataBinding.inflate(getLayoutInflater());
        ascending = false;

        area = new ArrayList<String>();
        numberOfLots = new ArrayList<String>();
        nooflots = new ArrayList<Integer>();

        try {
            JSONArray array = carpark.getJSONArray("value");
            System.out.println(head + "hello " + array.getJSONObject(499));
            System.out.println(head + " hello " + array.getJSONObject(499).get("Development"));
            System.out.println(head + "json array length: " + array.length());
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i).get("Development").toString().length() > 2)
                    area.add(array.getJSONObject(i).get("Development").toString());
                numberOfLots.add(array.getJSONObject(i).get("AvailableLots").toString());
                nooflots.add(Integer.valueOf(array.getJSONObject(i).get("AvailableLots").toString()));
                }
                System.out.println(head + "here3");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        System.out.println(head + "area array: " + area);
        System.out.println(head + "area array length: " + area.size());

        listView = (ListView) findViewById(R.id.parking_listView);
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), area, nooflots);
        listView.setAdapter(customBaseAdapter);


       /* db1.collection("Carpark")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    CompletableFuture.runAsync(() -> {
                                        binding.testViewfiredb.setText(document.getData().toString());
                                        temps.append(document.getData().toString());
                                        //    System.out.println(head+document.getData().toString());
                                    });
                                    // System.out.println(head+document.getId() + " => " + document.getData());
                                    // binding.testViewfiredb.setText(document.getData().toString());
                                }
                            } finally {
                                area = new ArrayList<String>();
                                numberOfLots = new ArrayList<String>();

                                try {
                                    if (temps != null) {
                                        JSONObject carpark = new JSONObject(String.valueOf(temps));
                                        JSONArray array = carpark.getJSONArray("value");
                                        System.out.println(head + "hello " + array.getJSONObject(499));
                                        System.out.println(head + " hello " + array.getJSONObject(499).get("Development"));
                                        System.out.println(head + "json array length: " + array.length());
                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.getJSONObject(i).get("Development").toString().length() > 2)
                                                area.add(array.getJSONObject(i).get("Development").toString());
                                            numberOfLots.add(array.getJSONObject(i).get("AvailableLots").toString());
                                            nooflots.add(Integer.valueOf(array.getJSONObject(i).get("AvailableLots").toString()));
                                        }
                                        System.out.println(head + "here3");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(head + "area array: " + area);
                                System.out.println(head + "area array length: " + area.size());

                                listView = (ListView) findViewById(R.id.parking_listView);
                                CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), area, nooflots);
                                listView.setAdapter(customBaseAdapter);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                }); */

        /*area = new ArrayList<String>();
        numberOfLots = new ArrayList<String>();

        try {
            if(temps!=null){
                JSONObject x = new JSONObject(String.valueOf(temps));
                JSONArray array = x.getJSONArray("value");
                System.out.println(head+ "hello "+ array.getJSONObject(499));
                System.out.println(head+" hello "+ array.getJSONObject(499).get("Development"));
                System.out.println(head+ "json array length: " + array.length());
                for(int i =0;i<array.length();i++) {
                    if(array.getJSONObject(i).get("Development").toString().length()>2)
                        area.add(array.getJSONObject(i).get("Development").toString());
                    numberOfLots.add(array.getJSONObject(i).get("AvailableLots").toString());
                }
                System.out.println(head+"here3");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(head+ "area array: " + area);
        System.out.println(head+ "area array length: " + area.size());

        listView = (ListView) findViewById(R.id.parking_listView);
        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(),area,numberOfLots);
        listView.setAdapter(customBaseAdapter);
        //arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,area);
        //listView.setAdapter(arrayAdapter1);


        /*listView = findViewById(R.id.parking_listView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mrt);
        listView.setAdapter(arrayAdapter);*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parking_menu, menu);
        MenuItem refresh = menu.findItem(R.id.refresh);
        MenuItem sort = menu.findItem(R.id.sort);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                binding = ActivityReadDataBinding.inflate(getLayoutInflater());
                db1.collection("Carpark")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        CompletableFuture.runAsync(() -> {
                                            binding.testViewfiredb.setText(document.getData().toString());
                                            temps.append(document.getData().toString());
                                            //    System.out.println(head+document.getData().toString());
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
                //numberOfLots = new ArrayList<String>();
                nooflots = new ArrayList<Integer>();
               // Map<String, Integer> map = new HashMap<String, Integer>();

                try {
                    if (temps != null) {
                        JSONObject x = new JSONObject(String.valueOf(temps));
                        JSONArray array = x.getJSONArray("value");
                        System.out.println(head + "hello " + array.getJSONObject(499));
                        System.out.println(head + " hello " + array.getJSONObject(499).get("Development"));
                        System.out.println(head + "json array length: " + array.length());
                        for (int i = 0; i < array.length(); i++) {
                            if (array.getJSONObject(i).get("Development").toString().length() > 2)
                                area.add(array.getJSONObject(i).get("Development").toString());
                            numberOfLots.add(array.getJSONObject(i).get("AvailableLots").toString());
                            nooflots.add(Integer.valueOf(array.getJSONObject(i).get("AvailableLots").toString()));
                            map.put(array.getJSONObject(i).get("Development").toString(),Integer.valueOf(array.getJSONObject(i).get("AvailableLots").toString()));
                        }
                        System.out.println(head + "here3");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(head + "area array: " + area);
                System.out.println(head + "area array length: " + area.size());

                listView = (ListView) findViewById(R.id.parking_listView);
                CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), area, nooflots);
                listView.setAdapter(customBaseAdapter);
                //arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,area);
                //listView.setAdapter(arrayAdapter1);

                Toast.makeText(this, "page refreshed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sort:
                if (ascending == false) {
                    Map<String, Integer> sortedMap =
                            map.entrySet().stream()
                                    .sorted(Entry.comparingByValue())
                                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                            (e1, e2) -> e1, LinkedHashMap::new));
                    map.forEach((key, value) -> System.out.println(head+"unsorted "+"Key : " + key + " Value : " + value));
                    sortedMap.forEach((key, value) -> System.out.println(head+ "sorted "+"Key : " + key + " Value : " + value));
                    List<String> sortedAscendingArea = new ArrayList<String>(sortedMap.keySet());

                    Collections.sort(nooflots);
                    CustomBaseAdapter customBaseAdapter1 = new CustomBaseAdapter(getApplicationContext(), sortedAscendingArea, nooflots);
                    listView.setAdapter(customBaseAdapter1);
                    ascending = true;
                    Toast.makeText(this, "Sorted in ascending order", Toast.LENGTH_SHORT).show();
                } else if (ascending == true) {
                    Map<String, Integer> sortedMap =
                            map.entrySet().stream()
                                    .sorted(Entry.<String,Integer>comparingByValue().reversed())
                                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                            (e1, e2) -> e1, LinkedHashMap::new));
                    map.forEach((key, value) -> System.out.println(head+"unsorted "+"Key : " + key + " Value : " + value));
                    sortedMap.forEach((key, value) -> System.out.println(head+ "sorted "+"Key : " + key + " Value : " + value));
                    List<String> sortedDescendingArea = new ArrayList<String>(sortedMap.keySet());

                    Collections.sort(nooflots, Collections.reverseOrder());
                    CustomBaseAdapter customBaseAdapter2 = new CustomBaseAdapter(getApplicationContext(), sortedDescendingArea, nooflots);
                    listView.setAdapter(customBaseAdapter2);
                    ascending = false;
                    Toast.makeText(this, "Sorted in descending order", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

}

