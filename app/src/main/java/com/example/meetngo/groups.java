package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class groups extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_groups);
        final Intent settings_intent = new Intent(this, settings.class);
        TextView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settings_intent);
            }
        });

        String email = mAuth.getCurrentUser().getEmail().toString();
        final String add_user = returnUsername(email);
        mDatabase.child("Users").child(add_user).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    HashMap<String, Object> contacts = (HashMap<String, Object>) dataSnapshot.getValue();
                    String c = "";
                    for(Map.Entry<String, Object> entry : contacts.entrySet()){
                        c = c + entry.getKey();
                    }
                    Toast.makeText(groups.this, c, Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, String> contacts = new HashMap<>();
                    contacts.put("ABC", "1234567890");
                    contacts.put("XYZ", "12345567890");
                    contacts.put("PQR", "1234567890");
                    for(Map.Entry<String, String> entry : contacts.entrySet()){
                        mDatabase.child("Users").child(add_user).child("groups").child(entry.getKey()).setValue(entry.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

}
