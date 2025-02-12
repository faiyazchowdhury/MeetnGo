package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class status_page extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    int timeRemaining;
    ArrayList<String> free_friends = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_page);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String email = mAuth.getCurrentUser().getEmail().toString();
        Intent duration_intent = getIntent();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, free_friends);
        listView = (ListView) findViewById(R.id.free_friends);
        listView.setAdapter(arrayAdapter);
        timeRemaining = duration_intent.getIntExtra("timeRemaining",60);

        final Intent settings_intent = new Intent(this, settings.class);
        TextView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settings_intent);
            }
        });

        final Intent select_groups = new Intent(this, select_groups.class);
        Button edit = findViewById(R.id.Edit);
        mDatabase.child("Users").child(returnUsername(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("freeness").getValue().toString().equals("1")){ // If Free
                    Button edit = findViewById(R.id.Edit);
                    edit.setText("NOT FREE ANYMORE");
                    startLocationService(1);
                }
                else{ // If not free
                    Button edit = findViewById(R.id.Edit);
                    edit.setText("SEND A BLAST");
                    TextView timer = findViewById(R.id.timer);
                    timer.setText( "SEND A BLAST to let friends know you are free." );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Users").child(returnUsername(email)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("freeness").getValue().toString().equals("1")){ // If Free (Not free Anymore)
                            startLocationService(0);
                            mDatabase.child("Users").child(returnUsername(email)).child("freeness").setValue(0);
                            Button edit = findViewById(R.id.Edit);
                            edit.setText("SEND A BLAST");
                            TextView timer = findViewById(R.id.timer);
                            timer.setText( "SEND A BLAST to let friends in groups know you are free." );
                        } if(!dataSnapshot.child("groups").hasChildren()) {
                            Toast.makeText(status_page.this, "Please add a group with the Groups button.", Toast.LENGTH_SHORT).show();// Send blast
                          //  final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
                          //  Button groups_button = (Button) findViewById(R.id.groups);
                          //  groups_button.startAnimation(animShake);
                        } else  { // Send Blast
                            startQueryService(0);
                            if(dataSnapshot.child("freeness").getValue().toString().equals("0")) { // If Free (Not free Anymore){
                                startActivity(select_groups);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final Intent groups_intent = new Intent(this, groups.class);
        TextView groups = findViewById(R.id.groups);
        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(groups_intent);
            }
        });

        startQueryService(1);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(TimerandLocationService.COUNTDOWN_BR));
        registerReceiver(br1, new IntentFilter(QueryService.FREE_FRIENDS_BR));
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(br);
        unregisterReceiver(br1);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //registerReceiver(br, new IntentFilter(TimerandLocationService.COUNTDOWN_BR));
        //unregisterReceiver(br);
    }

    private void startLocationService(int flag){
            Intent serviceIntent = new Intent(this, TimerandLocationService.class);
            if(flag == 1) {
                serviceIntent.putExtra("timeRemaining", timeRemaining);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    status_page.this.startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
            else{
                stopService(serviceIntent);
            }
    }

    private void startQueryService(int flag){
        Intent serviceIntent1 = new Intent(this, QueryService.class);
        if(flag == 1) {
            serviceIntent1.putExtra("timeRemaining", timeRemaining);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                status_page.this.startForegroundService(serviceIntent1);
            } else {
                startService(serviceIntent1);
            }
        }
        else{
            stopService(serviceIntent1);
        }
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private BroadcastReceiver br1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI1(intent);
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            timeRemaining = (int) millisUntilFinished;
            TextView timer = findViewById(R.id.timer);
            int minutes = ((int) millisUntilFinished / 1000) / 60;
            int seconds = ((int) millisUntilFinished / 1000) - 60*minutes;
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String email = mAuth.getCurrentUser().getEmail().toString();
            final String add_user = returnUsername(email);
            timer.setText("You're Free for " + String.valueOf(minutes) + ":" + String.valueOf(seconds));
            if(minutes == 0 && seconds == 0){
                mDatabase.child("Users").child(add_user).child("freeness").setValue(0);
                Toast.makeText(this, "You're freeness duration has expired", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void updateGUI1(Intent intent) {
        ArrayList<String> temp_free_friends = new ArrayList<>();
        temp_free_friends = intent.getStringArrayListExtra("free_friends");
        if(temp_free_friends.size() == 0){
            String message = "Oh no! No one else is free! Please spread the word about the app! You can still SEND A BLAST, and the chosen groups will be notified if their No Distractions setting is off. You can add groups in the Groups button above.";
            if(!free_friends.contains(message)){
                free_friends.add(message);
            }

        }
        for(int i=0;i<temp_free_friends.size();i++){
            if(!free_friends.contains(temp_free_friends.get(i))){
                free_friends.add(temp_free_friends.get(i));

            }
        }
        //Toast.makeText(this, free_friends.toString(), Toast.LENGTH_SHORT).show();
        /*if(!free_friends.contains("Brooke Brennan")){free_friends.add("Brooke Brennan");}
        if(!free_friends.contains("Reagan Hanna")){free_friends.add("Reagan Hanna");}
        if(!free_friends.contains("Faiyaz Chowdhury")){free_friends.add("Faiyaz Chowdhury");}*/
        arrayAdapter.notifyDataSetChanged();
    }

}
