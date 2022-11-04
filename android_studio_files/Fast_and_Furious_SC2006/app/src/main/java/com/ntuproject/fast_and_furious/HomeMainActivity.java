package com.ntuproject.fast_and_furious;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class HomeMainActivity extends AppCompatActivity {

    String where = this.getClass().getName();
    String head = "Fast and Furious Team from "+where+" : ";
    final String TAG = head;

    public boolean tracker = false;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();

    private StringBuilder busservice = new StringBuilder();
    private StringBuilder busstop = new StringBuilder();
    private StringBuilder busarrival = new StringBuilder();
    private StringBuilder carpark = new StringBuilder();

    public JSONObject bussero = null;
    public JSONObject busstoo = null;
    public JSONObject busario = null;
    public JSONObject carparko = null;

    public JSONArray bussera = null;
    public JSONArray busstoa = null;
    public JSONArray busaria = null;

    public JSONObject fav = null;
    public JSONArray favo = null;

    public double NTUlat = 1.3461733691799838;
    public double NTUlong = 103.68185366931402;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        if(tracker==false) {
            try {
                getfavourite();
                getData();
                getcarparkdata();
            } finally {
                System.out.println(head + "Update Finish");
            }
            tracker = true;
        }

        ImageButton commuterB = (ImageButton) findViewById(R.id.CommuterAvatar);
        ImageButton driverB = (ImageButton) findViewById(R.id.DriverAvatar);
        //FloatingActionButton testpage = (FloatingActionButton) findViewById(R.id.testpage);

        commuterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(HomeMainActivity.this, CommuterFavourite.class));
                System.out.println(head +"Go to Commuter Page");
                Intent in = new Intent(HomeMainActivity.this,CommuterNearby.class);
                try {
                    bussero = new JSONObject(String.valueOf(busservice));
                    busario = new JSONObject(String.valueOf(busarrival));
                    busstoo = new JSONObject(String.valueOf(busstop));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                in.putExtra("busservice", bussero.toString());
                in.putExtra("busarrival", busario.toString());
                in.putExtra("busstop",busstoo.toString());
                in.putExtra("fav",fav.toString());
                startActivity(in);
            }
        });

        driverB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(HomeMainActivity.this, DriverPage.class));
                System.out.println(head +"Go to Driver Page");
                Intent in = new Intent (HomeMainActivity.this,DriverPage.class);
                try{
                    carparko = new JSONObject(String.valueOf(carpark));

                }catch (JSONException e){
                    e.printStackTrace();
                }
                in.putExtra("carpark",carparko.toString());
                startActivity(in);

            }
        });

       /* testpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMainActivity.this, TestPage.class));
                System.out.println(head+"Go to TestPage");
            }
        });*/
    }
    private void getfavourite() {
        try {
            fav = new JSONObject(String.valueOf( loadAssetJSON("favlist.json") ));
            // if reading from string just pass in the string
            // just do fav = new JSONObject(String.valueOf("___string___")
            // dont need use loadAssetJSON function
            favo = fav.getJSONArray("favourite");
            System.out.println(head+"user favlist : "+favo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void getcarparkdata() {
        System.out.println(head+" carparkdata() ");
        //final CompletableFuture<String> q = new CompletableFuture<>();
        db1.collection("Carpark")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            try{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //CompletableFuture.runAsync(() -> {
                                    carpark.append(document.getData());
                                    //});
                                }
                            }
                            finally {
                                System.out.println(head+"x carpark"+carpark.toString());
                                //updateLL();
                            }

                        } else {
                            Log.w(head, "Error Firebase getting documents.", task.getException());
                        }

                    }
                });
    }

    private synchronized void getData() {


        System.out.println(head+" busservicedata() ");
        //final CompletableFuture<String> q = new CompletableFuture<>();
        db1.collection("BusService")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            try{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //CompletableFuture.runAsync(() -> {
                                    busservice.append(document.getData());
                                    //});
                                }
                            }
                            finally {
                                System.out.println(head+"x busservice"+busservice.toString());
                                //updateLL();
                            }

                        } else {
                            Log.w(head, "Error Firebase getting documents.", task.getException());
                        }

                    }
                });


        System.out.println(head+" busstopdata() ");
        //final CompletableFuture<String> q = new CompletableFuture<>();
        db1.collection("BusStopsData(70)")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            try{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //CompletableFuture.runAsync(() -> {
                                    busstop.append(document.getData());
                                    //});
                                }
                            }
                            finally {
                                System.out.println(head+"x busstop"+busstop.toString());
                                //updateLL();
                                //onResume();
                            }

                        } else {
                            Log.w(head, "Error Firebase getting documents.", task.getException());
                        }

                    }
                });

        System.out.println(head+" busarrivaldata() ");
        //final CompletableFuture<String> q = new CompletableFuture<>();
        db1.collection("BusArrivalData(70)")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            try{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //CompletableFuture.runAsync(() -> {
                                    busarrival.append(document.getData());
                                    //});
                                }
                            }
                            finally {
                                System.out.println(head+"x busArrival"+busarrival.toString());
                            }

                        } else {
                            Log.w(head, "Error Firebase getting documents.", task.getException());
                        }

                    }
                });

    }

    private String loadAssetJSON(String x) {
        String json = null;
        try {
            InputStream is = getAssets().open(x);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        System.out.println(head+" loadJSONasset done");
        return json;
    }
}