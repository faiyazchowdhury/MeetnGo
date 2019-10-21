package com.example.meetngo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryService extends Service {

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    public String email, add_user;
    public ArrayList<String> free_friends = new ArrayList<String>();
    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        email = mAuth.getCurrentUser().getEmail();
        add_user = returnUsername(email);

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
        super.onStartCommand(intent, flags, startID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> n = (HashMap<String, Object>) dataSnapshot.child("Notifications").getValue();
                for(Map.Entry<String, Object> entry : n.entrySet()){
                    try {
                        HashMap<String, Object> notifs = (HashMap<String, Object>) entry.getValue();
                        if(notifs.get("sender").equals(add_user)){
                             HashMap<String, Object> groups = (HashMap<String, Object>) notifs.get("groups");
                             for(Map.Entry<String, Object> entry1 : groups.entrySet()){
                                 HashMap<String, Object> g1 = (HashMap<String, Object>) entry1.getValue();
                                 for(Map.Entry<String, Object> entry2 : g1.entrySet()){
                                     free_friends.add(entry2.getKey());
                                 }
                             }
                        }
                    }
                    catch(Exception e){
                        Log.i("exception", e.getMessage());
                    }
                }

                String message = "";
                for(int i=0;i<free_friends.size();i++){
                    if(i == 0){
                        message = message + free_friends.get(i);
                    }
                    else{
                        message = message + ", " + free_friends.get(i);
                    }
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    String CHANNEL_ID = "my_channel_02";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            "My Channel",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle("Some of your friends are free!")
                            .setContentText("Free friends are : "+message).build();

                    
                }


                onDestroy();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        stopSelf();
        super.onDestroy();
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }
}
