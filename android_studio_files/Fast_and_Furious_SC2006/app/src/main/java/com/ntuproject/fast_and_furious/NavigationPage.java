package com.ntuproject.fast_and_furious;
//Source https://www.youtube.com/watch?v=KOBJkkhH9QY
//https://www.geeksforgeeks.org/how-to-draw-polyline-in-google-maps-in-android/
//https://developers.google.com/maps/documentation/javascript/examples/polyline-remove

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

// alt
// We run program in VM, Put the data to google cloud
// We use local comp

//API key: AIzaSyDCqi3kyqBKzBg_9t5_EOw__91KJ23WpRk (set ur own API key in AndroidManifest.xml)
/*
SupportFragment:
https://developers.google.com/android/reference/com/google/android/gms/maps/SupportMapFragment
https://developers.google.com/maps/documentation/android-sdk/map
https://abhiandroid.com/programming/googlemaps
 */

///To Edit
// Starting location : by default is current
// Suggestion box

public class NavigationPage extends HomeMainActivity implements OnMapReadyCallback{

    public float zoom = 10.5f;
    private GoogleMap mMap;
    private GoogleMap map;
    private ApiInterface apiInterface;
    private List<LatLng> polylinelist;
    private PolylineOptions polylineOptions;
    private LatLng origin1,destination1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigationpage);

        if(getIntent().hasExtra("fav")) {
            try {
                fav = new JSONObject( getIntent().getStringExtra("fav") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            favo = fav.getJSONArray("favourite");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //GoogleMap mMap = googleMap;
        double NTUlat = 1.3461733691799838;
        double NTUlong = 103.68185366931402;
        map = googleMap;


        for(int f=0;f<favo.length();f++){
            try {
                Marker x= map.addMarker(new MarkerOptions()
                        .position(new LatLng(favo.getJSONObject(f).getDouble("Latitude"),favo.getJSONObject(f).getDouble("Longitude")))
                        .title(favo.getJSONObject(f).getString("name"))
                        .snippet(favo.getJSONObject(f).getString("busname"))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                // below lin is use to zoom our camera on map.
                map.animateCamera(CameraUpdateFactory.zoomTo(zoom));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Add a marker in NTU and move the camera
        LatLng Singapore = new LatLng(NTUlat,NTUlong);
        Marker mark = map.addMarker(new MarkerOptions()
                .position(Singapore)
                .title("me")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        mark.showInfoWindow();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Singapore,zoom));

        map = googleMap;
        Polyline x = null;


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                //map.clear();

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setTrafficEnabled(true);

                map.addPolyline((new PolylineOptions()).add(Singapore,marker.getPosition())
                        // below line is use to specify the width of poly line.
                        .width(8)
                        // below line is use to add color to our poly line.
                        .color(Color.RED)
                        //.pattern(Dot)
                        // below line is to make our poly line geodesic.
                        .geodesic(true));
                // on below line we will be starting the drawing of polyline.

                LatLngBounds.Builder builder =new LatLngBounds.Builder();
                builder.include(new LatLng( NTUlat, NTUlong ));
                builder.include(new LatLng( marker.getPosition().latitude, marker.getPosition().latitude ));
                //map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),0));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Singapore, zoom));
                getDirection("1.3461733691799838"+","+"103.68185366931402", marker.getPosition().latitude+","+marker.getPosition().longitude);
                return false;
            }
        });

    }

    private void getDirection(String origin,String destination){

        String orilat = origin.substring(0,origin.indexOf(","));
        String orilong = origin.substring(origin.indexOf(",")+1);
        String destlat = destination.substring(0,destination.indexOf(","));
        String destlong = destination.substring(destination.indexOf(",")+1);

        apiInterface.getDirection("driving","less-driving",origin,destination,
                        getString(R.string.API_KEY)
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Result result) {
                        polylinelist = new ArrayList<>();
                        List<Route> routeList = result.getRoutes();
                        for(Route route : routeList){
                            String polyline = route.getOverviewPolyline().getPoints();
                            polylinelist.addAll(decodePoly(polyline));
                        }

                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(8);
                        polylineOptions.geodesic(true);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylinelist);
                        polylineOptions.isVisible();
                        map.addPolyline(polylineOptions);



                        LatLngBounds.Builder builder =new LatLngBounds.Builder();
                        builder.include(new LatLng( Double.valueOf(orilat), Double.valueOf(orilong) ));
                        builder.include(new LatLng( Double.valueOf(destlat), Double.valueOf(destlong) ));
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));


                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


    }

    //method to decode string to Latlong
    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat =0, lng = 0;

        while (index < len){
            int b,shift = 0,result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));

            poly.add(p);
        }
        return poly;
    }


}
