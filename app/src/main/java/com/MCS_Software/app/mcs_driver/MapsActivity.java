package com.MCS_Software.app.mcs_driver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST = 250;
    private GoogleMap mMap;
    private Button picked, checkTrips;
    String tripId;
    SharedPreferences sp_tripInfo;
    LatLng origin, destination;
    Button drop;
    TextView cliTripInfo, screenWake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        picked = findViewById(R.id.picked);
        checkTrips = findViewById(R.id.checkTrip);
        drop = findViewById(R.id.dropoff);
        cliTripInfo = findViewById(R.id.clientTripInfo);

        sp_tripInfo = getSharedPreferences(getString(R.string.carNamePass), MODE_PRIVATE);

        sp_tripInfo.getString(getString(R.string.vehName), getString(R.string.vehicle_number));
        tripId = sp_tripInfo.getString(getString(R.string.tripID), "tripid");




        if (sp_tripInfo.getBoolean(getString(R.string.enablePick), false)) {

            picked.setEnabled(true);
            checkTrips.setEnabled(false);
        } else {
            picked.setEnabled(false);
            checkTrips.setEnabled(true);
            sp_tripInfo.edit().putBoolean(getString(R.string.enablePick), false).apply();
        }


        picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                confirmPick();


            }
        });




        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              confirmDrop();
            }
        });


        checkTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, PendingActivity.class));
            }
        });


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //finish();
        }

        //Check whether this app has access to the location permission//


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


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

        // Add a marker in Sydney and move the camera


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
       final  LatLng sydney = new LatLng(-9.4438, 147.1803);


        DatabaseReference currentLocation = FirebaseDatabase.getInstance().
                getReference("Car Location/"+ sp_tripInfo.getString(getString(R.string.vehName), "NA"));




        currentLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                        (double) dataSnapshot.child("latitude").getValue(),
                        (double) dataSnapshot.child("longitude").getValue()

                )));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        if (!tripId.equals("tripid")){

            final DatabaseReference dbref1 = FirebaseDatabase
                    .getInstance().getReference(getString(R.string.testPend)+"/"+tripId);


            dbref1.child("showOriInClient").setValue(true);

            Log.d("Test Trip ID", "onCreate: "+ tripId);

            dbref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("Test ", "onDataChange: "+dataSnapshot.child("name").getValue());


                    origin = new LatLng((double) dataSnapshot.child("oriLatLng").child("latitude").getValue(),
                            (double) dataSnapshot.child("oriLatLng").child("longitude").getValue() );
                    destination =new LatLng((double)dataSnapshot.child("desLatLng").child("latitude").getValue(),
                            (double) dataSnapshot.child("desLatLng").child("longitude").getValue() );

                    cliTripInfo.setText(
                           "Client Name: "+ dataSnapshot.child("name").getValue().toString() +"\n"+
                           "Time: " +dataSnapshot.child("time").getValue().toString()
                    );


                    Log.d("POSITION", "onDataChange: "+origin);


                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-9.4438, 147.1803), 12));

                    mMap.addMarker(new MarkerOptions().position(origin).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.addMarker(new MarkerOptions().position(destination).title("Marker in Sydney"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    dbref1.removeEventListener(this);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

//If the permission has been granted...// 3400139

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//

            startTrackerService();
        } else {

//If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

//Start the TrackerService//


    private void startTrackerService() {


        startService(new Intent(this, TrackingService.class));
        //startService(new Intent(this, AssignListenerService.class));



////Notify the user that tracking has been enabled//

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();


    }



    public List<String> confirmPick(){

        final List<String> genRep;

        genRep = new ArrayList<String>();


        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Confirm Pick Up");


        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK

                picked.setEnabled(false);

                picked.setVisibility(View.GONE);
                drop.setVisibility(View.VISIBLE);
                String userID = sp_tripInfo.getString(getString(R.string.userId), "USER");
                String carNum = sp_tripInfo.getString(getString(R.string.carNum), "NA");

                DatabaseReference db_Stop_sendingLoc = FirebaseDatabase.getInstance().getReference("PickUpRead/" + userID);

                db_Stop_sendingLoc.child("Car").setValue("NA");

            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return  genRep;
    }



    public List<String> confirmDrop(){

        final List<String> genRep;

        genRep = new ArrayList<String>();


       final String driver = sp_tripInfo.getString(getString(R.string.driverName), getString(R.string.vehicle_number));


        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Confirm Drop Off");


        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK

                picked.setVisibility(View.GONE);
                drop.setEnabled(false);
                drop.setVisibility(View.VISIBLE);
                sp_tripInfo.edit().putBoolean(getString(R.string.enablePick), false).apply();
                checkTrips.setEnabled(true);

                final DatabaseReference dbref = FirebaseDatabase
                        .getInstance().getReference(getString(R.string.testPend) + "/" + tripId);

                final DatabaseReference completedReference = FirebaseDatabase.getInstance().getReference(getString(R.string.compTrips));

                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();
                        String car = dataSnapshot.child("car").getValue().toString();
                        String destin = dataSnapshot.child("destin").getValue().toString();
                      ///  String driver = dataSnapshot.child("driver").getValue().toString();
                        String fare = dataSnapshot.child("fair").getValue().toString();
                        String origin = dataSnapshot.child("origin").getValue().toString();
                        String userID = dataSnapshot.child("userID").getValue().toString();
                        double desLat = (double) dataSnapshot.child("desLatLng").child("latitude").getValue();
                        double desLon = (double) dataSnapshot.child("desLatLng").child("longitude").getValue();
                        double oriLat = (double) dataSnapshot.child("oriLatLng").child("latitude").getValue();
                        double oriLng = (double) dataSnapshot.child("oriLatLng").child("longitude").getValue();


                        JourneyInfo journeyInfo = new JourneyInfo(name, date, time, driver, car, userID, destin, origin,
                                fare, new LatLng(desLat, desLon), new LatLng(oriLat, oriLng));
                        String uploadId = completedReference.push().getKey();

                        completedReference.child(uploadId).setValue(journeyInfo);


                        dbref.removeEventListener(this);
                        dbref.removeValue();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return  genRep;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }

//    private LatLng requestLocationUpdates() {
//        LocationRequest request = new LocationRequest();
//
//
//        final LatLng[] latLng = new LatLng[1];
//
//
////Specify how often your app should request the device’s location//
//
//        request.setInterval(1000);
//
////Get the most accurate location data available//
//
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//        final String path = "Car Location/"+ sp_tripInfo.getString(getString(R.string.vehName), "na");
//        int permission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
//
////If the app currently has access to the location permission...//
//
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//
////...then request location updates//
//
//            client.requestLocationUpdates(request, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//
////Get a reference to the database, so your app can perform read and write operations//
//
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//
////Save the location data to the database//
//
//                         latLng[0] = new LatLng(location.getLatitude(),location.getLongitude());
//                        Log.d("TEST", "onLocationResult: "+ location.getLongitude());
//
//
//
//                        //ref.setValue(latLng);
//                    }
//                }
//            }, null);
//        }
//
//        return latLng[0];
//    }
}
