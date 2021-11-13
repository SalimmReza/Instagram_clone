package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.Adapter.Comment_Adapter;
import com.example.instagram.Model.Comment_c;
import com.example.instagram.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommmentActivity extends AppCompatActivity {

    private RecyclerView recycler_view;
    private Comment_Adapter comment_adapter;
    private List<Comment_c> comment_list;


    private CircleImageView image_profile;
    private TextView postt;
    private EditText add_comment;

    private String postId;
    private String author_id;

    FirebaseUser f_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commment);

        Toolbar toolbar =findViewById(R.id.c_toollbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        author_id = intent.getStringExtra("author_id");

        recycler_view=findViewById(R.id.c_recycler_view_id);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));



        comment_list= new ArrayList<>();
        comment_adapter= new Comment_Adapter(this, comment_list, postId);

        recycler_view.setAdapter(comment_adapter);

        add_comment=findViewById(R.id.c_add_comment_id);
        image_profile=findViewById(R.id.c_image_profile_id);
        postt=findViewById(R.id.c_post_id);




        f_user= FirebaseAuth.getInstance().getCurrentUser();

        get_user_image();

        postt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(add_comment.getText().toString()))
                {
                    Toast.makeText(CommmentActivity.this, "Empty comment", Toast.LENGTH_SHORT).show();

                }else
                {
                    put_comment();
                }
            }
        });

        get_comment();

    }

    private void get_comment() {

        FirebaseDatabase.getInstance().getReference().child("Comment")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                comment_list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Comment_c comment_c = snapshot.getValue(Comment_c.class);
                    comment_list.add(comment_c);
                }

                comment_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void put_comment() {

        HashMap<String, Object>Map = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comment").child(postId);

        String id = reference.push().getKey();

        Map.put("id" , id);
        Map.put("comment" , add_comment.getText().toString());
        Map.put("publisher" , f_user.getUid());

        add_comment.setText("");

        reference.child(id).setValue(Map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(CommmentActivity. this, "Comment added", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(CommmentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void get_user_image() {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(f_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user =datasnapshot.getValue(User.class);
                if (user.getImageurl().equals("default"))
                {
                    image_profile.setImageResource(R.mipmap.ic_launcher_round);
                }else
                {
                    Picasso.get().load(user.getImageurl()).into(image_profile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}