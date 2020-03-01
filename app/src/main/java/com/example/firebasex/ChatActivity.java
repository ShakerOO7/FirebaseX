package com.example.firebasex;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Contact> contacts = new ArrayList<>();
    RecyclerView contactsList;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getPermissions();
        loadContacts();
        initRecyclerView();

    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS}, 1);
        }
    }

    private void loadContacts() {
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , null, null, null);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.NUMBER));
                phone = phone.replace(" ", "");
                phone = phone.replace("-", "");
                phone = phone.replace("(", "");
                phone = phone.replace(")", "");
                try {
                    if (String.valueOf(phone.charAt(0)).equals("0")
                            && String.valueOf(phone.charAt(1)).equals("0")) {
                        phone = "+" + phone.substring(2);
                    }
                    if (!String.valueOf(phone.charAt(0)).equals("+")) {
                        phone = getCountryISO() + phone;
                    }
                    addContact(name, phone);
                    //contacts.add(new Contact(name, phone));
                } catch (IndexOutOfBoundsException e) {

                }

            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void addContact(String name, String phone) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query query = ref.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try{
                        contacts.add(new Contact(name, phone));
                    }catch (Exception e){

                    }
                    /*String phoneI = "", nameI = "";
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.child("phone").getValue() != null) {
                            phoneI = child.child("phone").getValue().toString();
                        }
                        if (child.child("name").getValue() != null) {
                            nameI = child.child("name").getValue().toString();
                        }
                    }
                    contacts.add(new Contact(nameI.equals("") ? name : nameI,
                            phoneI.equals("") ? phone : phoneI));*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryISO() {
        String iso = "+963";
        getApplicationContext();
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (tm != null && tm.getNetworkCountryIso() != null) {
            if (!tm.getNetworkCountryIso().equals("")) {
                iso = tm.getNetworkCountryIso();
            }
        }
        return CountryToPhonePrefix.getPhone(iso);
    }


    public void initRecyclerView() {
        contactsList = findViewById(R.id.contactList);
        contactsList.setHasFixedSize(true);
        contactsList.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        contactsList.setLayoutManager(layoutManager);
        adapter = new ContactsListAdapter(this, contacts);
        contactsList.setAdapter(adapter);
    }
}
