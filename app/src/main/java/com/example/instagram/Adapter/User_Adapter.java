package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.MainActivity2;
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


import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Adapter extends RecyclerView.Adapter<User_Adapter.ViewHolder>{

        private Context m_Context;
        private List<User> m_User;
        private boolean is_Fragment;

    private FirebaseUser firebaseUser;

    public User_Adapter(Context mContext, List<User> mUser, boolean isFragment) {
        this.m_Context = mContext;
        this.m_User = mUser;
        this.is_Fragment = isFragment;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(m_Context).inflate(R.layout.user_item, parent, false);
        return new User_Adapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        User user =m_User.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);


        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
        isFollowed(user.getId(), holder.btnFollow);

        if (user.getId().equals(firebaseUser.getUid()))
        {
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnFollow.getText().toString().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getId())
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(firebaseUser.getUid())
                            .setValue(true);

                    add_notification(user.getId());
                }else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getId())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(firebaseUser.getUid())
                            .removeValue();
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_Fragment)
                {
                    m_Context.getSharedPreferences("PROFILE" , Context.MODE_PRIVATE).edit()
                            .putString("profileId", user.getId()).apply();
                    ((FragmentActivity)m_Context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_id, new Profile_Fragment()).commit();
                }else
                {
                    Intent intent = new Intent(m_Context, MainActivity2.class);
                    intent.putExtra("publisherId" , user.getId());
                    m_Context.startActivity(intent);
                }
            }
        });

    }

    private void isFollowed(String id, Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(id).exists())
                    btnFollow.setText("following");
                else
                    btnFollow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return m_User.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView userName, fullName;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile= itemView.findViewById(R.id.i_profile_image_id);
            userName= itemView.findViewById(R.id.i_user_name_id);
            fullName= itemView.findViewById(R.id.i_full_name_id);
            btnFollow= itemView.findViewById(R.id.i_btn_follow_id);



        }
    }

   private void add_notification(String userId)
   {
       HashMap<String, Object> map = new HashMap<>();

       map.put("user_id" , userId);
       map.put("text", "Is following you!");
       map.put("postId" , "");
       map.put("is_post" , false);

       FirebaseDatabase.getInstance().getReference().child("Notifications")
               .child(firebaseUser.getUid()).push().setValue(map);
   }
}
