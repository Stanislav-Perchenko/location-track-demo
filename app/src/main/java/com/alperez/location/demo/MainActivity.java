package com.alperez.location.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alperez.location.demo.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

/**
 * Created by stanislav.perchenko on 10.08.2020 at 16:42.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView vDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.layout_drawer);
        vDrawer = findViewById(R.id.left_drawer);

        vDrawer.setNavigationItemSelectedListener(menuItem -> {
            final int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_item_settings:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
                case R.id.menu_item_location_test:
                    //TODO Implement Location test activity
                    //startActivity(new Intent(this, LocationTestActivity.class));
                    break;
                case R.id.menu_item_logout:
                    finish();
                    break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        setupToolbar();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    private void setupToolbar() {
        final int textColor = getColor(R.color.text_white);
        Toolbar vTb = findViewById(R.id.toolbar);
        vTb.setTitleTextColor(textColor);
        setSupportActionBar(vTb);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.screen_title_main);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.getDrawerArrowDrawable().setSpinEnabled(false);
        mDrawerToggle.getDrawerArrowDrawable().setColor(textColor);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
