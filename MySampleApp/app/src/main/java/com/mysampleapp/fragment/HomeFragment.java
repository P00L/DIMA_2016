package com.mysampleapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmService;
import com.mysampleapp.DetailsTransition;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.HomeAdapter;
import com.mysampleapp.adapter.ItemClickListener;
import com.mysampleapp.adapter.ItemClickListenerAnimation;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ItemClickListener, ItemClickListenerAnimation {

    private static final String ARG_SCHEDULELIST = "param1";
    private final static String LOG_TAG = PendingScheduleFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private TextView noDataTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;
    private ProgressBar mProgress;
    private DynamoDBMapper mapper;
    private DemoNoSQLOperation operation;
    private ArrayList<ScheduleDrugDO> items;
    private ArrayList<ScheduleDrugDO> todayItems;
    private MyAsyncTask myAsyncTask;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todayItems = getArguments().getParcelableArrayList(ARG_SCHEDULELIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.hide();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        noDataTextView = (TextView) view.findViewById(R.id.no_data);
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "all");

        activity.getSupportActionBar().setTitle(R.string.home);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.hamburger);
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

        if (todayItems == null) {
            Log.w(LOG_TAG, "load");
            myAsyncTask = new MyAsyncTask(this);
            myAsyncTask.execute();
        } else {
            //fake instantiation of items to avoid reload on rotation if came from a rotation
            items = new ArrayList<>();
            if (todayItems.size() > 0) {
                Log.w(LOG_TAG, "restore items");
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(todayItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getHour().compareTo(s2.getHour());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new HomeAdapter(getContext(), todayItems, this, this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Log.w(LOG_TAG, "restore no data string");
                enableEmptyState("No pending schedule \n for taday");
            }
        }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        Boolean success = false;
        Boolean successQuery = false;
        HomeFragment listClass;

        public MyAsyncTask(HomeFragment listClass) {
            this.listClass = listClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            disableEmptyState();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                successQuery = operation.executeOperation();
                items = ((DemoNoSQLTableScheduleDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
                success = true;
            } catch (final AmazonClientException ex) {
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgress.setVisibility(View.GONE);
            todayItems = new ArrayList<>();
            if (success) {
                for (ScheduleDrugDO d : items) {
                    String days = d.getDay();
                    String[] splitday = days.split("/");
                    List<String> list = new ArrayList<String>();
                    Log.w(LOG_TAG,days.toString());
                    //convert string format of day to int to match Calendar day
                    for (String s : splitday)
                        switch (s) {
                            case "L":
                                list.add("2");
                                break;
                            case "MA":
                                list.add("3");
                                break;
                            case "ME":
                                list.add("4");
                                break;
                            case "G":
                                list.add("5");
                                break;
                            case "V":
                                list.add("6");
                                break;
                            case "S":
                                list.add("7");
                                break;
                            case "D":
                                list.add("1");
                                break;
                            default:
                                list.add("x");
                                break;
                        }
                    Log.w(LOG_TAG,list.toString());
                    Log.w(LOG_TAG,list.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+"")+"");
                    if (list.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+"")) {
                        todayItems.add(d);
                        Log.w(LOG_TAG, "schedule drug " + d.getAlarmId().intValue() + "/" + d.getDrug());
                    }
                }
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(todayItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getHour().compareTo(s2.getHour());   //or whatever your sorting algorithm
                    }
                }));
                if (todayItems.size() > 0) {
                    Log.w(LOG_TAG, "SUCCESS " + "SUCCESS");
                    mAdapter = new HomeAdapter(getContext(), todayItems, listClass, listClass);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    enableEmptyState("No pending schedule \n for today");
                }
            } else {
                Log.w(LOG_TAG, "FAIL " + "FAIL");
                Snackbar snackbar = Snackbar
                        .make(fab, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myAsyncTask = new MyAsyncTask(listClass);
                                myAsyncTask.execute();
                            }
                        });
                snackbar.show();
                enableEmptyState("No data available \n check your connection");
            }
        }
    }

    private void enableEmptyState(String text) {
        noDataTextView.setText(text);
        noDataTextView.setVisibility(View.VISIBLE);
    }

    private void disableEmptyState() {
        noDataTextView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view, final int position, boolean isLongClick) {

    }

    @Override
    public void onClick(ImageView imageView, int position, boolean isLongClick) {

        //see github project to more detail
        Fragment scheduleFragment = ScheduleFragment.newInstance(todayItems.get(position));

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)
        //qui pendo potremo giocare con le animazioni come vogliamo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleFragment.setSharedElementEnterTransition(new DetailsTransition());
            scheduleFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            scheduleFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, "kittenImage")
                .replace(R.id.content_frame, scheduleFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myAsyncTask !=null) {
            myAsyncTask.cancel(true);
            mProgress.setVisibility(View.GONE);
        }
        if (items == null)
            getArguments().putParcelableArrayList(ARG_SCHEDULELIST, null);
        else
            getArguments().putParcelableArrayList(ARG_SCHEDULELIST, todayItems);

    }
}
