package com.MCS_Software.app.mcs_driver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    EditText pass, user;
    Button login;
    TextView vehicleNumber;
    List <String> listCar;
    DatabaseReference mDatabaseRef;
    String [] check;
    private  int selectedDriverName;
    SharedPreferences sp_tripInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pass = findViewById(R.id.passWord);
        user = findViewById(R.id.userName);
        login = findViewById(R.id.login);
        vehicleNumber = findViewById(R.id.vehicleNum);

        sp_tripInfo = getSharedPreferences(getString(R.string.carNamePass), MODE_PRIVATE);




        sp_tripInfo.edit().putString(getString(R.string.tripID),"tripid").apply();

        sp_tripInfo.edit().putBoolean(getString(R.string.enablePick), false).apply();


        listCar = new ArrayList<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference(getString(R.string.vehicles));

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listCar.clear();
                for (DataSnapshot post : dataSnapshot.getChildren()){


                    listCar.add(post.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.driver));


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!vehicleNumber.getText().toString().equals(getString(R.string.vehicle_number))){
                    checkLogin();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Select A Car", Toast.LENGTH_SHORT).show();
                }

            }
        });

        vehicleNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builders = new AlertDialog.Builder(MainActivity.this);
                builders.setTitle("Admin Detail");

                Context context = MainActivity.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Add a TextView here for the "Title" label, as noted in the comments
                final EditText driver1 = new EditText(context);
                driver1.setHint("User Name");
                layout.addView(driver1); // Notice this is an add method

                // Add another TextView here for the "Description" label
                final EditText Car = new EditText(context);
                Car.setHint("Password");
                layout.addView(Car); // Another add method

                builders.setView(layout); // Again this is a set method, not add

// Set up the buttons
                builders.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       // creatPendingDB(position,Car,driver1);

                        Toast.makeText(MainActivity.this, "IN OK", Toast.LENGTH_SHORT).show();


                        if (driver1.getText().toString().trim().equals("mine")) {
                            Toast.makeText(MainActivity.this, driver1.getText().toString(), Toast.LENGTH_SHORT).show();

                            check =  listCar.toArray(new String[listCar.size()]);


                            //checkBox(check);
                            onAmendClick(check);
                        }

                    }
                });


                builders.show();

            }
        });
    }

    private void checkLogin() {

        final boolean[] checkUser = {false};

        Log.d("testLogin1", "onClick: "+ vehicleNumber.getText().toString());

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("testLogin2", "onClick: "+ vehicleNumber.getText().toString());

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    if (postSnapshot.child("UserName").getValue().equals(user.getText().toString().trim())
                    && postSnapshot.child("Password").getValue().toString().equals(pass.getText().toString().trim())){

                        Toast.makeText(MainActivity.this, "SIGNED IN", Toast.LENGTH_SHORT).show();

                        checkUser[0] = true;

                        sp_tripInfo.edit().putString(getString(R.string.driverName),postSnapshot.child("Name").getValue().toString()).apply();


                        mDatabaseRef.child(postSnapshot.getKey()).child(getString(R.string.availability)).setValue("true");
                        finish();
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        break;

                    }

                }

                if (!checkUser[0]){
                    Toast.makeText(MainActivity.this, "USERNAME or PASSWORD INCORRECT", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void onAmendClick(String[] check) {

        Toast.makeText(MainActivity.this, "AmendClick", Toast.LENGTH_SHORT).show();

        final String [] amendOption = check;



        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)  ;
        builder.setCancelable(true);
        builder.setSingleChoiceItems(amendOption, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {

                Toast.makeText(MainActivity.this, amendOption[which], Toast.LENGTH_SHORT).show();

                selectedDriverName = which;

                //   dialog.dismiss();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();


            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {


                Log.d("WHICH", "onClick: "+selectedDriverName);
                final DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Car Location");


                String carName = amendOption[selectedDriverName];

                dref.child(carName).setValue(new LatLng(-9.4438, 147.1803));

                vehicleNumber.setText(carName);

                sp_tripInfo.edit().putString(getString(R.string.vehName),vehicleNumber.getText().toString()).apply();




//                dref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot postSnapShot:  dataSnapshot.getChildren()){
//
//                            if (amendOption[selectedDriverName].equals(postSnapShot.child("Name").getValue().toString())){
//
//                                dref.child(postSnapShot.getKey()).child(getString(R.string.assigned)).setValue(true);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        vehicleNumber.setText(sp_tripInfo.getString(getString(R.string.vehName), getString(R.string.vehicle_number)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        vehicleNumber.setText(sp_tripInfo.getString(getString(R.string.vehName), "Na"));

    }
}
