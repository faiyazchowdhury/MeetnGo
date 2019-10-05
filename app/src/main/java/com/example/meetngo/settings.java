package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_settings);
        final Switch notifications = findViewById(R.id.notifications);
        final Switch onlywhenfree = findViewById(R.id.onlywhenfree);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String email = mAuth.getCurrentUser().getEmail().toString();
        final String add_user = returnUsername(email);
        mDatabase.child("Users").child(add_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("settings").hasChild("notifications")){
                    if(((Long) dataSnapshot.child("settings").child("notifications").getValue()).intValue() == 1){
                        notifications.setChecked(true);
                    }

                    if(((Long) dataSnapshot.child("settings").child("onlywhenfree").getValue()).intValue() == 1){
                        onlywhenfree.setChecked(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button enable = findViewById(R.id.enable);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                startActivity(s);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent back = new Intent(this, freeness.class);
        mAuth = FirebaseAuth.getInstance();
        if(keyCode == KeyEvent.KEYCODE_BACK){

            final Switch notifications = findViewById(R.id.notifications);
            final Switch onlywhenfree = findViewById(R.id.onlywhenfree);
            final String email = mAuth.getCurrentUser().getEmail().toString();
            final String add_user = returnUsername(email);
            mDatabase.child("Users").child(add_user).child("settings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(notifications.isChecked()){
                        mDatabase.child("Users").child(add_user).child("settings").child("notifications").setValue(1);
                    }
                    else{
                        mDatabase.child("Users").child(add_user).child("settings").child("notifications").setValue(0);
                    }

                    if(onlywhenfree.isChecked()){
                        mDatabase.child("Users").child(add_user).child("settings").child("onlywhenfree").setValue(1);
                    }
                    else{
                        mDatabase.child("Users").child(add_user).child("settings").child("onlywhenfree").setValue(0);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            startActivity(back);
        }
        return super.onKeyDown(keyCode, event);
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

}
