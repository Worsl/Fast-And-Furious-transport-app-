package com.ntuproject.fast_and_furious;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;


public class CommuterFavourite2 extends AppCompatActivity {

    FloatingActionButton NearbyFab, SearchFab, FavouriteFab,AddFab;
    TextView NearbyText,SearchText,FavouriteText;
    Boolean isAllFabsVisible;
    String where = this.getClass().getName();
    String head = "Fast and Furious Team from "+where+" : ";
    final String TAG = head;
    //public float zoom = 10.5f;
    JSONObject[] bsstopo = null;

    //LatLng[] bstop = null;

    String busservice = "";
    String busstop = "";
    String busarrival = "";

    public JSONObject bussero = null;
    public JSONObject busstoo = null;
    public JSONObject busario = null;

    public JSONArray bussera = null;
    public JSONArray busstoa = null;
    public JSONArray busaria = null;

    public JSONObject fav = null;
    public JSONArray favo = null;


    public double NTUlat = 1.3461733691799838;
    public double NTUlong = 103.68185366931402;


    // Add a marker in NTU and move the camera
    LatLng Singapore = new LatLng(NTUlat,NTUlong);

    private ArrayList<LatLng> locationArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commuter_favourite_page_2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Favourite");

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



        SearchFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //startActivity(new Intent(CommuterNearby.this, CommuterSearch.class));
                Intent in = new Intent(CommuterFavourite2.this, CommuterSearch2.class);
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

        FavouriteFab.setOnClickListener(
                view -> Toast.makeText(CommuterFavourite2.this, "You are already at Favourite page", Toast.LENGTH_SHORT
                ).show());

        NearbyFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //startActivity(new Intent(CommuterNearby.this, CommuterFavourite.class));
                Intent in = new Intent(CommuterFavourite2.this, CommuterNearby.class);
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

        if(getIntent().hasExtra("busservice")) {
            try {
                bussero = new JSONObject( getIntent().getStringExtra("busservice") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(getIntent().hasExtra("busarrival")) {
            try {
                busario = new JSONObject( getIntent().getStringExtra("busarrival") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(getIntent().hasExtra("busstop")) {
            try {
                busstoo = new JSONObject( getIntent().getStringExtra("busstop") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(getIntent().hasExtra("fav")) {
            try {
                fav = new JSONObject( getIntent().getStringExtra("fav") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            bussera = bussero.getJSONArray("value");
            busstoa = busstoo.getJSONArray("value");
            busaria = busario.getJSONArray("value");
            favo = fav.getJSONArray("favourite");
            System.out.println(head+" length data: "+bussera.length()+" "+busstoa.length()+" "+busaria.length()+" "+favo.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bsstopo = new JSONObject[busstoa.length()];
        for(int bs=0;bs<busstoa.length();bs++){
            bsstopo[bs] = null;
        }

        updateLL();

        locationArrayList = new ArrayList<>();

    }
    // menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.fav_menu,menu);
        return true;}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mrt:
                Intent intent = new Intent(this, MrtPage.class);
                this.startActivity(intent);
                break;
            case R.id.refresh:
                updateLL();
                Toast.makeText(this, "Page Refreshed", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Update view
    private void updateLL(){


        LinearLayout llh = (LinearLayout) findViewById(R.id.llh);
        TextView tb = (TextView) findViewById(R.id.Busstop);
        TextView td = (TextView) findViewById(R.id.Destination);
        TextView ts = (TextView) findViewById(R.id.distance);
        TextView tq = (TextView) findViewById(R.id.qty);
        ImageButton fv = (ImageButton)  findViewById(R.id.favstar);

        ScrollView listofbus = (ScrollView) findViewById(R.id.listofbus);
        LinearLayout ll = (LinearLayout) findViewById(R.id.listofbusLL);
        ll.setClickable(true);
        LinearLayout llv = (LinearLayout) findViewById(R.id.llv);
        //llv.setClickable(false);
        llv.removeAllViewsInLayout();

        //ExpandableListView llv = (ExpandableListView) findViewById(R.id.llv);
        /*
        listofbus   -scroll
        ll  -v
        (bus stop)
            llv -v
                llhv    -H
                llhc    -H
        (bus)
                lhk     -V
                        lharrival   -H
         */


        if(busstoa.length()!=0){
            //System.out.println(head+" length data: "+busservice.length()+" "+busstop.length()+" "+busarrival.length());
            try {

                System.out.println(head+"Data Acquired");
                for (int i = 0; i < busstoa.length(); i++) {

                    Integer fvstate = 0;
                    Double templat = (double) busstoa.getJSONObject(i).get("Latitude");
                    Double templong = (double) busstoa.getJSONObject(i).get("Longitude");
                    String tempbname = busstoa.getJSONObject(i).getString("Description");
                    String tempbcode = busstoa.getJSONObject(i).getString("BusStopCode");


                    double dist = distance((double) busstoa.getJSONObject(i).get("Latitude"), (double) busstoa.getJSONObject(i).get("Longitude"), NTUlat, NTUlong, 'k');
                    //System.out.println(head+dist);
                    for (int g = 0; g < favo.length(); g++) {
                        //System.out.println(head+"Check is fav "+favo.getJSONObject(g).getString("busname"));
                        if (favo.getJSONObject(g).getString("busname").equals(busstoa.getJSONObject(i).getString("Description")) && favo.getJSONObject(g).getString("favourite").equals("Y")) {
                            fvstate = 1;
                        }
                    }
                    if( fvstate==1 ){
                    try {
                        bsstopo[i] = new JSONObject();
                        bsstopo[i].put("mark", busstoa.getJSONObject(i).getString("Description"));
                        bsstopo[i].put("fav", fvstate);
                        bsstopo[i].put("x", new LatLng((double) busstoa.getJSONObject(i).get("Latitude"), (double) busstoa.getJSONObject(i).get("Longitude")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //not necessary
                    LinearLayout llvc = new LinearLayout(this);
                    llvc.setOrientation(LinearLayout.VERTICAL);

                    //ExpandableListView llhc = new ExpandableListView(this);
                    LinearLayout llhc = new LinearLayout(this);
                    llhc.setOrientation(LinearLayout.HORIZONTAL);
                    //llhc.setLayoutParams(llh.getLayoutParams());

                    // bad actually
                    LinearLayout[] lhk = new LinearLayout[busstoa.length()];
                    for (int m = 0; m < lhk.length; m++) {
                        lhk[m] = new LinearLayout(this);
                        lhk[m].setOrientation(LinearLayout.VERTICAL);
                        lhk[m].setBackgroundColor(Color.parseColor("#C3C3C3")); //gray color
                    }
                    //llvc -> llhc -> lhk -> lharrival

                    TextView tv1 = new TextView(this);
                    tv1.setClickable(false);
                    tv1.setLayoutParams(tb.getLayoutParams());
                    tv1.setText(busstoa.getJSONObject(i).getString("Description"));
                    tv1.setTag(i);
                    llhc.addView(tv1);

                    TextView tv3 = new TextView(this);
                    tv3.setClickable(false);
                    tv3.setLayoutParams(td.getLayoutParams());
                    tv3.setText(busstoa.getJSONObject(i).getString("RoadName"));
                    llhc.addView(tv3);

                    TextView tv7 = new TextView(this);
                    tv7.setClickable(false);
                    tv7.setLayoutParams(ts.getLayoutParams());
                    tv7.setText(String.format("%.2f", dist));
                    llhc.addView(tv7);

                    TextView tv5 = new TextView(this);
                    tv5.setClickable(false);
                    tv5.setLayoutParams(tq.getLayoutParams());
                    Integer qty = 0;

                    for (int j = 0; j < busaria.length(); j++) {
                        //bus arrival bus stop vs busstop bus stop

                        if (busaria.getJSONObject(j).getString("BusStopCode").equals(busstoa.getJSONObject(i).getString("BusStopCode"))) {
                            //System.out.println(head+"WILD "+qty.toString()+" "+busaria.getJSONObject(j).getString("BusStopCode"));

                            JSONArray detail = busaria.getJSONObject(j).getJSONArray("Services");
                            LinearLayout[] lharrival = new LinearLayout[detail.length()];
                            for (int z = 0; z < detail.length(); z++) {
                                //String x = s1.substring(s1.lastIndexOf("T")+1,s1.lastIndexOf("T")+6);

                                lharrival[z] = new LinearLayout(this);
                                lharrival[z].setOrientation(LinearLayout.HORIZONTAL);
                                lharrival[z].setBackgroundColor(Color.parseColor("#C3C3C3")); //gray color?
/*
                                    int ri1 = (int)Math.floor(Math.random()*(10-1+1)+1);
                                    int ri2 = (int)Math.floor(Math.random()*(10-ri1+2)+ri1);
                                    int ri3 = (int)Math.floor(Math.random()*(10-ri2+1)+ri2);
 */


                                String t1 = detail.getJSONObject(z).getJSONObject("NextBus").getString("EstimatedArrival");
                                String t2 = detail.getJSONObject(z).getJSONObject("NextBus2").getString("EstimatedArrival");
                                String t3 = detail.getJSONObject(z).getJSONObject("NextBus3").getString("EstimatedArrival");

                                LocalDateTime now = LocalDateTime.now();
                                int ri1 = timedif(t1, now);
                                int ri2 = timedif(t2, now);
                                int ri3 = timedif(t3, now);
                                //System.out.println(head+detail.getJSONObject(z).getString("ServiceNo")+" "+ri1+" "+ri2+" "+ri3+"  "+now.getMinute());


                                if (ri3 < ri2) {
                                    ri2 = ri2 ^ ri3 ^ (ri3 = ri2);
                                }
                                if (ri2 < ri1) {
                                    ri1 = ri1 ^ ri2 ^ (ri2 = ri1);
                                }
                                if (ri3 < ri2) {
                                    ri2 = ri2 ^ ri3 ^ (ri3 = ri2);
                                }

                                TextView h = new TextView(CommuterFavourite2.this);
                                h.setText("Bus No: " + detail.getJSONObject(z).getString("ServiceNo"));
                                h.setLayoutParams(tb.getLayoutParams());
                                TextView c1 = new TextView(CommuterFavourite2.this);
                                c1.setText(String.valueOf(ri1));
                                c1.setLayoutParams(td.getLayoutParams());
                                c1.setTextColor(Color.parseColor(settiming(ri1)));
                                TextView c2 = new TextView(CommuterFavourite2.this);
                                c2.setText(String.valueOf(ri2));
                                c2.setLayoutParams(td.getLayoutParams());
                                c2.setTextColor(Color.parseColor(settiming(ri2)));
                                TextView c3 = new TextView(CommuterFavourite2.this);
                                c3.setText(String.valueOf(ri3));
                                c3.setLayoutParams(td.getLayoutParams());
                                c3.setTextColor(Color.parseColor(settiming(ri3)));

                                lharrival[z].addView(h);
                                lharrival[z].addView(c1);
                                lharrival[z].addView(c2);
                                lharrival[z].addView(c3);

                                lharrival[z].setTag(z);

                                lhk[i].setTag(i);
                                lhk[i].addView(lharrival[z]);

                                qty++;
                            }
                        }
                    }
                    //System.out.println(head+"x :"+qty);
                    tv5.setText(qty.toString());
                    llhc.addView(tv5);

                    ImageButton fc = new ImageButton(this);
                    fc.setLayoutParams(fv.getLayoutParams());
                    fc.setVisibility(View.VISIBLE);
                    if (fvstate == 1) {
                        fc.setImageResource(R.drawable.ic_star_yellow);
                        fc.setScaleType(ImageView.ScaleType.FIT_START);
                        fc.setTag("Y");
                    } else {
                        fc.setImageResource(R.drawable.ic_star);
                        fc.setScaleType(ImageView.ScaleType.FIT_START);
                        fc.setTag("N");
                    }
                    fc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Integer done = 0;
                            try {
                                System.out.println(head + " turn " + tv1.getText());
                                if (fc.getTag() == "Y") {
                                    fc.setImageResource(R.drawable.ic_star);
                                    fc.setScaleType(ImageView.ScaleType.FIT_START);
                                    fc.setTag("N");
                                    for (int g = 0; g < favo.length(); g++) {
                                        if (favo.getJSONObject(g).getString("busname").equals(tv1.getText())) {
                                            favo.getJSONObject(g).put("favourite", "N");
                                            done = 1;
                                            break;
                                        }
                                    }
                                } else {
                                    fc.setImageResource(R.drawable.ic_star_yellow);
                                    fc.setScaleType(ImageView.ScaleType.FIT_START);
                                    fc.setTag("Y");
                                    for (int g = 0; g < favo.length(); g++) {

                                        if (favo.getJSONObject(g).getString("busname").equals(tv1.getText())) {
                                            favo.getJSONObject(g).put("favourite", "Y");
                                            done = 1;
                                            break;
                                        }
                                    }
                                    if (done == 0) {
                                        String name = tempbname;
                                        String descrip = tempbcode;


                                        JSONObject json = new JSONObject();
                                        json.put("buscode", tempbcode);
                                        json.put("name", "");
                                        json.put("busname", tv1.getText());
                                        json.put("Latitude", templat);
                                        json.put("Longitude", templong);
                                        json.put("Description", "");
                                        json.put("favourite", "Y");

                                        fav.getJSONArray("favourite").put(json);
                                    }
                                }

                                favo = fav.getJSONArray("favourite");
                                System.out.println(head + "done so check " + fav);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            updateLL();
                        }
                    });
                    llhc.addView(fc);


                    llhc.setClickable(true);
                    llhc.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onClick(View v) {

                            System.out.println(head + "Click Bus: " + tv1.getText() + " " + llvc.getChildCount());

                            //hover

                            if (llvc.getChildCount() == 1 && tv5.getText() != "0") {
                                llvc.addView(lhk[(Integer) tv1.getTag()]);
                            } else {
                                llvc.removeViewAt(1);
                            }
                        }
                    });
                    llvc.addView(llhc);
                    //llvc.addView(llhc);
                    llv.addView(llvc);
                }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int timedif(String t,LocalDateTime now) {


        if(t.length()!=0){
            return Math.abs( Integer.valueOf(t.substring(t.lastIndexOf("T")+4,t.lastIndexOf("T")+6)) - now.getMinute() );
        }
        else{
            return 10000;
        }
    }

    public String settiming(int x){
        if(x>=1000){ //null
            return "#00000000";
        }else if(x>=7){
            return "#EF2929"; // red
        }else if(x>=4){
            return "#FFBF00"; // yellow
        }else{
            return "#44F585"; // green
        }
    }

    public String setcapacity(int x){
        if(x>=1000){ //null
            return "#00000000";
        }else if(x>=7){
            return "#EF2929"; // red
        }else if(x>=4){
            return "#FFBF00"; // yellow
        }else{
            return "#44F585"; // green
        }
    }

    //unit = M = miles, K = kilometers, N =  Nautical Miles
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public String loadAssetJSON(String x) {
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




    //Thread



}

/*//// Time tick
 CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 10000) {

        // This is called every interval. (Every 10 seconds in this example)
        public void onTick(long millisUntilFinished) {
            Log.d("test","Timer tick");
        }

        public void onFinish() {
            Log.d("test","Timer last tick");
            start();
        }
     }.start();



    private void CopyAssets() {
    AssetManager assetManager = getAssets();
    String[] files = null;



    System.out.println("File name => "+filename);
    InputStream in = null;
    OutputStream out = null;
    try {
        in = assetManager.open(YOUR_ASSETS_FILE);   // if files resides inside the "Files" directory itself
        out = new FileOutputStream(STORAGE_PATH).toString() +"/" + filename);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
    } catch(Exception e) {
       e.printStackTrace();
    }

}
private void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
        out.write(buffer, 0, read);
    }
}
 */

