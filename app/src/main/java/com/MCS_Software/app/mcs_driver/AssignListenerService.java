package com.MCS_Software.app.mcs_driver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AssignListenerService extends Service {


    public AssignListenerService() {
    }


    private static final String TAG = TrackingService.class.getSimpleName();


    SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(getString(R.string.carNamePass),MODE_PRIVATE);

        // startForeground(101, getNotification());
       // buildNotification("My_service","my_background_service","Determining Metro Driver Location");
       // requestLocationUpdates();

        readAssign();
    }

    private void readAssign() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.testPend));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot post: dataSnapshot.getChildren()){


                    if (post.child("car").getValue().toString().equals(sharedPreferences.getString(getString(R.string.vehName), "NA"))){
                        Log.d(TAG, "onDataChange: "+ sharedPreferences.getString(getString(R.string.vehName), "NA"));


                        buildNotification("service_Read_assign", "ReadAssign_Backgroud", "New Trip Request:"+
                                post.child("car").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//Create the persistent notification//

    private void buildNotification(String channelID, String cahannelName, String message) {

        String chanID;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            chanID = createNotificationChannelId(channelID, cahannelName);

        }
        else{
            chanID = "";
        }

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder
                //   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this, chanID)//Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)


                //Make this notification ongoing so it can’t be dismissed by the user//

                .setSound(alarmSound)
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);



        startForeground(1025, builder.build());

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelId(String channelid, String channelName) {

        NotificationChannel chan = new NotificationChannel(channelid, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.YELLOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager service =  getSystemService(NotificationManager.class);

        service.createNotificationChannel(chan);

        return channelid;


    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            //
          //  stopSelf();
        }
    };



//Initiate the request to track the device's location//

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();



//Specify how often your app should request the device’s location//

        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = "Car Location/"+ sharedPreferences.getString(getString(R.string.vehName), "na");
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    Location location = locationResult.getLastLocation();
                    if (location != null) {

//Save the location data to the database//

                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                        Log.d("TEST", "onLocationResult: "+ location.getLongitude());
                        ref.setValue(latLng);
                    }
                }
            }, null);
        }
    }
}
