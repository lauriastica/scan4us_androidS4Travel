package com.scan4us.scan4travel;

import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.scan4us.scan4travel.fragments.DashboardFragment;
import com.scan4us.scan4travel.fragments.HeartFragment;

public class MainActivity extends AppCompatActivity {

    private BottomBar mBottomBar;
    private FrameLayout frame;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frame = (FrameLayout) findViewById(R.id.frame);

        bundle = new Bundle();
        bundle.putString("Dashboard", "Dashboard Fragment");
        bundle.putString("Scan", "Scan Fragment");
        bundle.putString("Heart", "Heart Rate Fragment");
        bundle.putString("Maps", "Map Fragment");

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noNavBarGoodness();

        mBottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_dashboard) {
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    dashboardFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, dashboardFragment).commit();
                } else if (menuItemId == R.id.nav_heart) {
                    HeartFragment heartFragment = new HeartFragment();
                    heartFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, heartFragment).commit();
                } /*else if (menuItemId == R.id.nav_gallery) {
                    GalleryFragment galleryFragment = new GalleryFragment();
                    galleryFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, galleryFragment).commit();
                }*/
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_dashboard) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
          mBottomBar.setActiveTabColor("#009688");

        // mBottomBar.selectTabAtPosition(1, true);

    }
}
