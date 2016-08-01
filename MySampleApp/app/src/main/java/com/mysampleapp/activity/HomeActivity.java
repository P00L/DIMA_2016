package com.mysampleapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mysampleapp.fragment.DocFormFragment;
import com.mysampleapp.fragment.DocFragment;
import com.mysampleapp.fragment.DrugFormFragment;
import com.mysampleapp.fragment.DrugFragment;
import com.mysampleapp.R;
import com.mysampleapp.demo.DemoConfiguration;
import com.mysampleapp.demo.nosql.NoSQLSelectTableDemoFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DocFragment.OnFragmentInteractionListener,
        DrugFragment.OnFragmentInteractionListener,
        DrugFormFragment.OnFragmentInteractionListener,
        DocFormFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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

        android.app.FragmentManager fragmentManager = getFragmentManager();
        AppCompatActivity activity = this;
        Fragment fragment;
        switch (id){
            case R.id.doc_menu:
                Toast.makeText(this, "doc", Toast.LENGTH_LONG).show();
                fragment = new DocFragment();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                activity.getSupportActionBar().setTitle("DOC");
                break;
            case R.id.drug_menu:
                Toast.makeText(this, "drug", Toast.LENGTH_LONG).show();
                    fragment = new DrugFragment();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    activity.getSupportActionBar().setTitle("DRUG");
                break;
            case R.id.nav_camera:
                Toast.makeText(this, "camera", Toast.LENGTH_LONG).show();
                final DemoConfiguration.DemoItem demo_item = new DemoConfiguration.DemoItem(R.string.main_fragment_title_nosql_database, R.mipmap.database,
                        R.string.feature_nosql_database_demo_button, NoSQLSelectTableDemoFragment.class);
                    fragment = Fragment.instantiate(this, demo_item.fragmentClassName);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment, demo_item.fragmentClassName)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    activity.getSupportActionBar().setTitle(demo_item.titleResId);
                break;
            case R.id.nav_gallery:
                Toast.makeText(this, "camera", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, FormActivity.class);
                startActivity(intent);

                break;
            case R.id.nav_manage:
                Toast.makeText(this, "camera", Toast.LENGTH_LONG).show();
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentInteraction(Uri uri){

    }
}
