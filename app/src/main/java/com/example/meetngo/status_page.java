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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class status_page extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    int timeRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_page);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail().toString();
        Intent duration_intent = getIntent();
        timeRemaining = duration_intent.getIntExtra("timeRemaining",1);
        final Intent home = new Intent(this, freeness.class);
        Button edit = findViewById(R.id.Edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(home);
                startLocationService(0);
            }
        });
        startLocationService(1);
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
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);
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

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            final Intent home = new Intent(this, freeness.class);
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            TextView timer = findViewById(R.id.timer);
            int minutes = ((int) millisUntilFinished / 1000) / 60;
            int seconds = ((int) millisUntilFinished / 1000) - 60*minutes;
            timer.setText(minutes + " : " + seconds);
            if(minutes == 0 && seconds == 0){
                Toast.makeText(this, "You're freeness duration has expired", Toast.LENGTH_SHORT).show();
                startActivity(home);
            }
        }

    }

}
