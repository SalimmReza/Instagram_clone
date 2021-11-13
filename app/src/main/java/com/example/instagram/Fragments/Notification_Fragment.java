package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapter.Notification_Adapter;
import com.example.instagram.Model.N_Notification;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Notification_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private Notification_Adapter notification_adapter;
    private List<N_Notification> notification_List;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.n_recycler_view_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notification_List = new ArrayList<>();
        notification_adapter = new Notification_Adapter(getContext(), notification_List);
        recyclerView.setAdapter(notification_adapter);
        
        read_notification();

        return  view;
    }

    private void read_notification() {

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                        for (DataSnapshot snapshot : datasnapshot.getChildren())
                        {
                            notification_List.add(snapshot.getValue(N_Notification.class));
                        }

                        Collections.reverse(notification_List);
                        notification_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}