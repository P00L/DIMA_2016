package com.mysampleapp.fragment;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.mysampleapp.DetailsTransition;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.DocAdapter;
import com.mysampleapp.adapter.ItemClickListenerAnimation;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDoctor;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DocListFragment extends Fragment implements ItemClickListenerAnimation {

    private static final String ARG_DOCLIST = "param1";
    private final static String LOG_TAG = DocListFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<DoctorDO> items;
    private AppCompatActivity activity;
    private ProgressBar mProgress;
    private DemoNoSQLOperation operation;
    private TextView noDataTextView;
    private FloatingActionButton fab;
    private Animation rotate_open;


    public DocListFragment() {
        // Required empty public constructor
    }

    public static DocListFragment newInstance() {
        DocListFragment fragment = new DocListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            items = getArguments().getParcelableArrayList(ARG_DOCLIST);
        }
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Doctor");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "ASD");
        rotate_open = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_360);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);

        fab = (FloatingActionButton) activity.findViewById(R.id.fab);

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

        //restore
        if (items == null) {
            Log.w(LOG_TAG, "load");
            new MyAsyncTask(this).execute();
        } else {
            if (items.size() > 0) {
                Log.w(LOG_TAG, "restore items");
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(items, (new Comparator<DoctorDO>() {
                    @Override
                    public int compare(DoctorDO s1, DoctorDO s2) {
                        return s1.getName().compareTo(s2.getName());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new DocAdapter(getContext(), items, this);
                mRecyclerView.setAdapter(mAdapter);
                enableFab();
            } else {
                Log.w(LOG_TAG, "restore no data string");
                enableFab();
                enableEmptyState("Click \"+\" \n to insert a doctor");
            }
        }

        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = DocFormFragment.newInstance(new DoctorDO(), false, items);
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }
                }
        );

        // set action bar title
        activity.getSupportActionBar().setTitle(R.string.doctors);

        // set nav menu item checked
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_doc);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_hamburger);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(
                new View.OnClickListener() {
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
                }

        );
    }

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
        Boolean successQuery = false;
        DocListFragment listClass;

        public MyAsyncTask(DocListFragment listFragment) {
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
                items = ((DemoNoSQLTableDoctor.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
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
                    Collections.sort(items, (new Comparator<DoctorDO>() {
                        @Override
                        public int compare(DoctorDO s1, DoctorDO s2) {
                            return s1.getName().compareTo(s2.getName());   //or whatever your sorting algorithm
                        }
                    }));
                    mAdapter = new DocAdapter(getContext(), items, listClass);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Log.w(LOG_TAG, "SUCCESS " + "FAIL");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a doctor");
                }

            } else {
                if (successQuery) {
                    Log.w(LOG_TAG, "FAIL " + "SUCCESS");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a doctor");
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
        getArguments().putParcelableArrayList(ARG_DOCLIST, items);
    }

    //handle on click to perform animation
    @Override
    public void onClick(ImageView imageView, int position, boolean isLongClick) {

        fab.startAnimation(rotate_open);


        //see github project to more detail
        Fragment docFragment = DocFragment.newInstance(items.get(position));

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)
        //qui pendo potremo giocare con le animazioni come vogliamo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            docFragment.setSharedElementEnterTransition(new DetailsTransition());
            docFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            docFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, "kittenImage")
                .replace(R.id.content_frame, docFragment)
                .addToBackStack(null)
                .commit();
    }

    private void enableFab() {
        Log.w(LOG_TAG,"enable fab");
        fab.setEnabled(true);
        fab.setAlpha(1f);

    }

    private void disableFab() {
        Log.w(LOG_TAG,"disable fab");
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
