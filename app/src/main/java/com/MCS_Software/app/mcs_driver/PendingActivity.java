package com.MCS_Software.app.mcs_driver;

import android.content.DialogInterface;
import android.content.Intent;
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

    private int selectedDriverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        mDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.ReqTrip));
        driverDB = FirebaseDatabase.getInstance().getReference(getString(R.string.driver));
        mList = new ArrayList<>();
        listDriver = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewPending);
        adapter  = new PendingAdapter(PendingActivity.this,mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(PendingActivity.this));

        adapter.setOnItemClickListener(PendingActivity.this);

        recyclerView.setAdapter(adapter);





        driverDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){

                    if (postSnapShot.child(getString(R.string.assigned)).getValue() != null &&
                    postSnapShot.child(getString(R.string.assigned)).getValue().toString().equals("false")) {


                        listDriver.add(postSnapShot.child("Name").getValue().toString());


                    }

                }
//                int i= 0;
//                for(String name: listDriver){
//
//                    Log.d("NameList", "onCreate: "+name);
//
//                    check[i] = name;
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){


                     for ( DataSnapshot dataSnapshot1 :  postSnapShot.child(getString(R.string.trips)).getChildren()){



                         mList.add(new RequestedInfo(dataSnapshot1.child("Date").getValue().toString(),
                                 dataSnapshot1.child("Time").getValue().toString(),
                                 dataSnapshot1.child("Fare").getValue().toString(),
                                 dataSnapshot1.child("Destination").getValue().toString(),
                                 dataSnapshot1.child("Origin").getValue().toString(),
                                 new LatLng((double)dataSnapshot1.child("OriginLatLng").child("latitude").getValue(), (double)dataSnapshot1.child("OriginLatLng").child("longitude").getValue()),
                                 new LatLng((double)dataSnapshot1.child("DestinLatLng").child("latitude").getValue(), (double)dataSnapshot1.child("DestinLatLng").child("longitude").getValue())
                                 , postSnapShot.child("ClientInfo").child("fullName").getValue().toString()));
                         Log.d("dataSnapshot1", "onDataChange: "+ dataSnapshot1.child("Destination").getValue());
                     }

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

        check =  listDriver.toArray(new String[listDriver.size()]);


        //checkBox(check);
        onAmendClick(check);
    }




    public List<String> checkBox(final String [] checks){

        final List<String> genRep;

        genRep = new  ArrayList<String>();


        AlertDialog.Builder builder = new AlertDialog.Builder(PendingActivity.this);
        builder.setTitle("Choose some animals");

// add a checkbox list



        final boolean[] checkedItems = new boolean[checks.length]; //{false, false, false, false, false,false, false, false, false, false,false, false, false, false, false};
        builder.setMultiChoiceItems(checks, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box

                checkedItems[which] = isChecked;


            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK

                int i =0;
                for (String genRepitem: checks) {

                    if (checkedItems[i] == true){
                        genRep.add(genRepitem);
                        Log.d("GEN_REP", "onClick: " + genRepitem);
                    }
                    i++;

                }
            }
        });
        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return  genRep;
    }



    public void onAmendClick(String[] check) {

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
                final DatabaseReference dref = FirebaseDatabase.getInstance().getReference(getString(R.string.driver));


                dref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapShot:  dataSnapshot.getChildren()){

                            if (amendOption[selectedDriverName].equals(postSnapShot.child("Name").getValue().toString())){

                                dref.child(postSnapShot.getKey()).child(getString(R.string.assigned)).setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
