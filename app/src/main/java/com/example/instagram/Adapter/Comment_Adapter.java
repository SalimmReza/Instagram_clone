package com.example.instagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.MainActivity2;
import com.example.instagram.Model.Comment_c;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ViewHolder>{

    private Context m_context;
    private List<Comment_c> m_comment;


    ////////////////

    String postId;

    private FirebaseUser f_user;

    public Comment_Adapter(Context m_context, List<Comment_c> m_comment, String postId) {
        this.m_context = m_context;
        this.m_comment = m_comment;
        this.postId= postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(m_context).inflate(R.layout.cmnt_item, parent, false);
        return new Comment_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        f_user= FirebaseAuth.getInstance().getCurrentUser();
        Comment_c comment = m_comment.get(position);
        holder.comment_c.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        User user = datasnapshot.getValue(User.class);
                        holder.user_name.setText(user.getUsername());
                        if (user.getImageurl().equals("default")){
                            holder.image_profile.setImageResource(R.mipmap.ic_launcher_round);

                        }else
                        {
                            Picasso.get().load(user.getImageurl()).into(holder.image_profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.comment_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(m_context, MainActivity2.class);
                intent.putExtra("publisherId", comment.getPublisher());
                m_context.startActivity(intent);

            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_context, MainActivity2.class);
                intent.putExtra("publisherId", comment.getPublisher());
                m_context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getPublisher().endsWith(f_user.getUid()))
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(m_context).create();
                    alertDialog.setTitle("Do you want to delete this comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseDatabase.getInstance().getReference().child("Comment")
                                    .child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(m_context, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }


                                }
                            });
                        }
                    });
                    alertDialog.show();
                }

               // return ;
            }


        });

    }

    @Override
    public int getItemCount() {
        return m_comment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        public TextView user_name, comment_c;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image_profile=itemView.findViewById(R.id.c_i_image_profile);
            user_name=itemView.findViewById(R.id.c_i_username_id);
            comment_c=itemView.findViewById(R.id.c_i_comment_id);

        }
    }


}
