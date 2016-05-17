package com.qoe.lengji.qoeclient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar = null;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchToFragment(type);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int temp = type;
        switch (id) {
            case R.id.nav_movie:
                type = 0;
                break;
            case R.id.nav_episode:
                type = 1;
                break;
            case R.id.nav_music:
                type = 2;
                break;
            case R.id.nav_cartoon:
                type = 3;
                break;
            case R.id.nav_sport:
                type = 4;
                break;
            case R.id.nav_entertainment:
                type = 5;
                break;
            case R.id.nav_others:
                type = 6;
                break;
//            case R.id.nav_feedback:
//                break;
            default:
                type = 0;
                break;
        }
        if (temp != type) {
            switchToFragment(type);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void switchToFragment(int type) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment = new ItemFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_layout, fragment).commit();
        toolbar.setTitle(Video.getTypeString(type));
    }

}
