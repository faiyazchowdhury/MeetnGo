package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Map;

public class contacts_in_group extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_in_group);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String email = mAuth.getCurrentUser().getEmail().toString();
        final String add_user = returnUsername(email);
        ListView listView = findViewById(R.id.contacts);
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        final Intent cig = getIntent();
        final String gn = cig.getStringExtra("group_name");
        final Intent cip = new Intent(this, contacts_in_phone.class);
        cip.putExtra("group_name", gn);
        TextView group_name = findViewById(R.id.groupname);
        group_name.setText(gn);
        mDatabase.child("Users").child(add_user).child("groups").child(gn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> contacts = (HashMap<String, String>) dataSnapshot.getValue();
                for(Map.Entry<String, String> entry : contacts.entrySet()){
                    arrayList.add(entry.getKey());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(cip);
            }
        });

        TextView deletegroup = findViewById(R.id.deletegroup);
        final Intent i2 = new Intent(this, groups.class);
        deletegroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Users").child(add_user).child("groups").child(gn).removeValue();
                Toast.makeText(contacts_in_group.this, "Group deleted successfully!", Toast.LENGTH_LONG).show();
                startActivity(i2);
            }
        });

        final Intent i3 = new Intent(this, delete_members.class);
        TextView deletemembers = findViewById(R.id.deletemembers);
        deletemembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i3.putExtra("group_name", gn);
                startActivity(i3);
            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent logout = new Intent(this, groups.class);
        mAuth = FirebaseAuth.getInstance();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(logout);
        }
        return super.onKeyDown(keyCode, event);
    }
}
