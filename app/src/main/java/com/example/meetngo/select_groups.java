package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_groups);
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
                mDatabase.child("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            int i = ((Long) dataSnapshot.child("num").getValue()).intValue();
                            mDatabase.child("Notifications").child("num").setValue(i+1);
                            mDatabase.child("Notifications").child("n"+(i+1)).child("sender").setValue(add_user);
                            Date currentTime = Calendar.getInstance().getTime();
                            mDatabase.child("Notifications").child("n"+(i+1)).child("time").setValue(currentTime);
                            for(int j=0;j<selected_groups.size();j++){
                                mDatabase.child("Notifications").child("n"+(i+1)).child("groups").child(selected_groups.get(j)).setValue(selected_members.get(j));
                            }
                        }
                        else{
                            int i = 1;
                            mDatabase.child("Notifications").child("num").setValue(i);
                            mDatabase.child("Notifications").child("n"+i).child("sender").setValue(add_user);
                            Date currentTime = Calendar.getInstance().getTime();
                            mDatabase.child("Notifications").child("n"+i).child("time").setValue(currentTime);
                            for(int j=0;j<selected_groups.size();j++){
                                mDatabase.child("Notifications").child("n"+i).child("groups").child(selected_groups.get(j)).setValue(selected_members.get(j));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                startActivity(i1);

            }
        });
    }
    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }
}
