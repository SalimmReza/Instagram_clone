
package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagram.Fragments.Home_Fragment;
import com.example.instagram.Fragments.Notification_Fragment;
import com.example.instagram.Fragments.Profile_Fragment;
import com.example.instagram.Fragments.Search_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bottomNavigationView=findViewById(R.id.bottom_navigation_id);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

                switch (menuitem.getItemId())
                {
                    case R.id.nav_home:
                        selectorFragment=new Home_Fragment();
                                break;

                    case R.id.nav_add:
                        selectorFragment=null;
                        //add will intent to a new activity
                        Intent intent = new Intent(MainActivity2.this, PostActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.nav_love:
                        selectorFragment=new Notification_Fragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment=new Search_Fragment();
                        break;

                    case R.id.nav_people:
                        selectorFragment=new Profile_Fragment();
                        break;

                }
                if (selectorFragment!=null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_id, selectorFragment).commit();
                }

                return true;

            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null)
        {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId" ,profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_id, new Profile_Fragment()).commit();

            bottomNavigationView.setSelectedItemId(R.id.bottom_navigation_id);
        }else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_id,new Home_Fragment()).commit();
        }
    }
}
