package com.example.instagram.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragments.Post_detail_Fragment;
import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.Model.POst;
import com.example.instagram.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Photo_Adapter extends RecyclerView.Adapter<Photo_Adapter.Viewholder> {

    private Context m_context;
    private List<POst> m_posts;

    public Photo_Adapter(Context m_context, List<POst> m_posts) {
        this.m_context = m_context;
        this.m_posts = m_posts;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(m_context).inflate(R.layout.photoo_item, parent, false);
        return new Photo_Adapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

       /* POst pOst = m_posts.get(position);
        Picasso.get().load(pOst.getImageurl())
                .placeholder(R.mipmap.ic_launcher_round).into(holder.post_image);*/

        final POst post = m_posts.get(position);
        Glide.with(m_context).load(post.getImageurl())/*.placeholder(R.mipmap.ic_launcher_round)*/.override(300,300).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 m_context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                         .edit().putString("postId" , post.getPostId()).apply();

                ((FragmentActivity)m_context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_id, new Post_detail_Fragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return m_posts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

            public ImageView post_image;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            post_image=itemView.findViewById(R.id.photo_post_image_id);
        }
    }
}
