package com.example.blog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blog.Fragments.FragmentHome;
import com.example.blog.Fragments.FragmentProfile;
import com.example.blog.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar ;
    DrawerLayout drawerLayout ;
    NavigationView navigationView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_tool_bar);
        setSupportActionBar(toolbar);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHome()).commit();


        navigationSetting();
        selectMenuItems();
        addNavigationInfo();

    }

    public void navigationSetting(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.home_navigation);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    public void selectMenuItems(){
       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId()){
                   case R.id.nav_home:
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHome()).commit();
                       drawerLayout.closeDrawers();
                       break;
                   case R.id.nav_profile:
                       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                       fragmentTransaction.replace(R.id.fragment_container,new FragmentProfile());
                       fragmentTransaction.commit();
                       fragmentTransaction.addToBackStack("fragment");
                       drawerLayout.closeDrawers();
                       break;
                   case R.id.nav_log_out:
                       AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               Intent openLoginAct = new Intent(HomeActivity.this, LoginActivity.class);
                               startActivity(openLoginAct);
                               finish();
                           }
                       });

               }
               return true;
           }
       });
    }

    public void addNavigationInfo(){
       View header = navigationView.getHeaderView(0);
        TextView userName = header.findViewById(R.id.nav_header_name);
        TextView userMail = header.findViewById(R.id.nav_header_mail);
        ImageView userImg = header.findViewById(R.id.nav_header_user);

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userImg);

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }
        else{
            super.onBackPressed();
        }
    }
}
