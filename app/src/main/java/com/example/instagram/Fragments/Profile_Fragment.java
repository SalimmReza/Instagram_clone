package com.example.instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.Adapter.Photo_Adapter;
import com.example.instagram.Adapter.Post_Adapter;
import com.example.instagram.EditrofileActivity;
import com.example.instagram.FollowerActivity;
import com.example.instagram.Model.POst;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.internal.ClientStream;

public class Profile_Fragment extends Fragment {

    private RecyclerView recyclerView_save;
    private Photo_Adapter photo_adapter_save;
    private List<POst> my_save_post;

    private RecyclerView recyclerView;
    private Photo_Adapter photo_adapter;
    private List<POst> my_photo_list;

    private CircleImageView image_profile;
    private TextView folllower, folllowing,ppost,full_name,bbio,user_name;

    private ImageView my_pictures, saved_pictures , opption;

    private FirebaseUser f_user;

    String profile_id;

    private Button edit_profile;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        f_user= FirebaseAuth.getInstance().getCurrentUser();
        String data = getContext().getSharedPreferences("PROFILE" , Context.MODE_PRIVATE).getString("profileId" ,"none");
        if (data.equals("none"))
        {
            profile_id=f_user.getUid();

        }else
        {
            profile_id=data;
        }

        profile_id =f_user.getUid();

        image_profile= view.findViewById(R.id.pr_image_profile_id);
        opption= view.findViewById(R.id.pr_options_id);
        folllower= view.findViewById(R.id.pr_followers_id);
        folllowing= view.findViewById(R.id.pr_following_id);
        ppost= view.findViewById(R.id.pr_post_id);
        full_name= view.findViewById(R.id.pr_fullname_id);
        bbio= view.findViewById(R.id.pr_bio_id);
        user_name= view.findViewById(R.id.pr_username_id);
        my_pictures= view.findViewById(R.id.pr_my_pictures_id);
        saved_pictures= view.findViewById(R.id.pr_save_pictures_id);
        edit_profile= view.findViewById(R.id.pr_edit_profile_id);

        recyclerView= view.findViewById(R.id.pr_recycler_save_pictures_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        my_photo_list= new ArrayList<>();
        photo_adapter= new Photo_Adapter(getContext(), my_photo_list);
        recyclerView.setAdapter(photo_adapter);

        recyclerView_save=view.findViewById(R.id.pr_recycler_view_pictures_id);
        recyclerView_save.setHasFixedSize(true);
                recyclerView_save.setLayoutManager(new GridLayoutManager(getContext(), 3));
                my_save_post=new ArrayList<>();
        photo_adapter_save= new Photo_Adapter(getContext() , my_save_post);
                recyclerView_save.setAdapter(photo_adapter_save);


        user_info();
        get_followers_and_following_count();
        get_post_count();
        get_saved_posts();

        //to show photos in profile;
        my_photos();

        if (profile_id.equals(f_user.getUid()))
        {
            edit_profile.setText("Edit Profile");
        }else
        {
            check_following_status();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn_text = edit_profile.getText().toString();
                if (btn_text.equals("Edit Profile"))
                {
                    //go to edit activity
                   /* Intent i =(new Intent(getContext(), EditrofileActivity.class));
                    startActivity(i);*/

                  /*  Intent intent = (new Intent(getContext(), EditrofileActivity.class));
                    startActivity(intent);*/

                    startActivity(new Intent(getContext(), EditrofileActivity.class));


                }else
                {
                    if (btn_text.equals("follow"))
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(f_user.getUid()).child("following")
                                .child(profile_id).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profile_id).child("followers").child(f_user.getUid()).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(f_user.getUid()).child("following")
                                .child(profile_id).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profile_id).child("followers").child(f_user.getUid()).removeValue();

                    }
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_save.setVisibility(View.GONE);

        my_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);
            }
        });

        saved_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_save.setVisibility(View.VISIBLE);
            }
        });

        folllower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), FollowerActivity.class);
                intent.putExtra("id", profile_id);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        folllowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), FollowerActivity.class);
                intent.putExtra("id", profile_id);
                intent.putExtra("title", "followings");
                startActivity(intent);
            }
        });


        return view;

    }

    private void get_saved_posts() {

         final List<String> saved_id = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("bookmark").child(f_user
        .getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for (DataSnapshot snapshot: datasnapshot.getChildren())
                {
                    saved_id.add(snapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Post").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot1) {
                        my_save_post.clear();

                        for (DataSnapshot snapshot1: datasnapshot1.getChildren())
                        {
                            POst pOst = snapshot1.getValue(POst.class);

                            for (String id: saved_id)
                            {
                                if (pOst.getPostId().equals(id))
                                {
                                    my_save_post.add(pOst);
                                }
                            }
                        }
                        photo_adapter_save.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void my_photos() {

        FirebaseDatabase.getInstance().getReference().child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                my_photo_list.clear();

                for (DataSnapshot snapshot: datasnapshot.getChildren())
                {
                    POst pOst = snapshot.getValue(POst.class);
                    if (pOst.getPublisher().equals(profile_id))
                    {
                        my_photo_list.add(pOst);
                    }
                }

                Collections.reverse(my_photo_list);
                photo_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void check_following_status() {

        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(f_user.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if (datasnapshot.child(profile_id).exists())
                {
                    edit_profile.setText("following");
                }else
                {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_post_count() {

        FirebaseDatabase.getInstance().getReference().child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                int counter = 0;
                for (DataSnapshot snapshot : datasnapshot.getChildren())
                {
                    POst pOst = snapshot.getValue(POst.class);

                    if (pOst.getPublisher().equals(profile_id))counter ++;


                }


                ppost.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void get_followers_and_following_count() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profile_id);

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                folllower.setText("" + datasnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                folllowing.setText("" + datasnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void user_info() {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(profile_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                User user = datasnapshot.getValue(User.class);

                Picasso.get().load(user.getImageurl()).into(image_profile);
                user_name.setText(user.getUsername());
                full_name.setText(user.getName());
                bbio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}