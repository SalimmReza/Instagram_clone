package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instagram.Adapter.Post_Adapter;
import com.example.instagram.Model.POst;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Home_Fragment extends Fragment {

    private RecyclerView recyclerView_post;
    private Post_Adapter post_adapter;
    private List<POst> pOstList;

    private List<String> foll0wing_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);

       recyclerView_post=view.findViewById(R.id.h_recycler_view_posts_id);
        recyclerView_post.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView_post.setLayoutManager(linearLayoutManager);
        pOstList= new ArrayList<>();
        post_adapter = new Post_Adapter(getContext(), pOstList);
        recyclerView_post.setAdapter(post_adapter);

        foll0wing_list = new ArrayList<>();

        check_following_users();

        return  view;


    }
     private void check_following_users() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
                .getCurrentUser()
        .getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                foll0wing_list.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    foll0wing_list.add(snapshot.getKey());
                }


                foll0wing_list.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                read_post();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void read_post() {

        FirebaseDatabase.getInstance().getReference().child("Post")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        pOstList.clear();

                        for (DataSnapshot snapshot : datasnapshot.getChildren()){
                           POst pOst = snapshot.getValue(POst.class);

                           for (String id: foll0wing_list){
                               if (pOst.getPublisher().equals(id)){
                                   pOstList.add(pOst);

                               }
                           }
                        }

                        post_adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}