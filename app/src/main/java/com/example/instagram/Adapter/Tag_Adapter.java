package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;

import java.util.List;

public class Tag_Adapter extends RecyclerView.Adapter<Tag_Adapter.ViewHolder>{

    private Context m_context;
    private List<String> m_Tags;
    private List<String> m_TagsCount;

    public Tag_Adapter(Context m_context, List<String> m_Tags, List<String> m_TagsCount) {
        this.m_context = m_context;
        this.m_Tags = m_Tags;
        this.m_TagsCount = m_TagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(m_context).inflate(R.layout.tag_item,parent, false);
        return new Tag_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tag.setText("#" + m_Tags.get(position));
        holder.noOfPosts.setText(m_TagsCount.get(position)+ " Posts");

    }

    @Override
    public int getItemCount() {
       return m_Tags.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tag, noOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag= itemView.findViewById(R.id.hash_tag_id);
            noOfPosts= itemView.findViewById(R.id.no_of_post_id);

        }
    }

    public void filter (List<String> filterTags, List<String> filterTagsCount){
        this.m_Tags = filterTags;
        this.m_TagsCount = filterTagsCount;

        notifyDataSetChanged();

    }

}
