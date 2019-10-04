package com.example.meetngo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class freeness extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        final String w_email = mAuth.getCurrentUser().getEmail().toString();
        setContentView(R.layout.activity_freeness);
        final Intent i4 = new Intent(this, groups.class);
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i4);
            }
        });
        TextView w_email1 = findViewById(R.id.w_email);
        w_email1.setText(w_email);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent logout = new Intent(this, login.class);
        mAuth = FirebaseAuth.getInstance();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            startActivity(logout);
        }
        return super.onKeyDown(keyCode, event);
    }

}
