package com.ntuproject.fast_and_furious;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.ntuproject.fast_and_furious.databinding.ActivityReadDataBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TestPage extends HomeMainActivity {

    private ListView test;

    final String TAG = "MyActivity";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseFirestore db1 = FirebaseFirestore.getInstance();

    ActivityReadDataBinding binding;
    DatabaseReference ref;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private String Area;


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
        setContentView(R.layout.activity_read_data);


        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }



        //test = (ListView) findViewById(R.id.listview);
        String path;
        binding = ActivityReadDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

////Read JSON file straight
        String jsonFileString = Utils.getJsonFromAssets(getApplicationContext(), "response.json");
        Log.i("data", jsonFileString);

        try {
            JSONObject obj = new JSONObject(jsonFileString);
            JSONObject valuelist = obj.getJSONObject("value");
            Area = valuelist.getString("Area");
            binding.testviewjson.setText(Area);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Gson gson = new Gson();
        //Response response = gson.fromJson(jsonFileString, Response.class);
        //binding.testviewjson.setText(response.getValueList().get(1).getArea());
/*
        Type listUserType = new TypeToken<List<value>>() { }.getType();
        List<value> value = gson.fromJson(jsonFileString, listUserType);
        for (int i = 1; i < value.size(); i++) {
            Log.i("data", "> Item " + i + "\n" + value.get(i));
        }
 */
        try {
            // get JSONObject from JSON file
            JSONObject obj = new JSONObject(loadJSONFromAsset("response.json"));
            // fetch JSONArray named users
            JSONArray a = obj.getJSONArray("value");
            // implement for loop for getting users list data
            for (int i = 0; i < a.length(); i++) {
                // create a JSONObject for fetching single user data
                JSONObject aDetail = a.getJSONObject(i);
                // fetch email and name and store it in arraylist
                //b.add(userDetail.getString("name"));
                //c.add(userDetail.getString("email"));
                // create a object for getting contact data from JSONObject
                //JSONObject contact = aDetail.getJSONObject("CarParkID");
                // fetch mobile number and store it in arraylist
                //d.add(contact.getString("mobile"));
                System.out.println(aDetail.toString());
                binding.testviewjson.append(aDetail.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

////////////

        ///Testing Realtime database
        binding.testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.testslot.getText().toString();
                if (!username.isEmpty()){
                    System.out.println(head+"readData()");
                    readData(username);

                }else{
                    Toast.makeText(TestPage.this,"Please Enter username", Toast.LENGTH_LONG).show();
                    System.out.println(head+"Error username");
                }
            }

            private void readData(String username) {
                //ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://hendyley-personal-default-rtdb.asia-southeast1.firebasedatabase.app/");
                //ref.child("Users");
                ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                System.out.println(head+"Successful Read");
                                DataSnapshot dss = task.getResult();
                                String firstname = String.valueOf(dss.child("firstname").getValue());
                                String lastname = String.valueOf(dss.child("lastname").getValue());
                                binding.testView.setText(firstname +" "+lastname);

                            }else{
                                System.out.println(head+"Error 1");
                                Toast.makeText(TestPage.this,"Fail",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            System.out.println(head+"Error 2 cause "+task.isSuccessful());
                            Toast.makeText(TestPage.this,"Fail to read",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //firebase
        binding.testbuttonfiredb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db1.collection("Carpark")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        System.out.println(head+document.getId() + " => " + document.getData());
                                        binding.testViewfiredb.setText(document.getData().toString());
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });


            }
        });


        //Testing Storage Database
        binding.testbuttonstorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                StorageReference storef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/hendyley-personal.appspot.com/o/response.json?alt=media&token=58e413e1-398e-479f-ad20-c024307642b6");
                //System.out.println(head+storef.getName());
                binding.testViewstorage.setText(storef.getName());
/*
                File localFile;
                try {
                    localFile = File.createTempFile("images", "json");
                    FileDownloadTask task = storef.getFile(localFile);
                    try {
                        //Tasks.await(task); // or use await with timeout: Tasks.await(task, 30, TimeUnit.SECONDS)
                        Tasks.await(task, 500, TimeUnit.MILLISECONDS);
                        if (task.isSuccessful()) {
                            Toast.makeText(CommuterPage.this, "File downloaded successfully.", Toast.LENGTH_SHORT).show();
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile)));
                                StringBuilder result = new StringBuilder();
                                String line = null;
                                while((line.concat(String.valueOf(reader.read()))) != null){
                                    Log.e("Test", "Result - "+ result);
                                    result.append(line);
                                }
                                System.out.println(head+result.toString());
                            } catch (FileNotFoundException e) {
                                System.out.println(head+"ES 1 "+e);
                            } catch (IOException e) {
                                System.out.println(head+"ES 2 "+e);
                            }
                        } else {
                            Log.e("Test", "Failed: " + task.getException().getMessage());
                            Toast.makeText(CommuterPage.this, "File downloading failed", Toast.LENGTH_LONG).show();
                        }

                    } catch (ExecutionException e) {
                        System.out.println(head+"ES 3 "+e);
                    } catch (InterruptedException e) {
                        System.out.println(head+"ES 4 "+e);
                    } catch (TimeoutException e) {
                        System.out.println(head+"ES 5 "+e);
                    }

                } catch (IOException e) {
                    System.out.println(head+"ES 5 "+e);
                }
*/


/*/
                storef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        System.out.println(head+"Success "+bytes);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        System.out.println(head+"Fail");
                    }
                });
*/
                /*/ Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("first", "Ada");
                user.put("last", "Lovelace");
                user.put("born", 1815);
                System.out.println(head+user+"is here");

                // Add a new document with a generated ID
                db1.collection("response")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                db1.collection("response")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        System.out.println(head+ document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    System.out.println(head+"Error 3");
                                }
                            }
                        });

                 */
            }
        });





 
    }

    public String loadJSONFromAsset(String x) {
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
        return json;
    }
}


