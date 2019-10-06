package com.example.meetngo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class create_group extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Button next = findViewById(R.id.next);
        final Intent i1 = new Intent(this, contacts_in_phone.class);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText gn = findViewById(R.id.group_name);
                String g_name = gn.getText().toString();
                if(!g_name.isEmpty()) {
                    i1.putExtra("group_name", g_name);
                    startActivity(i1);
                }
                else{
                    Toast.makeText(create_group.this, "Group name field is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
