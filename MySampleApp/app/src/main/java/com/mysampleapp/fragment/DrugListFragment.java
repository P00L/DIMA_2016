package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.mysampleapp.DetailsTransition;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.DrugAdapter;
import com.mysampleapp.adapter.ItemClickListenerAnimation;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DrugDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DrugListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DrugListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugListFragment extends Fragment implements ItemClickListenerAnimation {

    private final static String LOG_TAG = DrugListFragment.class.getSimpleName();
    private static final String ARG_DRUGLIST = "param1";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private DrugAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<DrugDO> items;
    private String[] drugnames;
    private AppCompatActivity activity;
    private ProgressDialog mProgressDialog;
    private DemoNoSQLOperation operation;
    private TextView noDataTextView;
    private FloatingActionButton fab;
    private Animation rotate_open;
    private ProgressBar mProgress;


    public DrugListFragment() {
        // Required empty public constructor
    }

    public static DrugListFragment newInstance() {
        DrugListFragment fragment = new DrugListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            items = getArguments().getParcelableArrayList(ARG_DRUGLIST);
        }
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drug_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        activity = (AppCompatActivity) getActivity();
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Drug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "ASD");
        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        rotate_open = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_360);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        if (!fab.isShown()) {
            fab.show();
        }

        fab.setImageResource(R.drawable.ic_action_plus);

        //fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

        //hide fab on scroll down show on scroll up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                    //fab.animate().translationY(fab.getHeight() + 32).setInterpolator(new AccelerateInterpolator(2)).start();
                } else if (dy < 0)
                    fab.show();
                //fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        noDataTextView = (TextView) view.findViewById(R.id.no_data);
        if (items == null) {
            new MyAsyncTask(this).execute();
        } else {
            if (items.size() > 0) {
                Log.w(LOG_TAG, "restore items");
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(items, (new Comparator<DrugDO>() {
                    @Override
                    public int compare(DrugDO s1, DrugDO s2) {
                        return s1.getName().compareTo(s2.getName());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new DrugAdapter(getContext(), items, this);
                mRecyclerView.setAdapter(mAdapter);
                enableFab();
            } else {
                Log.w(LOG_TAG, "restore no data string");
                enableFab();
                enableEmptyState("Click \"+\" \n to insert a drug");

            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Fragment fragment = DrugFormFragment.newInstance(new DrugDO(), false, items);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        activity.getSupportActionBar().setTitle(R.string.drugs);

        // set nav menu item checked
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_drug);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_hamburger);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        Boolean success = false;
        DrugListFragment listClass;
        Boolean successQuery = false;

        public MyAsyncTask(DrugListFragment listFragment) {
            listClass = listFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            disableEmptyState();
            disableFab();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                successQuery = operation.executeOperation();
                items = ((DemoNoSQLTableDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
                success = true;
            } catch (final AmazonClientException ex) {
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgress.setVisibility(View.GONE);
            if (success) {
                Log.w(LOG_TAG, "SUCCESS " + items.size() + "");
                if (successQuery) {
                    Log.w(LOG_TAG, "SUCCESS " + "SUCCESS");
                    enableFab();
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    Collections.sort(items, (new Comparator<DrugDO>() {
                        @Override
                        public int compare(DrugDO s1, DrugDO s2) {
                            return s1.getName().compareTo(s2.getName());   //or whatever your sorting algorithm
                        }
                    }));
                    mAdapter = new DrugAdapter(getContext(), items, listClass);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Log.w(LOG_TAG, "SUCCESS " + "FAIL");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a drug");
                }

            } else {
                if (successQuery) {
                    Log.w(LOG_TAG, "FAIL " + "SUCCESS");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a drug");
                } else {
                    Log.w(LOG_TAG, "FAIL " + "FAIL");
                    Snackbar snackbar = Snackbar
                            .make(fab, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new MyAsyncTask(listClass).execute();
                                }
                            });
                    snackbar.show();
                    disableFab();
                    enableEmptyState("No data available \n check your connection");
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelableArrayList(ARG_DRUGLIST, items);
    }

    //handle on click to perform animation
    @Override
    public void onClick(ImageView imageView, int position, boolean isLongClick) {

        fab.startAnimation(rotate_open);

        //see github project to more detail
        Fragment drugFragment = DrugFragment.newInstance(items.get(position));

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)
        //qui pendo potremo giocare con le animazioni come vogliamo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drugFragment.setSharedElementEnterTransition(new DetailsTransition());
            drugFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            drugFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, "kittenImage")
                .replace(R.id.content_frame, drugFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(activity.getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //mAdapter.filter(newText);
                if(mAdapter != null) {
                    mAdapter.getFilter().filter(newText);
                    return true;
                }
                else
                    return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void enableFab() {
        Log.w(LOG_TAG, "enable fab");
        fab.setEnabled(true);
        fab.setAlpha(1f);

    }

    private void disableFab() {
        Log.w(LOG_TAG, "disable fab");
        fab.setEnabled(false);
        fab.setAlpha(0.2f);
    }

    private void enableEmptyState(String text) {
        noDataTextView.setText(text);
        noDataTextView.setVisibility(View.VISIBLE);
    }

    private void disableEmptyState() {
        noDataTextView.setVisibility(View.GONE);
    }

}
