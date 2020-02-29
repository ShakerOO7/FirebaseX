package com.example.firebasex;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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
            this.context.startActivity(new Intent(this.context, Messaging.class));
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
