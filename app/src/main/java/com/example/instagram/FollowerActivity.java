package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.instagram.Adapter.User_Adapter;
import com.example.instagram.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowerActivity  extends AppCompatActivity {

    private String id;
    private String title;
    private List<String> id_list;

    private RecyclerView recyclerView;
    private User_Adapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.f_toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.f_recycler_view_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new User_Adapter(this, userList, false);
        recyclerView.setAdapter(userAdapter);
        id_list = new ArrayList<>();
        switch (title) {
            case "likes":
                get_Reacts();
                break;

            case "followings":
                get_Following();
                break;

            case "followers":
                get_Followers();
                break;

            case "views":
                get_Views();
                break;
        }

    }

    private void get_Views() {
        FirebaseDatabase.getInstance().getReference("Story").child(id).child(getIntent().getStringExtra("storyid"))
                .child("views").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_list.add(snapshot.getKey());
                }
                show_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void get_Reacts() {
        FirebaseDatabase.getInstance().getReference("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_list.add(snapshot.getKey());
                }
                show_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void get_Following() {
        FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_list.add(snapshot.getKey());
                }
                show_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void get_Followers() {
        FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    id_list.add(snapshot.getKey());
                }
                show_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void show_users() {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.example.instagram.Model.User user = snapshot.getValue(com.example.instagram.Model.User.class);
                    for (String id : id_list) {
                        assert user != null;
                        if (user.getId().equals(id))
                            userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
