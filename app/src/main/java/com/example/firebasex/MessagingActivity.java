package com.example.firebasex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.rpc.BadRequest;

import java.util.ArrayList;
import java.util.HashSet;

public class MessagingActivity extends AppCompatActivity {

    String chatKey;
    ArrayList<String> messages = new ArrayList<>();
    RecyclerView messagesList;
    RecyclerView.Adapter adapter;
    LinearLayoutManager layoutManager;
    EditText et;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initRecyclerView();
        new Thread(this::getChat).start();

        et = findViewById(R.id.message);
        send = findViewById(R.id.send);
        send.setOnClickListener(e -> {
            if(et.getText().toString() != ""){
                if(et.getText().toString() != ""){
                    addMessage(et.getText().toString());
                    et.setText("");
                }
            }
        });

    }

    public void getChat() {
        chatKey = getIntent().getStringExtra("key");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats").child(chatKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    messages.add((String)child.getValue());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addMessage(String message){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats").child(chatKey);
        ref.push().setValue(message);
        messages.add(message);
        adapter.notifyDataSetChanged();
    }

    public void initRecyclerView() {
        messagesList = findViewById(R.id.messagesList);
        messagesList.setHasFixedSize(true);
        messagesList.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        messagesList.setLayoutManager(layoutManager);
        adapter = new MessagesListAdapter(this, messages);
        messagesList.setAdapter(adapter);
    }
}
