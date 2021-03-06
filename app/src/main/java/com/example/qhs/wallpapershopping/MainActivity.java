package com.example.qhs.wallpapershopping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.qhs.wallpapershopping.Fragments.Dialog;
import com.example.qhs.wallpapershopping.Fragments.Fragment_Shopping;
import com.example.qhs.wallpapershopping.Fragments.Fragment_about;
import com.example.qhs.wallpapershopping.Fragments.Fragment_favorite;
import com.example.qhs.wallpapershopping.Fragments.Fragment_home;
import com.example.qhs.wallpapershopping.Fragments.Fragment_login;
import com.example.qhs.wallpapershopping.Fragments.Fragment_search;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity<navigation> extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //Decleration
    private AuthHelper mAuthHelper;
    private Menu mOptionsMenu;
    private ImageButton profileBtn;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragment;
    boolean doubleBackToExitPressedOnce = false;
    //anim
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.example.qhs.wallpapershopping.key.currentPosition";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Splash Screen
        if (Splashscreen.Splash == 0) {
            Intent intent = new Intent(this, Splashscreen.class);
            startActivity(intent);
            super.onCreate(savedInstanceState);
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        //End Splash
//        //anim
//        if (savedInstanceState != null) {
//            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
//            // Return here to prevent adding additional GridFragments when changing orientation.
//            return;
//        }
//        //end anim

//        Window window = getWindow();
//        WindowManager.LayoutParams winParams = window.getAttributes();
//        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        window.setAttributes(winParams);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);



        //Full screen ui
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                                  WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        //End Full screen ui
        //Hide navigation bar
        //   View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
//        int uiOptions =  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        decorView.setSystemUiVisibility(uiOptions);
        //End Hide navigation bar
        //animation
        //setAnimation();

        fragment = new Fragment_home();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, fragment).addToBackStack("fragment_home").commit();


        toolbar = findViewById(R.id.toolbar);

        //toolbar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));

        setSupportActionBar(toolbar);


        //Profile
        mAuthHelper = AuthHelper.getInstance(this);

        profileBtn = findViewById(R.id.ProfileBtn);
        profileBtn.setOnClickListener(view -> {
            fragment = new Fragment_login();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, fragment).addToBackStack("fragment_login").commit();
        });

        if (mAuthHelper.isLoggedIn()) {
            Log.d("USERNAME: ", "isloggedin");
            profileBtn.setVisibility(View.GONE);
        }

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    //anim shared element trans
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    //end anim shared element trans
    @SuppressLint("Range")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        FragmentManager fm = getSupportFragmentManager();
        //View layout = this.findViewById(R.id.constraintLayout);
        switch (menuItem.getItemId()) {
            case R.id.menu_shopping_cart:
                if (mAuthHelper.isLoggedIn()) {
                    //getFragmentManager().popBackStackImmediate();
                    fragment = new Fragment_Shopping();

                    fm.beginTransaction().replace(R.id.frame, fragment, "fragment_shopping").addToBackStack("fragment_shopping").commit();
                } else {
                    Log.d("SHOPPING", fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName()+"+"+fm.getBackStackEntryCount());
                    if (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName() == "fragment_dialog"){
                        fm.popBackStackImmediate();
                    }
                    TextView title = findViewById(R.id.txtTitle);
                    title.setText("عضویت");
                    toolbar.setNavigationIcon(null);
                    Blur blur = new Blur();
                    Bitmap map = Blur.takeScreenShot(this);
                    Bitmap fast = blur.fastblur(map, 10);
                    fragment = new Dialog();
                    ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                    BitmapDrawable ob = new BitmapDrawable(getResources(), fast);
                    constraintLayout.setBackground(ob);
                    //constraintLayout.setBackground(this.getDrawable(R.drawable.back4dialog));
                    fm.beginTransaction().replace(R.id.frame, fragment, "fragment_dialog").addToBackStack("fragment_dialog").commit();
                }
                break;
            case R.id.menu_search:

                //getFragmentManager().popBackStackImmediate();
                fragment = new Fragment_search();
                fm.beginTransaction().replace(R.id.frame, fragment, "fragment_search").addToBackStack("fragment_search").commit();
                break;

            case R.id.menu_home:
                //getFragmentManager().popBackStackImmediate();
                fragment = new Fragment_home();
                fm.beginTransaction().replace(R.id.frame, fragment, "fragment_home").addToBackStack("fragment_home").commit();
                break;

            case R.id.menu_favorite:
                if (mAuthHelper.isLoggedIn()) {
                    //getFragmentManager().popBackStackImmediate();
                    fragment = new Fragment_favorite();
                    fm.beginTransaction().replace(R.id.frame, fragment, "fragment_favorite").addToBackStack("fragment_favorite").commit();
                } else {
                    Log.d("FAVORITE", fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName()+"+"+fm.getBackStackEntryCount());
                    if (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName() == "fragment_dialog"){
                        fm.popBackStackImmediate();
                    }
                    TextView title = findViewById(R.id.txtTitle);
                    title.setText("عضویت");
                    toolbar.setNavigationIcon(null);
                    Blur blur = new Blur();
                    Bitmap map = Blur.takeScreenShot(this);
                    Bitmap fast = blur.fastblur(map, 10);
                    fragment = new Dialog();
                    ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                    BitmapDrawable ob = new BitmapDrawable(getResources(), fast);
                    constraintLayout.setBackground(ob);
                    //constraintLayout.setBackground(this.getDrawable(R.drawable.back4dialog));
                    fm.beginTransaction().replace(R.id.frame, fragment, "fragment_dialog").addToBackStack("fragment_dialog").commit();
                }
                break;

            case R.id.menu_call:
                //getFragmentManager().popBackStackImmediate();
                fragment = new Fragment_about();
                fm.beginTransaction().replace(R.id.frame, fragment, "fragment_call").addToBackStack("fragment_call").commit();
                break;

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            mAuthHelper.clear();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            profileBtn.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.signout_menu, menu);
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(mOptionsMenu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.action_signout);

        if (mAuthHelper.isLoggedIn()) {
            register.setVisible(true);
        } else {
            register.setVisible(false);
        }
        return true;
    }

    private void updateOptionsMenu() {
        if (mOptionsMenu != null) {
            onPrepareOptionsMenu(mOptionsMenu);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        updateOptionsMenu();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        if (!getFragmentManager().popBackStackImmediate()) {
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStackImmediate();

                String tag = fm.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                //Log.d("TAAG", tag);
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    String name = fm.getBackStackEntryAt(i).getName();
                    Log.d("name", name);

                }

                int index = 0;
                switch (tag) {
                    case "fragment_home":


                        index = 0;
                        fm.popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        //fm.beginTransaction().replace(R.id.frame, new Fragment_home(), "fragment_home").addToBackStack("fragment_home").commit();
                        break;
                    case "fragment_shopping":
                        index = 1;
                        break;
                    case "fragment_search":
                        index = 2;
                        break;
                    case "fragment_favorite":
                        index = 3;
                        break;
                    case "fragment_call":
                        index = 4;
                        break;


                }
                navigation.getMenu().getItem(index).setChecked(true);
            } else {
                if (doubleBackToExitPressedOnce) {
                    //super.onBackPressed();
                    //finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                //super.onBackPressed();
                //navigation.getMenu().getItem(0).setChecked(true);
            }

        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}