package com.example.meetngo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;
import java.util.concurrent.Executor;

public class TimerandLocationService extends Service {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public int timeRemaining;
    private String email, add_user;


    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    private FusedLocationProviderClient mFusedLocationClient;
    public CountDownTimer countDownTimer;

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.example.meetngo.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    public String notif_no;

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail().toString();
        add_user = returnUsername(email);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        timeRemaining = intent.getIntExtra("timeRemaining",30);
        //notif_no = intent.getStringExtra("notif_num");
        //Toast.makeText(this, notif_no, Toast.LENGTH_SHORT).show();
        countDownTimer = new CountDownTimer(timeRemaining*60*1000, 1000) {
            @Override
            public void onTick(long l) {
                timeRemaining = (((int)l)/1000)/60;
                bi.putExtra("countdown", l);
                sendBroadcast(bi);
                mDatabase.child("Users").child(add_user).child("duration").setValue(timeRemaining);
                //mDatabase.child("Notifications").child(notif_no).child("duration").setValue(timeRemaining);
            }

            @Override
            public void onFinish() {
                mDatabase.child("Users").child(add_user).child("freeness").setValue(0);
                onDestroy();
            }
        }.start();
        getLocation();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        countDownTimer.cancel();
        stopSelf();
        super.onDestroy();
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }


    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        final LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");
                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            mDatabase.child("Users").child(add_user).child("location").child("latitude").setValue(location.getLatitude());
                            mDatabase.child("Users").child(add_user).child("location").child("longitude").setValue(location.getLongitude());
                            //mDatabase.child("Notifications").child(notif_no).child("location").child("latitude").setValue(location.getLatitude());
                            //mDatabase.child("Users").child(notif_no).child("location").child("longitude").setValue(location.getLongitude());
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

}
