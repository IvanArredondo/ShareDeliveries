package com.walnutapps.sharedeliveries;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    DatabaseReference mDatabase;


    LocationManager locationManager;
    LocationListener locationListener;

    LatLng userLatLng;
    Location userLocation;

    ArrayList<String> nearbyOrderIds = new ArrayList<>();

    public void addOrder(View v){

        Log.i("in order", "working");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle("Start an order");
        //test


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Add the buttons
        builder.setView(inflater.inflate(R.layout.add_order, null));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                Dialog f = (Dialog) dialog;

                EditText restaurantEditText = f.findViewById(R.id.restuarant);
                EditText descriptionEdittext = f.findViewById(R.id.description);
                EditText numberOfMembersEditText = f.findViewById(R.id.numberOfMembers);
                EditText totalPriceEditText = f.findViewById(R.id.totalPrice);

                if(!restaurantEditText.getText().toString().isEmpty() && !descriptionEdittext.getText().toString().isEmpty() && !numberOfMembersEditText.getText().toString().isEmpty() && !totalPriceEditText.getText().toString().isEmpty()){

                    Log.i("Fields:", "All full");

                    Order newOrder = new Order(restaurantEditText.getText().toString(), descriptionEdittext.getText().toString(), Integer.parseInt(numberOfMembersEditText.getText().toString()), Float.parseFloat(totalPriceEditText.getText().toString()), userLatLng);
                    mDatabase = FirebaseDatabase.getInstance().getReference("orders");
                    String key =  mDatabase.push().getKey();
                    mDatabase.child(key).setValue(newOrder);
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("locations").child(key).setValue(userLatLng);
                }



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        userLocation = new Location("");

        getOrdersNearMe();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                View v = getLayoutInflater().inflate(R.layout.info_window, null);
//                return null;
//
//            }
//        });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateMap(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null){
                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    public void updateMap(Location location){


            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            userLocation.setLatitude(userLatLng.latitude);
            userLocation.setLongitude(userLatLng.longitude);

            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
            //mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));

        Log.i("my location", userLatLng.toString());
    }

    public void getOrdersNearMe(){

        mDatabase.child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("datasnapshot: ", dataSnapshot.toString());

                Iterable<DataSnapshot> ds = dataSnapshot.getChildren();
                Iterator<DataSnapshot> ids = ds.iterator();

                while(ids.hasNext()){
                    DataSnapshot location = ids.next();
                    Double latitude = Double.parseDouble(location.child("latitude").getValue().toString());
                    Double longitude = Double.parseDouble(location.child("longitude").getValue().toString());

                    Location currentLocation = new Location("");
                    currentLocation.setLatitude(latitude);
                    currentLocation.setLongitude(longitude);

                    if(userLocation.distanceTo(currentLocation) < 1000 ){
                        nearbyOrderIds.add(location.getKey());
                    }

                }



                for(String orderId : nearbyOrderIds){
                    mDatabase.child("orders").child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LatLng orderLatLng = new LatLng(Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString()), Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString()));
                            mMap.addMarker(new MarkerOptions().position(orderLatLng).title(dataSnapshot.child("restaurant").getValue().toString()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
