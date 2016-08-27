package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.PendingScheduleAdapter;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingScheduleFragment extends Fragment {

    private static final String ARG_SCHEDULELIST = "param1";
    private final static String LOG_TAG = PendingScheduleFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private ProgressDialog mProgressDialog;
    private DemoNoSQLOperation operation;
    private ArrayList<ScheduleDrugDO> items;
    private ArrayList<ScheduleDrugDO> pendingItems;
    private DynamoDBMapper mapper;
    private Set<String> pending_notification;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noDataTextView;
    private AppCompatActivity activity;

    public PendingScheduleFragment() {
        // Required empty public constructor
    }


    public static PendingScheduleFragment newInstance() {
        PendingScheduleFragment fragment = new PendingScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pendingItems = getArguments().getParcelableArrayList(ARG_SCHEDULELIST);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_schedule, container, false);
    }


    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        //getting pending notification
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        pending_notification = new HashSet<String>(sharedPref.getStringSet(getContext().getString(R.string.pending_alarm),
                new HashSet<String>()));

        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.hide();

        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "all");

        activity.getSupportActionBar().setTitle(R.string.pending_schedule);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_pending_schedule);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        noDataTextView = (TextView) view.findViewById(R.id.no_data);

        if (pendingItems == null) {
            new MyAsyncTask().execute();
        } else {
            if (pendingItems.size() > 0) {
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(pendingItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new PendingScheduleAdapter(getContext(), pendingItems);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                noDataTextView.setVisibility(View.VISIBLE);
            }
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_skip_all).setVisible(true);
        menu.findItem(R.id.action_take_all).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        //TODO IMPLEMETARE TAKE ALL E CANCEL ALL
        switch (item.getItemId()) {
            case R.id.action_skip_all:
                Log.w(LOG_TAG,"skip all");
                return true;
            case R.id.action_take_all:
                Log.w(LOG_TAG,"take all");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelableArrayList(ARG_SCHEDULELIST, pendingItems);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        Boolean success = false;

        public MyAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("We are working for you");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                success = operation.executeOperation();
                items = ((DemoNoSQLTableScheduleDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
            } catch (final AmazonClientException ex) {
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            pendingItems = new ArrayList<>();
            Log.w(LOG_TAG, "pending schedule" + pending_notification.toString());
            if (success && items.size() > 0) {
                for (ScheduleDrugDO d : items) {
                    if (pending_notification.contains(d.getAlarmId().intValue() + "/" + d.getDrug())) {
                        pendingItems.add(d);
                        Log.w(LOG_TAG, "schedule drug " + d.getAlarmId().intValue() + "/" + d.getDrug());
                    }
                }
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                //TODO controlloare se server sortatre
                Collections.sort(pendingItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                if (pendingItems.size() > 0) {
                    mAdapter = new PendingScheduleAdapter(getContext(), pendingItems);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    noDataTextView.setVisibility(View.VISIBLE);
                }
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                noDataTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
