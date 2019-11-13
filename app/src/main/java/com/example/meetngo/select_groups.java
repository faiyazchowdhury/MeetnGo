package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class select_groups extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> selected_groups = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<Object> members = new ArrayList<>();
    private ArrayList<Object> selected_members = new ArrayList<>();
    public int timeRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_groups);
        Intent duration_intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String email = mAuth.getCurrentUser().getEmail().toString();
        final String add_user = returnUsername(email);
        listView = findViewById(R.id.select_groups);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        mDatabase.child("Users").child(add_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> groups = (HashMap<String, Object>) dataSnapshot.child("groups").getValue();
                timeRemaining = ((Long) dataSnapshot.child("duration").getValue()).intValue();
                for(Map.Entry<String,Object> entry : groups.entrySet()){
                    arrayList.add(entry.getKey());
                    members.add(entry.getValue());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_groups.add(arrayList.get(i));
                selected_members.add(members.get(i));
            }
        });

        Button done = findViewById(R.id.done);
        final Intent i1 = new Intent(this, status_page.class);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selected_groups.isEmpty()) {
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Notifications").hasChildren()) {
                                HashMap<String, Object> notifs = (HashMap<String, Object>) dataSnapshot.child("Notifications").getValue();
                                int flag = 1;
                                for(Map.Entry<String, Object> entry : notifs.entrySet()){
                                    try {
                                        HashMap<String, Object> n = (HashMap<String, Object>) entry.getValue();
                                        if (n.get("sender").equals(add_user)) {

                                            int duration = ((Long) dataSnapshot.child("Users").child(add_user).child("duration").getValue()).intValue();
                                            int distance = ((Long) dataSnapshot.child("Users").child(add_user).child("distance").getValue()).intValue();
                                            String message = (String) dataSnapshot.child("Users").child(add_user).child("message").getValue();
                                            mDatabase.child("Notifications").child(entry.getKey()).child("sender").setValue(add_user);

                                            mDatabase.child("Notifications").child(entry.getKey()).child("distance").setValue(distance);
                                            mDatabase.child("Notifications").child(entry.getKey()).child("duration").setValue(duration);
                                            mDatabase.child("Notifications").child(entry.getKey()).child("message").setValue(message);

                                            mDatabase.child("Notifications").child("receivers").removeValue();
                                            for (int j = 0; j < selected_groups.size(); j++) {
                                                mDatabase.child("Notifications").child(entry.getKey()).child("receivers").setValue(selected_members.get(j));
                                            }
                                            flag = 0;
                                        }
                                    }
                                    catch (Exception e){
                                        Log.i("Exception", e.getMessage());
                                    }

                                }
                                if(flag == 1) {
                                    int i = ((Long) dataSnapshot.child("Notifications").child("num").getValue()).intValue();
                                    int duration = ((Long) dataSnapshot.child("Users").child(add_user).child("duration").getValue()).intValue();
                                    int distance = ((Long) dataSnapshot.child("Users").child(add_user).child("distance").getValue()).intValue();
                                    String message = (String) dataSnapshot.child("Users").child(add_user).child("message").getValue();
                                    mDatabase.child("Notifications").child("num").setValue(i + 1);
                                    mDatabase.child("Notifications").child("n" + (i + 1)).child("sender").setValue(add_user);

                                    mDatabase.child("Notifications").child("n" + (i + 1)).child("distance").setValue(distance);
                                    mDatabase.child("Notifications").child("n" + (i + 1)).child("duration").setValue(duration);
                                    mDatabase.child("Notifications").child("n" + (i + 1)).child("message").setValue(message);

                                    for (int j = 0; j < selected_groups.size(); j++) {
                                        mDatabase.child("Notifications").child("n" + (i + 1)).child("receivers").setValue(selected_members.get(j));
                                    }
                                }
                            } else {
                                int i = 1;
                                int duration = ((Long) dataSnapshot.child("Users").child(add_user).child("duration").getValue()).intValue();
                                int distance = ((Long) dataSnapshot.child("Users").child(add_user).child("distance").getValue()).intValue();
                                String message = (String) dataSnapshot.child("Users").child(add_user).child("message").getValue();
                                mDatabase.child("Notifications").child("num").setValue(i);
                                mDatabase.child("Notifications").child("n" + i).child("sender").setValue(add_user);

                                mDatabase.child("Notifications").child("n" + i).child("distance").setValue(distance);
                                mDatabase.child("Notifications").child("n" + i).child("duration").setValue(duration);
                                mDatabase.child("Notifications").child("n" + i).child("message").setValue(message);

                                for (int j = 0; j < selected_groups.size(); j++) {
                                    mDatabase.child("Notifications").child("n" + i).child("receivers").setValue(selected_members.get(j));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mDatabase.child("Users").child(add_user).child("freeness").setValue(1);
                    i1.putExtra("timeRemaining", timeRemaining);
                    startActivity(i1);
                }
                else{
                    Toast.makeText(select_groups.this, "Please select atleast 1 group!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }
}
