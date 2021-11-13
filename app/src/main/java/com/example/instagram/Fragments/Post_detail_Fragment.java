package com.example.instagram.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapter.Post_Adapter;
import com.example.instagram.Model.POst;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Post_detail_Fragment extends Fragment {

    private String postId;
    private RecyclerView recycler_View;
    private Post_Adapter post_adapter;
    private List<POst> post_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post_detail_, container, false);


        postId=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                .getString("postId" , "none" );

        recycler_View= view.findViewById(R.id.pd_recycler_view_id);
        recycler_View.setHasFixedSize(true);
        recycler_View.setLayoutManager(new LinearLayoutManager(getContext()));

        post_list = new ArrayList<>();
        post_adapter = new Post_Adapter(getContext(), post_list);
        recycler_View.setAdapter(post_adapter);

        FirebaseDatabase.getInstance().getReference().child("Post")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                post_list.clear();
                post_list.add(datasnapshot.getValue(POst.class));

                post_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}