package com.example.firebasex;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        startActivity(new Intent(this, MainActivity.class).putExtra("key",
                remoteMessage.getData().get("key")));
    }

}
