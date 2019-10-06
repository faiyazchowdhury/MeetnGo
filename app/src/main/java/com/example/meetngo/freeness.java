package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class freeness extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    int final_distance=-1, final_duration=-1, f=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final String w_email = mAuth.getCurrentUser().getEmail().toString();
        setContentView(R.layout.activity_freeness);
        getValuesFromDatabase(w_email);
        final Intent i4 = new Intent(this, groups.class);
        final Intent settings_intent = new Intent(this, settings.class);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch free = findViewById(R.id.freeness);
                EditText message = findViewById(R.id.message);
                if(free.isChecked()){
                    f = 1;
                }
                else{
                    f = 0;
                }
                String m = message.getText().toString();

                sendValuesToDatabase(mAuth.getCurrentUser().getEmail().toString(), final_distance, final_duration, f, m);
                startActivity(i4);
            }
        });
        TextView w_email1 = findViewById(R.id.w_email);
        w_email1.setText(w_email);

        TextView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settings_intent);
            }
        });

        final SeekBar distance = findViewById(R.id.distance);
        SeekBar duration = findViewById(R.id.duration);


        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final_distance = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(freeness.this, "The set distance is "+final_distance+" minutes of walk", Toast.LENGTH_SHORT).show();
            }
        });

        duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final_duration = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distance.setMax(final_duration);
                Toast.makeText(freeness.this, "The set duration is "+final_duration+" minutes", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent logout = new Intent(this, login.class);
        mAuth = FirebaseAuth.getInstance();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            startActivity(logout);
        }
        return super.onKeyDown(keyCode, event);
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

    public void sendValuesToDatabase(String email, final int distance, final int duration, final int freeness, final String message){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String add_user = returnUsername(email);
        mDatabase.child("Users").child(add_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabase.child("Users").child(add_user).child("distance").setValue(distance);
                mDatabase.child("Users").child(add_user).child("duration").setValue(duration);
                mDatabase.child("Users").child(add_user).child("freeness").setValue(freeness);
                mDatabase.child("Users").child(add_user).child("message").setValue(message);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getValuesFromDatabase(String email){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String add_user = returnUsername(email);
        mDatabase.child("Users").child(add_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(((Long) dataSnapshot.child("freeness").getValue()).intValue() != -1){
                    if(((Long) dataSnapshot.child("freeness").getValue()).intValue() == 1){
                        Switch free = findViewById(R.id.freeness);
                        free.setChecked(true);
                    }
                    else{
                        Switch free = findViewById(R.id.freeness);
                        free.setChecked(false);
                    }
                }

                if(((Long) dataSnapshot.child("duration").getValue()).intValue() != -1){
                    SeekBar duration = findViewById(R.id.duration);
                    duration.setProgress(((Long) dataSnapshot.child("duration").getValue()).intValue());
                }

                if(((Long) dataSnapshot.child("distance").getValue()).intValue() != -1){
                    SeekBar duration = findViewById(R.id.distance);
                    duration.setProgress(((Long) dataSnapshot.child("distance").getValue()).intValue());
                }

                if(!dataSnapshot.child("message").getValue().toString().isEmpty()){
                    EditText message = findViewById(R.id.message);
                    message.setText(dataSnapshot.child("message").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
