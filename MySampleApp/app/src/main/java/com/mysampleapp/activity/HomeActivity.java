package com.mysampleapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.util.ThreadUtils;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLOperationListItem;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DynamoDBUtils;
import com.mysampleapp.demo.nosql.NoSQLShowResultsDemoFragment;
import com.mysampleapp.fragment.DocFormFragment;
import com.mysampleapp.fragment.DocListFragment;
import com.mysampleapp.fragment.DrugFormFragment;
import com.mysampleapp.fragment.DrugFragment;
import com.mysampleapp.fragment.DrugListFragment;
import com.mysampleapp.R;
import com.mysampleapp.demo.DemoConfiguration;
import com.mysampleapp.demo.nosql.NoSQLSelectTableDemoFragment;
import com.mysampleapp.fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DrugListFragment.OnFragmentInteractionListener,
        DrugFormFragment.OnFragmentInteractionListener,
        DocFormFragment.OnFragmentInteractionListener,
        DrugFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener {

    /** The NoSQL Table demo operations will be run against. */
    private DemoNoSQLTableBase demoTable;

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

        Intent intent = getIntent();
        String message = intent.getStringExtra(SplashActivity.FRAGMENT_MESSAGE);
        Fragment fragment;
        AppCompatActivity activity = this;
        demoTable = DemoNoSQLTableFactory.instance(getApplicationContext())
                .getNoSQLTableByTableName("Doctor");

        if (savedInstanceState == null) {
            // se e' la prima volta che apriamo l'activity creaiamo il tutto come nuovo in base a cosa si vuole aprire
            switch (message) {
                case "fragment_home":
                    fragment = HomeFragment.newInstance();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    navigationView.setCheckedItem(R.id.nav_gallery);
                    break;
            }
        } else {
            // se si arriva da una rotazione dello schermo.... allora non facciamo nulla e riprende la vecchia roba dal back stack
        }
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

        switch (id) {
            case R.id.doc_menu:

                final DemoNoSQLOperation operation = (DemoNoSQLOperation)demoTable.getOperationByName(getApplicationContext(),"ASD");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean foundResults = false;
                        try {
                            foundResults = operation.executeOperation();
                        } catch (final AmazonClientException ex) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("ASD",
                                            String.format("Failed executing selected DynamoDB table (%s) operation (%s) : %s",
                                                    demoTable.getTableName(), operation.getTitle(), ex.getMessage()), ex);
                                }
                            });
                            return;
                        }

                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (operation.isScan()) {
                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
                                    dialogBuilder.setTitle(R.string.nosql_dialog_title_scan_warning_text);
                                    dialogBuilder.setMessage(R.string.nosql_dialog_message_scan_warning_text);
                                    dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                    dialogBuilder.show();
                                }
                            }
                        });

                        if (!foundResults) {
                            handleNoResultsFound();
                        } else {
                            showResultsForOperation(operation);
                        }
                    }
                }).start();
                break;
            case R.id.drug_menu:
                fragment = DrugListFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case R.id.nav_camera:
                final DemoConfiguration.DemoItem demo_item = new DemoConfiguration.DemoItem(R.string.main_fragment_title_nosql_database, R.mipmap.database,
                        R.string.feature_nosql_database_demo_button, NoSQLSelectTableDemoFragment.class);
                fragment = Fragment.instantiate(this, demo_item.fragmentClassName);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment, demo_item.fragmentClassName)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case R.id.nav_gallery:
                fragment = HomeFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentInteraction(Uri uri) {}


    private void handleNoResultsFound() {
        //TODO far partire fragment doctor con no data found
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                dialogBuilder.setTitle(R.string.nosql_dialog_title_no_results_text);
                dialogBuilder.setMessage(R.string.nosql_dialog_message_no_results_text);
                dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                dialogBuilder.show();
            }
        });
    }

    private void showResultsForOperation(final DemoNoSQLOperation operation) {
        // On execution complete, open the NoSQLShowResultsDemoFragment.
        final DocListFragment resultsDemoFragment = new DocListFragment();
        resultsDemoFragment.setOperation(operation);

        final FragmentActivity fragmentActivity = this;

        if (fragmentActivity != null) {
            fragmentActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, resultsDemoFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}
