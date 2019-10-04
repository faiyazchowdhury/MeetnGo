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
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText email = findViewById(R.id.email);
                final String s_email = email.getText().toString();
                if(!s_email.isEmpty()){
                    resetpassword(s_email);
                }
                else{
                    Toast.makeText(forgotpassword.this, "Email field is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetpassword(String email){
        mAuth = FirebaseAuth.getInstance();
        final Intent login = new Intent(this, login.class);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgotpassword.this, "Password reset email sent to registered email id!", Toast.LENGTH_SHORT).show();
                    startActivity(login);
                }
                else{
                    Toast.makeText(forgotpassword.this, "user not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
