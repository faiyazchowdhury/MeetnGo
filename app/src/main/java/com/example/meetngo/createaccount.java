package com.example.meetngo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class createaccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_createaccount);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText email = findViewById(R.id.email);
                final EditText pass = findViewById(R.id.password);
                final EditText phone = findViewById(R.id.phone);
                final String s_email = email.getText().toString();
                final String s_pass = pass.getText().toString();
                final String phn = phone.getText().toString();
                if(!s_email.isEmpty() && !s_pass.isEmpty() && !phn.isEmpty()){
                    create(s_email, s_pass, phn);
                }
                else{
                    Toast.makeText(createaccount.this, "Either email or password field is blank!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void create(final String email, String password, final String phone){
        final Intent login = new Intent(this, login.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(createaccount.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                    final String add_user = returnUsername(email);
                    mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = new User(email, phone, -1, "",-1, -1, -1, -1);
                            mDatabase.child("Users").child(add_user).setValue(u);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    startActivity(login);
                }
                else{
                    Toast.makeText(createaccount.this, "User creation failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

}
