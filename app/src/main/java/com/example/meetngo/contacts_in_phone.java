package com.example.meetngo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class contacts_in_phone extends AppCompatActivity {

    public class Android_Contact {
        //----------------< fritzbox_Contacts() >----------------
        public String android_contact_Name = "";
        public String android_contact_TelefonNr = "";
        public int android_contact_ID=0;
//----------------</ fritzbox_Contacts() >----------------
    }

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> number = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String gn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_in_phone);
        Intent cip = getIntent();
        gn = cip.getStringExtra("group_name");
        TextView g_name = findViewById(R.id.group_name);
        g_name.setText(gn);
        fp_get_Android_Contacts();

    }

    public void fp_get_Android_Contacts() {
//----------------< fp_get_Android_Contacts() >----------------
        ArrayList<Android_Contact> arrayList_Android_Contacts = new ArrayList<Android_Contact>();

//--< get all Contacts >--
        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }
//--</ get all Contacts >--

//----< Check: hasContacts >----
        if (cursor_Android_Contacts.getCount() > 0) {
//----< has Contacts >----
//----< @Loop: all Contacts >----
            while (cursor_Android_Contacts.moveToNext()) {
//< init >
                Android_Contact android_contact = new Android_Contact();
                String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String contact_display_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//</ init >

//----< set >----
                android_contact.android_contact_Name = contact_display_name;


//----< get phone number >----
                int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//< set >
                        android_contact.android_contact_TelefonNr = phoneNumber;
//</ set >
                    }
                    phoneCursor.close();
                }
//----</ set >----
//----</ get phone number >----

// Add the contact to the ArrayList
                arrayList_Android_Contacts.add(android_contact);
            }
//----</ @Loop: all Contacts >----

//< show results >
            ListView ac = findViewById(R.id.contactsinphone);

            for(int i=0;i<arrayList_Android_Contacts.size();i++){
                name.add(arrayList_Android_Contacts.get(i).android_contact_Name);
                number.add(arrayList_Android_Contacts.get(i).android_contact_TelefonNr);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
            ac.setAdapter(adapter);
//</ show results >

            final ArrayList<String> selected_name = new ArrayList<>();
            final ArrayList<String> selected_number = new ArrayList<>();
            ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selected_name.add(name.get(i));
                    selected_number.add(sanitizeNumber(number.get(i)));
                }
            });

            mAuth = FirebaseAuth.getInstance();
            String email = mAuth.getCurrentUser().getEmail().toString();
            final String add_user = returnUsername(email);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            final Intent i1 = new Intent(this, contacts_in_group.class);
            i1.putExtra("group_name", gn);
            Button confirm = findViewById(R.id.next);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0;i<selected_name.size();i++) {
                        if(!selected_number.get(i).isEmpty()) {
                            mDatabase.child("Users").child(add_user).child("groups").child(gn).child(selected_name.get(i)).setValue(selected_number.get(i));
                        }
                    }
                    startActivity(i1);
                }
            });
        }
//----</ Check: hasContacts >----

// ----------------</ fp_get_Android_Contacts() >----------------
    }

    private String sanitizeNumber(String number){
        String s_number = number.replaceAll("[\\+ \\-]", "");
        return s_number;
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

}

