package com.example.instagram.Adapter;

import android.app.Notification;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.Post_detail_Fragment;
import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.Model.N_Notification;
import com.example.instagram.Model.POst;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.ViewHolder> {

    private Context m_context;
    private List<N_Notification> m_notification;

    public Notification_Adapter(Context m_context, List<N_Notification> m_notification) {
        this.m_context = m_context;
        this.m_notification = m_notification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(m_context).inflate(R.layout.notification_item, parent, false);

        return new Notification_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final N_Notification n_notification = m_notification.get(position);

        get_user(holder.image_profile, holder.user_name, n_notification.getUser_id());
        holder.comment.setText(n_notification.getText());

        if (n_notification.isIs_post() ){
            holder.post_image.setVisibility(View.VISIBLE);
            get_post_image(holder.post_image, n_notification.getPostId());
        }else
        {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (n_notification.isIs_post())
                {
                    m_context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postId" , n_notification.getPostId()).apply();

                    ((FragmentActivity)m_context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container_id, new Post_detail_Fragment())
                            .commit();
                }else
                {
                    m_context.getSharedPreferences("PROFILE" , m_context.MODE_PRIVATE)
                            .edit().putString("profileId" , n_notification.getUser_id()).apply();

                    ((FragmentActivity)m_context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container_id, new Profile_Fragment())
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_notification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView user_name, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile= itemView.findViewById(R.id.n_i_image_profile_id);
            post_image= itemView.findViewById(R.id.n_i_post_image_id);
            user_name= itemView.findViewById(R.id.n_i_user_name_id);
            comment= itemView.findViewById(R.id.n_i_comment_id);

        }
    }


    private void get_post_image(ImageView imageView ,String postId){

        FirebaseDatabase.getInstance().getReference().child("Post")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                POst pOst = datasnapshot.getValue(POst.class);
                Picasso.get().load(pOst.getImageurl()).placeholder(R.mipmap.ic_launcher_round).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_user(ImageView imageView , TextView textView,String userId)
    {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                User user = datasnapshot.getValue(User.class);
                if (user.getImageurl().equals("default"))
                {
                    imageView.setImageResource(R.mipmap.ic_launcher_round);
                }else
                {
                    Picasso.get().load(user.getImageurl()).into(imageView);
                }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
