package com.ntuproject.fast_and_furious;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Binder;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ntuproject.fast_and_furious.databinding.ActivityReadDataBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CommuterPage extends TestPage {

    private ListView test;

    final String TAG = "MyActivity";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();

    ActivityReadDataBinding binding;
    DatabaseReference ref;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();


    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // do your stuff
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commuter_nearby_page);

        ScrollView buslist = (ScrollView) findViewById(R.id.listofbus);
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.listofbusLL);

        try {
            // get JSONObject from JSON file
            JSONObject obj = new JSONObject(loadJSONFromAsset("response.json"));
            // fetch JSONArray named users
            JSONArray a = obj.getJSONArray("value");
            // implement for loop for getting users list data
            for (int i = 0; i < a.length(); i++) {
                JSONObject Detail = a.getJSONObject(i);

                //JSONObject CarParkID = Detail.getJSONObject("CarParkID");
                //JSONObject Area = Detail.getJSONObject("Area");
                //JSONObject Development = Detail.getJSONObject("Development");
                //JSONObject Location = Detail.getJSONObject("Location");
                //JSONObject AvailableLots= Detail.getJSONObject("AvailableLots");
                //JSONObject LotType = Detail.getJSONObject("LotType");
                //JSONObject Agency = Detail.getJSONObject("Agency");

                //String vw = "Bus No: "+CarParkID.toString()+" Area:"+Area+" Agency: "+Agency+" Available: "+AvailableLots;
                //String vw = "GGGGGG";
                //System.out.println(head+vw);

                TextView tv = new TextView(this);
                tv.setText(Detail.toString());
                System.out.println(head+Detail.toString());
                ll.addView(tv);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


 
    }
}


