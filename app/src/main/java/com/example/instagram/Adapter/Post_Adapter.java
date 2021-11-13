package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.CommmentActivity;
import com.example.instagram.FollowerActivity;
import com.example.instagram.Fragments.Post_detail_Fragment;
import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.Model.POst;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.ViewHolde>{

        private Context m_context;
        private List<POst> m_pOst;

        private FirebaseUser firebaseUser;

    public Post_Adapter(Context m_context, List<POst> m_pOst) {
        this.m_context = m_context;
        this.m_pOst = m_pOst;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(m_context).inflate(R.layout.post_item, parent , false);
        return new Post_Adapter.ViewHolde(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolde holder, int position) {



        POst pOst = m_pOst.get(position);
        Picasso.get().load(pOst.getImageurl()).into(holder.postImage);
        holder.descriptionn.setText(pOst.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(pOst.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                User user = datasnapshot.getValue(User.class);

                if (user.getImageurl().equals("default"))
                {
                    holder.image_profile.setImageResource(R.mipmap.ic_launcher);
                }else
                {
                    Picasso.get().load(user.getImageurl()).into(holder.image_profile);
                }
                holder.user_name.setText(user.getUsername());
                holder.author.setText(user.getName());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        is_liked(pOst.getPostId(), holder.likes);
        no_of_likes(pOst.getPostId(), holder.mo_of_likes);
        no_of_comments(pOst.getPostId(), holder.no_of_comments );
        is_saved(pOst.getPostId(), holder.savee);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likes.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(pOst.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);

                   add_notifications(pOst.getPostId(), pOst.getPublisher());

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(pOst.getPostId()).child(firebaseUser.getUid())
                            .removeValue();
                }

            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_context, CommmentActivity.class);
                intent.putExtra("postId", pOst.getPostId());
                intent.putExtra("author_id", pOst.getPublisher());
                m_context.startActivity(intent);

            }
        });

        holder.no_of_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_context, CommmentActivity.class);
                intent.putExtra("postId", pOst.getPostId());
                intent.putExtra("author_id", pOst.getPublisher());
                m_context.startActivity(intent);


            }
        });


        holder.savee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.savee.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("bookmark")
                            .child(firebaseUser.getUid()).child(pOst.getPostId())
                            .setValue(true);

                }else{
                    FirebaseDatabase.getInstance().getReference().child("bookmark")
                            .child(firebaseUser.getUid()).child(pOst.getPostId())
                            .removeValue();
                }
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
                        .putString("profileId" , pOst.getPublisher()).apply();

                //PROFILE

                ((FragmentActivity)m_context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_id, new Profile_Fragment()).commit();
            }
        });

        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
                        .putString("profileId" , pOst.getPublisher()).apply();

                ((FragmentActivity)m_context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_id, new Profile_Fragment()).commit();
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_context.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit()
                        .putString("profileId" , pOst.getPublisher()).apply();

                ((FragmentActivity)m_context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_id, new Profile_Fragment()).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("postId" , pOst.getPostId()).apply();

                ((FragmentActivity)m_context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_id, new Post_detail_Fragment()).commit();
            }
        });

        holder.mo_of_likes.setOnClickListener(new View.OnClickListener() {
            @Override

                public void onClick(View v) {
                    Intent intent = new Intent (m_context , FollowerActivity.class);
                    intent.putExtra("id", pOst.getPublisher());
                    intent.putExtra("title", "likes");
                    m_context.startActivity(intent);
                }
        });
    }



    @Override
    public int getItemCount() {
        return m_pOst.size();
    }

    public class ViewHolde extends RecyclerView.ViewHolder {

       public ImageView image_profile, postImage, likes, comments, savee, moree;
       public TextView user_name, mo_of_likes, author, no_of_comments;
       SocialTextView descriptionn;

        public ViewHolde(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.p_profile_image_id);
            postImage = itemView.findViewById(R.id.p_post_image_id);
            likes = itemView.findViewById(R.id.p_like_id);
            comments = itemView.findViewById(R.id.p_comment_id);
            savee = itemView.findViewById(R.id.p_save_id);
            moree = itemView.findViewById(R.id.p_more_id);
            user_name = itemView.findViewById(R.id.p_username);
            mo_of_likes = itemView.findViewById(R.id.p_no_of_likes_id);
            author = itemView.findViewById(R.id.p_author_id);
            no_of_comments = itemView.findViewById(R.id.p_no_of_comments_id);
            descriptionn = itemView.findViewById(R.id.p_post_description_id);


        }
    }

    private void is_saved(final String postId, final ImageView savee) {

        FirebaseDatabase.getInstance().getReference().child("bookmark")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                        if (datasnapshot.child(postId).exists())
                        {
                            savee.setImageResource(R.drawable.ic_save_black);
                            savee.setTag("saved");
                        }else
                        {
                            savee.setImageResource(R.drawable.ic_save);
                            savee.setTag("Save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void is_liked(String postId, ImageView image_View){
        FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(firebaseUser.getUid()).exists())
                {
                    image_View.setImageResource(R.drawable.redheart);
                    image_View.setTag("Liked");

                }else
                {
                    image_View.setImageResource(R.drawable.heart);
                    image_View.setTag("Like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void no_of_likes (String postId, TextView textt)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                textt.setText(datasnapshot.getChildrenCount() + " Likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void no_of_comments(String postId , TextView textt)
    {
        FirebaseDatabase.getInstance().getReference().child("Comment")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                textt.setText("View All " + datasnapshot.getChildrenCount()+ " Comments" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void add_notifications(String postId , String publisherId)
    {

        HashMap<String, Object>map = new HashMap<>();

        map.put("user_id" , publisherId);
        map.put("text", "Liked your post..");
        map.put("postId" , postId);
        map.put("is_post" , true);

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(firebaseUser.getUid()).push().setValue(map);
    }
}
