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

public class createaccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
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
                final String s_email = email.getText().toString();
                final String s_pass = pass.getText().toString();
                if(!s_email.isEmpty() && !s_pass.isEmpty()){
                    create(s_email, s_pass);
                }
                else{
                    Toast.makeText(createaccount.this, "Either email or password filed is blank!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void create(String email, String password){
        final Intent login = new Intent(this, login.class);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(createaccount.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(login);
                }
                else{
                    Toast.makeText(createaccount.this, "User creation failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
