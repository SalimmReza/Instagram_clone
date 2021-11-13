package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.Fragments.Home_Fragment;
import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditrofileActivity extends AppCompatActivity {

    private ImageView closee;
    private CircleImageView image_profile;
    private TextView savee , change_photo , edit_profile;
    private MaterialEditText full_name, user_name, bio;

    private FirebaseUser f_user;

    private Uri m_image_uri;
    private StorageReference storageReference;
    private StorageTask upload_task;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editrofile);

        closee=findViewById(R.id.e_close_id);
        image_profile=findViewById(R.id.e_image_profile_id);
        savee=findViewById(R.id.e_save_id);
        change_photo=findViewById(R.id.e_change_photo_id);
        edit_profile=findViewById(R.id.e_edit_profile_id);
        full_name=findViewById(R.id.e_fullname_id);
        user_name=findViewById(R.id.e_username_id);
        bio=findViewById(R.id.e_bio_id);

        f_user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference  = FirebaseStorage.getInstance().getReference().child("Uploads");


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(f_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user = datasnapshot.getValue(User.class);
                full_name.setText(user.getName());
                user_name.setText(user.getUsername());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        closee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditrofileActivity.this);

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditrofileActivity.this);

            }
        });

        savee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_profile();
                Toast.makeText(EditrofileActivity.this, "Profile Updated..", Toast.LENGTH_LONG).show();
            }

        });

    }
    private void update_profile (){
        HashMap<String,Object> map= new HashMap<>();
        map.put("name",full_name.getText().toString());
        map.put("username",user_name.getText().toString());
        map.put("bio", bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(f_user.getUid()).updateChildren(map);
    }

    private void upload_image (){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (m_image_uri != null)
        {
            StorageReference fileref = storageReference.child(System.currentTimeMillis() + ".jpeg");

            upload_task= fileref.putFile(m_image_uri);
            upload_task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", "" + url);
                        FirebaseDatabase.getInstance().getReference("Users").child(f_user.getUid()).updateChildren(hashMap);

                        progressDialog.dismiss();
                    }else
                    {
                        Toast.makeText(EditrofileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else
        {
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            m_image_uri= result.getUri();

            upload_image();

        }else
        {
            Toast.makeText(this, "Something went wrong ! Try again!" , Toast.LENGTH_LONG).show();
        }

    }
}