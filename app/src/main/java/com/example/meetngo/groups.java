package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class groups extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent duration_intent = getIntent();
        final int timeRemaining = duration_intent.getIntExtra("timeRemaining",1);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Intent cig = new Intent(this, com.example.meetngo.contacts_in_group.class);
        setContentView(R.layout.activity_groups);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView = (ListView) findViewById(R.id.groups);
        listView.setAdapter(arrayAdapter);
        final Intent settings_intent = new Intent(this, settings.class);
        Button settings = findViewById(R.id.settings);
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
                    HashMap<String, Object> groups = (HashMap<String, Object>) dataSnapshot.getValue();
                    for(Map.Entry<String, Object> entry : groups.entrySet()){
                        arrayList.add(entry.getKey());
                    }
                    Collections.sort(arrayList);
                    arrayAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            cig.putExtra("group_name", arrayList.get(i));
                            startActivity(cig);
                        }
                    });
                }
                else{
                    String message = "You don't have any groups yet!";
                    arrayList.add(message);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Intent i1 = new Intent(this, create_group.class);
        Button cng = findViewById(R.id.newgroup);
        cng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i1);
            }
        });

        final Intent status_page_intent = new Intent(this, status_page.class);
        TextView letzChill = findViewById(R.id.letzChill);
        letzChill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(status_page_intent);
            }
        });
        TextView letzChillBorder = findViewById(R.id.letzChillBorder);
        letzChillBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(status_page_intent);
            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }


}
