package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageView icon_image;
    LinearLayout linear_layout;
    Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // icon_image = findViewById(R.id.icon_image_id);
        //linear_layout = findViewById(R.id.linear_layout_id);
        login = findViewById(R.id.log_in_id);
        register = findViewById(R.id.register_id);

//        linear_layout.animate().alpha(0f).setDuration(1);

        /*TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());*/
//        icon_image.setAnimation(animation);
    }

    public void register(View view) {

        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void Login(View view) {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private class MyAnimationListener implements Animation.AnimationListener{


        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon_image.clearAnimation();
            icon_image.setVisibility(View.INVISIBLE);
            linear_layout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this, MainActivity2.class));
            finish();
        }
    }
}