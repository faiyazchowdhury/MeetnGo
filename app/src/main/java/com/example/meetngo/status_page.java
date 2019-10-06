package com.example.meetngo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class status_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_page);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent logout = new Intent(this, freeness.class);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(logout);
        }
        return super.onKeyDown(keyCode, event);
    }
}
