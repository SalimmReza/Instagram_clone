package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private  EditText user_name, name, email, password;
    private  Button register;
    private TextView already;
    private DatabaseReference m_RootRef;
    private FirebaseAuth mAuth;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name=findViewById(R.id.username_id);
        name=findViewById(R.id.name_id);
        email=findViewById(R.id.email_id);
        password=findViewById(R.id.pasword_id);
        register=findViewById(R.id.r_register_id);
        already=findViewById(R.id.alreadu_a_user_id);

        m_RootRef = FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);


    }

    public void AlreadyUser(View view) {

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View view) {

        String tusername= user_name.getText().toString();
        String tname= name.getText().toString();
        String temail= email.getText().toString();
        String tpassword= password.getText().toString();


        if (temail.isEmpty()){
            email.setError("Enter Email address");
            email.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(temail).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return;
        }
        if (tpassword.isEmpty()){
            password.setError("Enter you pasword");
            password.requestFocus();
            return;
        }
        if (tpassword.length()<6){
            password.setError("Your password should be more than 6 digits/characters");
            password.requestFocus();
            return;
        }
        if (tusername.isEmpty()){
            user_name.setError("Enter you UserName");
            user_name.requestFocus();
            return;
        }
        if (tname.isEmpty()){
            name.setError("Enter you Name");
            name.requestFocus();
            return; 
        }else
        {
            registerUser(tusername,tname,temail,tpassword);
        }

    }

    private void registerUser(String user_name, String name, String email, String password) {
            pd.setMessage("Loading...");
            pd.show();
        mAuth.createUserWithEmailAndPassword(email, password)
    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {

            HashMap<String,Object>map= new HashMap<>();
            map.put("name",name);
            map.put("username",user_name);
            map.put("emial",email);
            map.put("password",password);
            map.put("bio","");
            map.put("imageurl","default");
            map.put("id",mAuth.getCurrentUser().getUid());

            m_RootRef.child("Users").child(mAuth.getCurrentUser().getUid())
                    .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Update your profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity2.class);
                        startActivity(intent);
                        finish();

                    }

                }
            });

        }
    }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}