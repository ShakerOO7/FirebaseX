package com.example.firebasex;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private ArrayList<Contact> contacts;
    private Context context;

    public ContactsListAdapter (Context context, ArrayList<Contact> contacts){
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items
                , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsListAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(contacts.get(position));
        holder.name.setText(contacts.get(position).getName());
        holder.phone.setText(contacts.get(position).getPhone());
        holder.container.setOnClickListener(e -> {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference().child("chats");

            String curUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String targetUId = contacts.get(position).getUId();

            String key = "";

            final String substring = curUId.substring(0, Math.min(12, curUId.length()));
            final String substring1 = targetUId.substring(0, Math.min(12, targetUId.length()));
            if(curUId.compareTo(targetUId) > 0)
                key = substring + substring1;
            else
                key = substring1 + substring;

            db.getReference().child("users").child(curUId).child("chats").child(key).setValue(true);
            db.getReference().child("users").child(targetUId).child("chats").child(key).setValue(true);

            this.context.startActivity(new Intent(this.context, MessagingActivity.class)
                    .putExtra("key",key));
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name, phone;
        ConstraintLayout container;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameContact);
            phone = itemView.findViewById(R.id.phoneContact);
            container = itemView.findViewById(R.id.container);
        }
    }

}
