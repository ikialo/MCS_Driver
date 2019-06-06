package com.MCS_Software.app.mcs_driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class PendingActivity extends AppCompatActivity implements PendingAdapter.OnItemClickListener {


    private DatabaseReference mDatabase, driverDB;

    private List<RequestedInfo> mList;
    private List<String> listDriver;
    RecyclerView recyclerView;
    PendingAdapter adapter;
    String [] check;
    int iterateNumber;

    List<String> listCar;
     SharedPreferences sharedPreferences;

    private int selectedDriverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        mDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.testPend));
        driverDB = FirebaseDatabase.getInstance().getReference(getString(R.string.driver));
        mList = new ArrayList<>();
        listDriver = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewPending);
        adapter  = new PendingAdapter(PendingActivity.this,mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(PendingActivity.this));

        adapter.setOnItemClickListener(PendingActivity.this);

        recyclerView.setAdapter(adapter);


        sharedPreferences = getSharedPreferences(getString(R.string.carNamePass),MODE_PRIVATE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mList.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                        if (dataSnapshot1.child("car").getValue().toString().equals(sharedPreferences.getString(getString(R.string.vehName), "na"))) {


                            mList.add(new RequestedInfo(dataSnapshot1.child("date").getValue().toString(),
                                    dataSnapshot1.child("time").getValue().toString(),
                                    dataSnapshot1.child("fair").getValue().toString(),
                                    dataSnapshot1.child("destin").getValue().toString(),
                                    dataSnapshot1.child("origin").getValue().toString(),
                                    new LatLng((double) dataSnapshot1.child("oriLatLng").child("latitude").getValue(), (double) dataSnapshot1.child("oriLatLng").child("longitude").getValue()),
                                    new LatLng((double) dataSnapshot1.child("desLatLng").child("latitude").getValue(), (double) dataSnapshot1.child("desLatLng").child("longitude").getValue())
                                    , dataSnapshot1.child("name").getValue().toString(),
                                    dataSnapshot1.child("userID").getValue().toString())
                            );
                            Log.d("dataSnapshot1", "onDataChange: " + dataSnapshot1.child("Date").getValue());
//                         }
                        }
//




                    }



                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Click at Position"+ position, Toast.LENGTH_SHORT).show();

       // check =  listCar.toArray(new String[listCar.size()]);


        checkBox(position);
       // onAmendClick(check,position);
    }




    public List<String> checkBox(final int pos){

        final List<String> genRep;

        genRep = new  ArrayList<String>();


        AlertDialog.Builder builder = new AlertDialog.Builder(PendingActivity.this);
        builder.setTitle("Choose Option");


        builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                Toast.makeText(PendingActivity.this, "num"+pos, Toast.LENGTH_SHORT).show();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       // mList.clear();
                        int i = 0;

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                            if (dataSnapshot1.child("car").getValue().toString().equals(sharedPreferences.getString(getString(R.string.vehName), "na"))) {


                                if(pos == i){

                                    String userID = dataSnapshot1.child("userID").getValue().toString();
                                    String carNum = dataSnapshot1.child("car").getValue().toString();

                                    sharedPreferences.edit().putString(getString(R.string.userId),userID).apply();
                                    sharedPreferences.edit().putString(getString(R.string.carNum), carNum).apply();
                                    sharedPreferences.edit().putBoolean(getString(R.string.enablePick), true).apply();

                                    sharedPreferences.edit().putString(getString(R.string.tripID),dataSnapshot1.getKey()).apply();


                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PickUpRead/"
                                    + userID);

                                    databaseReference.child("Car").setValue(carNum);

                                    startActivity(new Intent(PendingActivity.this, MapsActivity.class));
                                }

                                i++;
                            }
//




                        }



                        adapter.notifyDataSetChanged();

                        mDatabase.removeEventListener(this);

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



    public void onAmendClick(String[] check, final int pos) {

        Toast.makeText(PendingActivity.this, "AmendClick", Toast.LENGTH_SHORT).show();

        final String [] amendOption = check;



        AlertDialog.Builder builder = new AlertDialog.Builder(PendingActivity.this)  ;
        builder.setCancelable(true);
        builder.setSingleChoiceItems(amendOption, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {

                Toast.makeText(PendingActivity.this, amendOption[which], Toast.LENGTH_SHORT).show();

                selectedDriverName = which;

             //   dialog.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PendingActivity.this, "Cancel", Toast.LENGTH_SHORT).show();


            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {


                Log.d("WHICH", "onClick: "+selectedDriverName);

                creatPendingDB(pos, amendOption[selectedDriverName]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 11200
    private void creatPendingDB(final int position, final String car) {

        iterateNumber = 0;


        final DatabaseReference clientNameDB = FirebaseDatabase.getInstance().getReference(getString(R.string.testReq));
        final DatabaseReference dbpend = FirebaseDatabase.getInstance().getReference("TestPendingTrips");
        clientNameDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (position == iterateNumber) {

                        RequestedInfo requestedInfo =new RequestedInfo(dataSnapshot1.child("Date").getValue().toString(),
                                dataSnapshot1.child("Time").getValue().toString(),
                                dataSnapshot1.child("Fare").getValue().toString(),
                                dataSnapshot1.child("Destination").getValue().toString(),
                                dataSnapshot1.child("Origin").getValue().toString(),
                                new LatLng((double) dataSnapshot1.child("OriginLatLng").child("latitude").getValue(), (double) dataSnapshot1.child("OriginLatLng").child("longitude").getValue()),
                                new LatLng((double) dataSnapshot1.child("DestinLatLng").child("latitude").getValue(), (double) dataSnapshot1.child("DestinLatLng").child("longitude").getValue())
                                , dataSnapshot1.child("UserName").getValue().toString(),
                                dataSnapshot1.child("UserId").getValue().toString(), car,"driver");


                        String uploadId = dbpend.push().getKey();
                        dbpend.child(uploadId).setValue(requestedInfo);
                        clientNameDB.child(dataSnapshot1.getKey()).removeValue();
                        //break;
                    }
                    iterateNumber++;
                }

                // iterateNumber = 0;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void startTrackerService() {


        //startService(new Intent(this, TrackingService.class));

        startService(new Intent(this, AssignListenerService.class));



////Notify the user that tracking has been enabled//

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

//Close MainActivity//

        // finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startTrackerService();
    }

}
