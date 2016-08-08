package com.mysampleapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.mysampleapp.R;
import com.mysampleapp.demo.DemoConfiguration;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.NoSQLSelectTableDemoFragment;
import com.mysampleapp.fragment.DocFormFragment;
import com.mysampleapp.fragment.DocListFragment;
import com.mysampleapp.fragment.DrugFormFragment;
import com.mysampleapp.fragment.DrugFragment;
import com.mysampleapp.fragment.DrugListFragment;
import com.mysampleapp.fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DrugListFragment.OnFragmentInteractionListener,
        DrugFormFragment.OnFragmentInteractionListener,
        DocFormFragment.OnFragmentInteractionListener,
        DrugFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        View.OnClickListener{

    private Button signOutButton;
    private Button signInButton;
    private IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AWSMobileClient.initializeMobileClientIfNecessary(this);
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

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

        setupSignInButtons();
        updateUserImage();
        updateUserName();


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
                fragment = DocListFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
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

    @Override
    public void onClick(final View view) {
        if (view == signOutButton) {
            // The user is currently signed in with a provider. Sign out of that provider.
            identityManager.signOut();
            // Show the sign-in button and hide the sign-out button.
            signOutButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);

            return;
        }
        if (view == signInButton) {
            // Start the sign-in activity. Do not finish this activity to allow the user to navigate back.
            startActivity(new Intent(this, SignInActivity.class));
            return;
        }

        // ... add any other button handling code here ...

    }

    private void setupSignInButtons() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        signOutButton = (Button) header.findViewById(R.id.button_signout);
        signOutButton.setOnClickListener(this);

        signInButton = (Button) header.findViewById(R.id.button_signin);
        signInButton.setOnClickListener(this);

        final boolean isUserSignedIn = identityManager.isUserSignedIn();
        signOutButton.setVisibility(isUserSignedIn ? View.VISIBLE : View.INVISIBLE);
        signInButton.setVisibility(!isUserSignedIn ? View.VISIBLE : View.INVISIBLE);

    }

    private void updateUserName() {
        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();
        final IdentityProvider identityProvider =
                identityManager.getCurrentIdentityProvider();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        final TextView userNameView = (TextView) header.findViewById(R.id.userName);

        if (identityProvider == null) {
            // Not signed in
            userNameView.setText("NOT SIGNED IN");
            return;
        }

        final String userName =
                identityProvider.getUserName();

        if (userName != null) {
            userNameView.setText(userName);
        }
    }


    private void updateUserImage() {

        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();
        final IdentityProvider identityProvider =
                identityManager.getCurrentIdentityProvider();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        final ImageView imageView =
                (ImageView)header.findViewById(R.id.userImage);

        if (identityProvider == null) {
            // Not signed in
            if (Build.VERSION.SDK_INT < 22) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.user));
            }
            else {
                imageView.setImageDrawable(this.getDrawable(R.mipmap.user));
            }

            return;
        }

        final Bitmap userImage = identityManager.getUserImage();
        if (userImage != null) {
            imageView.setImageBitmap(userImage);
        }
    }

    public void onFragmentInteraction(Uri uri) {}

}
