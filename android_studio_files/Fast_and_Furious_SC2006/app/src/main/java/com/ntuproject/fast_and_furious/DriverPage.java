package com.ntuproject.fast_and_furious;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DriverPage extends HomeMainActivity {
    public JSONObject carpark = null;
    public JSONArray carparkpo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverpage);

        ImageButton navigationB = (ImageButton) findViewById(R.id.NavigationAvatar);
        ImageButton parkingB = (ImageButton) findViewById(R.id.ParkingAvatar);

        Bundle b = getIntent().getExtras();
        Intent in = new Intent(DriverPage.this, ParkingPage.class);

        if(getIntent().hasExtra("carpark")) {
            try {
                carparko = new JSONObject( getIntent().getStringExtra("carpark") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        navigationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverPage.this, NavigationPage.class));
                System.out.println(head +"Go to Navigation Page");
            }
        });

        parkingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in.putExtra("carpark", carparko.toString());
                //startActivity(new Intent(DriverPage.this, ParkingPage.class));
                System.out.println(head +"Go to Parking Page");
                startActivity(in);
            }
        });

    }



}
