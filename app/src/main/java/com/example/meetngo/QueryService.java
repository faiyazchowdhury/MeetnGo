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
import androidx.core.app.NotificationManagerCompat;

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
    public static final String FREE_FRIENDS_BR = "com.example.meetngo.free_friends_br";
    Intent bi1 = new Intent(FREE_FRIENDS_BR);
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
            String CHANNEL_ID = "my_channel_02";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(2, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        super.onStartCommand(intent, flags, startID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> n = (HashMap<String, Object>) dataSnapshot.child("Notifications").getValue();
                HashMap<String, Object> users = (HashMap<String, Object>) dataSnapshot.child("Users").getValue();
                for(Map.Entry<String, Object> entry : n.entrySet()){
                    try {
                        HashMap<String, Object> notifs = (HashMap<String, Object>) entry.getValue();
                        if(notifs.get("sender").equals(add_user)){
                             HashMap<String, Object> receivers = (HashMap<String, Object>) notifs.get("receivers");
                             for(Map.Entry<String, Object> entry2 : receivers.entrySet()){
                                 if(!free_friends.contains(entry2.getKey())){
                                     for(Map.Entry<String, Object> entry3 : users.entrySet()){
                                         HashMap<String, Object> attributes = (HashMap<String, Object>) entry3.getValue();
                                         if(attributes.get("phone").equals(entry2.getValue())){
                                             if(attributes.get("freeness").equals((long) 1)){
                                                 free_friends.add(entry2.getKey());
                                             }
                                             else{
                                                 if(free_friends.contains(entry2.getKey())){
                                                     free_friends.remove(entry2.getKey());
                                                 }
                                             }
                                         }
                                     }

                                 }
                             }
                        }

                        HashMap<String, Object> receivers = (HashMap<String, Object>) notifs.get("receivers");
                        String my_phone = (String) dataSnapshot.child("Users").child(add_user).child("phone").getValue();
                        for(Map.Entry<String, Object> receiver : receivers.entrySet()){
                            if(my_phone.equals(receiver.getValue().toString())){
                                if(!free_friends.contains(notifs.get("sender").toString()) && dataSnapshot.child("Users").child(notifs.get("sender").toString()).child("freeness").getValue().toString().equals("1")) {
                                    free_friends.add(notifs.get("sender").toString());
                                }
                                if(dataSnapshot.child("Users").child(notifs.get("sender").toString()).child("freeness").getValue().toString().equals("0")) {
                                    if(free_friends.contains(notifs.get("sender").toString())){
                                        //free_friends.remove(notifs.get("sender").toString());
                                        free_friends.clear();
                                    }
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        Log.i("exception", e.getMessage());
                    }
                }

                bi1.putStringArrayListExtra("free_friends",free_friends);
                sendBroadcast(bi1);

                if(!free_friends.isEmpty()) {
                    String message = "";
                    for (int i = 0; i < free_friends.size(); i++) {
                        if (i == 0) {
                            message = message + free_friends.get(i);
                        } else {
                            message = message + ", " + free_friends.get(i);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        String CHANNEL_ID = "my_channel_03";
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                "My Channel",
                                NotificationManager.IMPORTANCE_LOW);

                        /*Intent intent = new Intent(getApplicationContext(), notification_tap.class);
                        intent.putStringArrayListExtra("free_friends", free_friends);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Some of your friends are free!!")
                                .setContentText(message)
                                //.setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);

                        NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
                        notificationManager1.notify(1, builder.build());
                    }

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
