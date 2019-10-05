package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        final Intent i3 = new Intent(this, forgotpassword.class);
        final Intent i4 = new Intent(this, createaccount.class);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText email = findViewById(R.id.email);
                final EditText pass = findViewById(R.id.password);
                final String s_email = email.getText().toString();
                final String s_pass = pass.getText().toString();
                if(!s_email.isEmpty() && !s_pass.isEmpty()){
                    login1(s_email, s_pass);
                }
                else{
                    Toast.makeText(login.this, "Either email or password field is blank!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView fp = findViewById(R.id.fp);
        fp.setClickable(true);
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i3);
            }
        });

        TextView ca = findViewById(R.id.ca);
        ca.setClickable(true);
        ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i4);
            }
        });


    }

    protected void login1(final String email, String password){
        final Intent loginsuccess = new Intent(this, freeness.class);
        final Intent settings = new Intent(this, com.example.meetngo.settings.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(login.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                    final String add_user = returnUsername(email);
                    mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.child(add_user).child("settings").hasChild("notifications")){
                                Toast.makeText(login.this, "Please update your settings!", Toast.LENGTH_LONG).show();
                                startActivity(settings);
                            }
                            else{
                                startActivity(loginsuccess);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    Toast.makeText(login.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

}
