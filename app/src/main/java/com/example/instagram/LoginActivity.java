package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    private TextView dnthave;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email=findViewById(R.id.login_email_id);
        password=findViewById(R.id.login_pasword_id);
        login=findViewById(R.id.login_id);
        dnthave=findViewById(R.id.dnt_have_account_id);

        mAuth=FirebaseAuth.getInstance();
    }

    public void DnthaveAccount(View view) {

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view) {
        String txt_email=email.getText().toString();
        String txt_password=password.getText().toString();

        if (TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_password))
        {
            Toast.makeText(LoginActivity.this, "Enter valid Email and Password", Toast.LENGTH_LONG).show();

        }else
        {
            loginUser(txt_email,txt_password);
        }


    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Update your profile", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}