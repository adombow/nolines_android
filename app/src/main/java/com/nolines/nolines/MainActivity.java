package com.nolines.nolines;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nolines.nolines.adapters.HomeCardAdapter;
import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.dummy.DummyContent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RideFragment.OnListFragmentInteractionListener,
        HomeFragment.OnListFragmentInteractionListener,
        TicketFragment.OnListFragmentInteractionListener,
        MapsFragment.OnFragmentInteractionListener{

    public static final String RIDE_ID_ARGUMENT = "com.nolines.nolines.RIDE_ID_ARGUMENT";

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = HomeFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void setupActionBarDrawerToggle(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if(id == R.id.nav_home){
            fragmentClass = HomeFragment.class;
        }
        else if (id == R.id.nav_AR) {
            Intent intent = new Intent(this, ARActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_Map) {
            fragmentClass = MapsFragment.class;
        } else if (id == R.id.nav_Rides) {
            fragmentClass = RideFragment.class;
        } else if (id == R.id.nav_Tickets){
            fragmentClass = TicketFragment.class;
        } else if (id == R.id.nav_About) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if(fragmentClass != null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onListFragmentInteraction(Ride ride){}

    @Override
    public void onHomeCardClicked(int viewType){
        try {
            switch(viewType){

                /*
                public final int WELCOME_CARD = 0;
                public final int MAP_CARD = 1;
                public final int RESERVATION_CARD = 2;
                public final int SERVICE_TEST_CARD = 3;
                public final int SHOP_CARD = 4;
                */

                case 0:
                    replaceFragmentWithAnimation(TicketFragment.class.newInstance(), "TicketFragment");
                    break;
                case 1:
                    replaceFragmentWithAnimation(MapsFragment.class.newInstance(), "MapFragment");
                    break;

                case 2:
                    replaceFragmentWithAnimation(RideFragment.class.newInstance(), "RideFragment");
                    break;

            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void onListFragmentInteraction(Ticket ticket){
        //TODO: Open map view with this ticket selected
        Fragment fragment = null;
        try {
            fragment = (Fragment) MapsFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment != null){
            Bundle args = new Bundle();
            args.putInt(RIDE_ID_ARGUMENT, ticket.getRide().getId());
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, fragment).
                    addToBackStack(null).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){}

    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_right, R.animator.exit_to_left, R.animator.enter_from_left, R.animator.exit_to_right);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
