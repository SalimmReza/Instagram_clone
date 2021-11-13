package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    private String imageUrl;
    private ImageView close, image_added;
    private TextView post;
    SocialAutoCompleteTextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close= findViewById(R.id.close_id);
        post= findViewById(R.id.post_id);
        image_added= findViewById(R.id.image_added_id);
        description= findViewById(R.id.description_id);

        //to take images from gallery
        CropImage.activity().start(PostActivity.this);

    }

    //to take images from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri =result.getUri();
            //to add image

            image_added.setImageURI(imageUri);
        }else
        {
            Toast.makeText(PostActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostActivity.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }
    }

    public void close(View view) {
        Intent intent = new Intent(PostActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }


    public void post(View view) {
        upload();
    }

    private void upload() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null)
        {
            StorageReference filepath = FirebaseStorage.getInstance().getReference("Post").child(System.currentTimeMillis()+"."+ getFileExtension(imageUri));

            StorageTask uploadtask = filepath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri>task) {

                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Post");
                    String postId = ref.push().getKey();

                    HashMap<String , Object> map = new HashMap<>();
                    map.put("postId" ,postId);
                    map.put("imageurl", imageUrl);
                    map.put("description" ,description.getText().toString());
                    map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags= description.getHashtags();
                    if (!hashTags.isEmpty())
                    {
                        for (String tag: hashTags)
                        {
                            map.clear();

                            map.put("tag" , tag.toLowerCase());
                            map.put("postId" , postId);

                            mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);


                        }
                    }
                        pd.dismiss();
                    Intent intent = new Intent(PostActivity.this , MainActivity2.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else 
        {
            Toast.makeText(PostActivity.this, "No Image was selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }


    @Override
    protected void onStart() {
        super.onStart();
        ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getBaseContext());
        FirebaseDatabase.getInstance().getReference().child("HashTags")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                        for(DataSnapshot snapshot : datasnapshot.getChildren())
                        {
                            hashtagAdapter.add(new Hashtag(snapshot.getKey(),(int) snapshot.getChildrenCount()));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        description.setHashtagAdapter(hashtagAdapter);


    }
}