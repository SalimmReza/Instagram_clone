
package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapter.Tag_Adapter;
import com.example.instagram.Adapter.User_Adapter;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Search_Fragment extends Fragment {

    private RecyclerView recycler_View;

    private List<User> m_User;
    private User_Adapter user_Adapter;
    //hashtags
    private RecyclerView recyclerView_Tags;
    private List<String> m_HashTags;
    private List<String> m_HashTagsCount;
    private Tag_Adapter tag_Adapter;

    private AppCompatAutoCompleteTextView searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recycler_View = view.findViewById(R.id.s_recycler_view_users_id);
        recycler_View.setHasFixedSize(true);
        recycler_View.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView_Tags = view.findViewById(R.id.s_recycler_view_tags_id);
        recyclerView_Tags.setHasFixedSize(true);
        recyclerView_Tags.setLayoutManager(new LinearLayoutManager(getContext()));


        m_HashTags = new ArrayList<>();
        m_HashTagsCount = new ArrayList<>();
        tag_Adapter = new Tag_Adapter(getContext(), m_HashTags, m_HashTagsCount);
        recyclerView_Tags.setAdapter(tag_Adapter);

        m_User = new ArrayList<>();
        user_Adapter = new User_Adapter(getContext(), m_User, true);
        recycler_View.setAdapter(user_Adapter);

        searchBar = view.findViewById(R.id.search_bar_id);

        readUsers();

        readTags();


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                fiter(s.toString());

            }
        });

        return view;
    }

    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                m_HashTags.clear();
                m_HashTagsCount.clear();

                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    m_HashTags.add(snapshot.getKey());
                    m_HashTagsCount.add(snapshot.getChildrenCount() + "");


                }

                tag_Adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (TextUtils.isEmpty(searchBar.getText().toString())) {
                    m_User.clear();
                    for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        m_User.add(user);
                    }
                    user_Adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                m_User.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    m_User.add(user);
                }
                user_Adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fiter(String text)
    {
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for (String s : m_HashTags)
        {
            if (s.toLowerCase().contains(text.toLowerCase()))
            {
                mSearchTags.add(s);
                mSearchTagsCount.add(m_HashTagsCount.get(m_HashTags.indexOf(s)));
            }
        }
        tag_Adapter.filter(mSearchTags,mSearchTagsCount);

    }
}
