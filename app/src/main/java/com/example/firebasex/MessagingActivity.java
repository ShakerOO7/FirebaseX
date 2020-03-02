package com.example.firebasex;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessagingActivity extends AppCompatActivity {

    String chatKey;
    ArrayList<String> messages = new ArrayList<>();
    RecyclerView messagesList;
    RecyclerView.Adapter adapter;
    LinearLayoutManager layoutManager;
    EditText et;
    Button send;
    String myName, targetName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        initRecyclerView();
        new Thread(this::getChat).start();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myName = (String) dataSnapshot.child("First Name").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView tv = findViewById(R.id.target);
        targetName = getIntent().getStringExtra("targetUId");
        ref = FirebaseDatabase.getInstance().getReference("users").child(targetName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv.setText((String) dataSnapshot.child("First Name").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        et = findViewById(R.id.message);
        et.setSelection(0);
        send = findViewById(R.id.send);
        send.setOnClickListener(e -> {
            if (!et.getText().toString().equals("")) {
                addMessage(et.getText().toString());
                et.setText("");
            }
        });

    }

    public void getChat() {
        chatKey = getIntent().getStringExtra("key");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats").child(chatKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    messages.add((String) myName + ": " + child.getValue());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addMessage(String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats").child(chatKey);
        ref.push().setValue(message);
        messagesList.smoothScrollToPosition(messages.size());
        messages.add(myName + ": " + message);
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initRecyclerView() {
        messagesList = findViewById(R.id.messagesList);
        messagesList.setHasFixedSize(true);
        messagesList.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        messagesList.setLayoutManager(layoutManager);
        adapter = new MessagesListAdapter(this, messages);
        messagesList.setAdapter(adapter);
    }
}
