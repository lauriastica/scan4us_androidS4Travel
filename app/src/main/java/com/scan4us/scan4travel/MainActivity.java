package com.scan4us.scan4travel;

import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.roughike.bottombar.OnTabClickListener;
import com.scan4us.scan4travel.fragments.DashboardFragment;
import com.scan4us.scan4travel.fragments.HeartFragment;
import com.scan4us.scan4travel.fragments.MapFragment;
import com.scan4us.scan4travel.fragments.ScanFragment;

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

        BottomBarTab bottomBarTabDashboard = new BottomBarTab(R.drawable.ic_home_black_24dp, "Dashboard");
        BottomBarTab bottomBarTabScan = new BottomBarTab(R.drawable.ic_touch_app_black_24dp, "Scan");
        BottomBarTab bottomBarTabHeart = new BottomBarTab(R.drawable.ic_favorite_black_24dp, "Heart Rate");
        BottomBarTab bottomBarTabMap = new BottomBarTab(R.drawable.ic_room_black_24dp, "Map");

        mBottomBar.setItems(bottomBarTabDashboard,bottomBarTabScan,bottomBarTabHeart,bottomBarTabMap);

        mBottomBar.setOnTabClickListener(new OnTabClickListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    dashboardFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, dashboardFragment).commit();
                } else if (position == 2) {
                    HeartFragment heartFragment = new HeartFragment();
                    heartFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, heartFragment).commit();
                } else if (position == 1) {
                    ScanFragment scanFragment = new ScanFragment();
                    scanFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, scanFragment).commit();
                } else if (position == 3) {
                    MapFragment mapFragment = new MapFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, mapFragment).commit();
                }
            }

            @Override
            public void onTabReSelected(int position) {

            }
        });

        /*mBottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabClickListener() {
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
                } else if (menuItemId == R.id.nav_scan) {
                    ScanFragment scanFragment = new ScanFragment();
                    scanFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, scanFragment).commit();
                } else if (menuItemId == R.id.nav_map) {
                    MapFragment mapFragment = new MapFragment();
                    mapFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, mapFragment).commit();
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_dashboard) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });*/

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        /*mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorPrimary));*/

        //mBottomBar.setTextAppearance(R.style.CodeFont);
        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        //mBottomBar.setActiveTabColor(R.color.colorSecondary);

        //mBottomBar.useDarkTheme(true);

        // mBottomBar.selectTabAtPosition(1, true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

}
