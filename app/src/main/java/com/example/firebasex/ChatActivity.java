package com.example.firebasex;

import android.Manifest;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;

import java.util.ArrayList;

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
            Manifest.permission.WRITE_CONTACTS},1);
        }
    }

    private void loadContacts() {
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , null, null, null);
        }
        if (cursor != null) {
            while (cursor.moveToNext()){
                contacts.add(new Contact(cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)), cursor.getString(cursor.
                        getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
            }
        }
    }

    public void initRecyclerView(){
        contactsList = findViewById(R.id.contactList);
        contactsList.setHasFixedSize(true);
        contactsList.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        contactsList.setLayoutManager(layoutManager);
        adapter = new ContactsListAdapter(this, contacts);
        contactsList.setAdapter(adapter);
    }
}
