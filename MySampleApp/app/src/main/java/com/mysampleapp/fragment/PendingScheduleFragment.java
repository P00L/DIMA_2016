package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmService;
import com.mysampleapp.DetailsTransition;
import com.mysampleapp.R;
import com.mysampleapp.SottoscortaService;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.ItemClickListener;
import com.mysampleapp.adapter.ItemClickListenerAnimation;
import com.mysampleapp.adapter.PendingScheduleAdapter;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.DrugDO;
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
public class PendingScheduleFragment extends Fragment implements ItemClickListener, ItemClickListenerAnimation {

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
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private Animation rotate_open;
    private Animation rotate_open_360;
    private Animation rotate_close;
    private ProgressBar mProgress;
    private Paint p = new Paint();
    private FrameLayout fabMenuBG;
    private int edit_position;
    private AlertDialog.Builder alertDialog;
    private MyAsyncTask myAsyncTask;

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
        fabMenuBG = (FrameLayout) view.findViewById(R.id.swipe_bg);
        //getting pending notification
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        pending_notification = new HashSet<String>(sharedPref.getStringSet(getContext().getString(R.string.pending_alarm),
                new HashSet<String>()));

        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) activity.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) activity.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) activity.findViewById(R.id.fab3);
        rotate_open = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_180);
        rotate_close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_180);
        rotate_open_360 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_360);

        if (!fab.isShown()) {
            fab.show();
        }
        fab.setImageResource(R.drawable.expand);
        //fab.startAnimation(rotate_close_180);

        //hide fab on scroll down show on scroll up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (fab1.isShown()) {
                        fabMenuBG.setVisibility(View.GONE);
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();
                        fab.startAnimation(rotate_close);
                        fab.hide();
                    } else {
                        fab.hide();
                    }
                    //fab.animate().translationY(fab.getHeight() + 32).setInterpolator(new AccelerateInterpolator(2)).start();
                } else if (dy < 0) {
                    fab.show();
                    //fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                }
            }
        });

        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!fab1.isShown()) {
                            //getView().setAlpha(0.2f);
                            //mRecyclerView.setClickable(false);
                            //open menu
                            fab1.show();
                            fab2.show();
                            fab3.show();
                            fabMenuBG.setVisibility(View.VISIBLE);
                            fab.startAnimation(rotate_open);

                        } else {
                            //close menu
                            //getView().setAlpha(1f);
                            //mRecyclerView.setClickable(true);
                            fab1.hide();
                            fab2.hide();
                            fab3.hide();
                            fabMenuBG.setVisibility(View.GONE);
                            fab.startAnimation(rotate_close);
                        }

                    }
                }
        );

        //postpone all
        fab1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogPickerDelay dialogPicker = new DialogPickerDelay();
                        dialogPicker.setOnClick(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //start service to postpone alarm
                                Intent i = new Intent(getContext(), AlarmService.class);
                                i.putExtra(AlarmService.SCHEDULE_LIST_EXTRA, pendingItems);
                                i.putExtra(AlarmService.ACTION_EXTRA, "postpone");
                                i.putExtra(AlarmService.DELAY_EXTRA, Integer.parseInt(getResources().getStringArray(R.array.delay)[which]));
                                getContext().startService(i);
                                int number_pending = pendingItems.size();
                                pendingItems.clear();
                                mAdapter.notifyItemRangeRemoved(0, number_pending);
                                if (pendingItems.size() == 0) {
                                    enableEmptyState("No pendig schedule");
                                    disableFab();
                                    fab1.hide();
                                    fab2.hide();
                                    fab3.hide();
                                }
                                mAdapter.notifyItemRangeRemoved(0, number_pending);
                                fab.show();
                                fab.startAnimation(rotate_close);
                                Snackbar.make(fab, "postpone all " +getResources().getStringArray(R.array.delay)[which]+" min!!", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        dialogPicker.show(activity.getSupportFragmentManager(), null);
                    }
                }
        );
        //skip all
        fab2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.w(LOG_TAG, "skip all");
                        for (ScheduleDrugDO shedule : pendingItems) {
                            clearSharedPref(shedule);
                        }
                        int number_pending = pendingItems.size();
                        pendingItems.clear();
                        if (pendingItems.size() == 0) {
                            enableEmptyState("No pendig schedule");
                            disableFab();
                            fab1.hide();
                            fab2.hide();
                            fab3.hide();
                        }
                        mAdapter.notifyItemRangeRemoved(0, number_pending);
                        fab.show();
                        fab.startAnimation(rotate_close);
                        Snackbar.make(fab, "Skipped all!!", Snackbar.LENGTH_LONG).show();
                    }
                }
        );
        //take all
        fab3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SaveTask(pendingItems).execute();
                    }
                }
        );

        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "all");

        activity.getSupportActionBar().setTitle(R.string.pending_schedule);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_pending_schedule);

        noDataTextView = (TextView) view.findViewById(R.id.no_data);

        if (pendingItems == null) {
            Log.w(LOG_TAG, "load");
            myAsyncTask = new MyAsyncTask(this);
            myAsyncTask.execute();
        } else {
            //fake instantiation of items to avoid reload on rotation if came from a rotation
            items = new ArrayList<>();
            if (pendingItems.size() > 0) {
                Log.w(LOG_TAG, "restore items");
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(pendingItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new PendingScheduleAdapter(getContext(), pendingItems, this, this);
                mRecyclerView.setAdapter(mAdapter);
                enableFab();
            } else {
                Log.w(LOG_TAG, "restore no data string");
                disableFab();
                enableEmptyState("No pending schedule");
            }
        }
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
        initSwipe();
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

    @Override
    public void onClick(View view, final int position, boolean isLongClick) {
        switch (view.getId()) {
            case R.id.take:
                Log.w(LOG_TAG, "take");
                ArrayList<ScheduleDrugDO> tmp = new ArrayList<>();
                tmp.add(pendingItems.get(position));
                new SaveTask(tmp).execute();
                break;
            case R.id.skip:
                Log.w(LOG_TAG, "skip");
                clearSharedPref(pendingItems.get(position));
                pendingItems.remove(position);
                if (pendingItems.size() == 0) {
                    enableEmptyState("No pendig schedule");
                    disableFab();
                    fab1.hide();
                    fab2.hide();
                    fab3.hide();
                }
                fab.show();
                mAdapter.notifyItemRemoved(position);
                break;
            case R.id.postpone:
                Log.w(LOG_TAG, "postpone");
                DialogPickerDelay dialogPicker = new DialogPickerDelay();
                dialogPicker.setOnClick(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<ScheduleDrugDO> tmp = new ArrayList<>();
                        //start service to postpone alarm
                        Intent i = new Intent(getContext(), AlarmService.class);
                        tmp.add(pendingItems.get(position));
                        i.putExtra(AlarmService.SCHEDULE_LIST_EXTRA, tmp);
                        i.putExtra(AlarmService.ACTION_EXTRA, "postpone");
                        i.putExtra(AlarmService.DELAY_EXTRA, Integer.parseInt(getResources().getStringArray(R.array.delay)[which]));
                        getContext().startService(i);
                        pendingItems.remove(position);
                        mAdapter.notifyItemRemoved(position);
                    }
                });
                dialogPicker.show(activity.getSupportFragmentManager(), null);
                break;
        }
    }

    @Override
    public void onClick(ImageView imageView, int position, boolean isLongClick) {
        fab.startAnimation(rotate_open_360);

        //see github project to more detail
        Fragment scheduleFragment = ScheduleFragment.newInstance(pendingItems.get(position));

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
    public void onPause() {
        super.onPause();
        if (myAsyncTask !=null) {
            myAsyncTask.cancel(true);
            mProgress.setVisibility(View.GONE);
        }
        if (fab1.isShown()) {
            fab1.hide();
            fab2.hide();
            fab3.hide();
            fab.startAnimation(rotate_close);
        }
        if (items == null)
            getArguments().putParcelableArrayList(ARG_SCHEDULELIST, null);
        else
            getArguments().putParcelableArrayList(ARG_SCHEDULELIST, pendingItems);

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        Boolean success = false;
        Boolean successQuery = false;
        PendingScheduleFragment listClass;

        public MyAsyncTask(PendingScheduleFragment listClass) {
            this.listClass = listClass;
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
            pendingItems = new ArrayList<>();
            Log.w(LOG_TAG, "pending schedule" + pending_notification.toString());
            if (success) {
                for (ScheduleDrugDO d : items) {
                    if (pending_notification.contains(d.getAlarmId().intValue() + "/" + d.getDrug())) {
                        pendingItems.add(d);
                        Log.w(LOG_TAG, "schedule drug " + d.getAlarmId().intValue() + "/" + d.getDrug());
                    }
                }
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(pendingItems, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                if (pendingItems.size() > 0) {
                    Log.w(LOG_TAG, "SUCCESS " + "SUCCESS");
                    enableFab();
                    mAdapter = new PendingScheduleAdapter(getContext(), pendingItems, listClass, listClass);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    disableFab();
                    enableEmptyState("No pending schedule");
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
                disableFab();
                enableEmptyState("No data available \n check your connection");
            }
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;
        private ArrayList<ScheduleDrugDO> scheduleDrugList;
        private DrugDO drugItem;

        public SaveTask(ArrayList<ScheduleDrugDO> scheduleDrugList) {
            this.scheduleDrugList = scheduleDrugList;
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (scheduleDrugList.size() != 1) {
                mProgress.setVisibility(View.VISIBLE);
            }
                /*
            //TODO NON RIMANE SULLA ROTAZIONE
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getContext());
            // Set progressdialog title
            mProgressDialog.setTitle("Save data");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
            */

        }

        @Override
        protected Void doInBackground(Void... params) {
            for (ScheduleDrugDO scheduleDrugDO : scheduleDrugList) {
                //getting the drug associated to the schedule to decrement the sottoscorta
                DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                        .getNoSQLTableByTableName("Drug");
                DemoNoSQLOperation operation = ((DemoNoSQLTableDrug) demoTable).getOperationByNameSingle(getContext(), "one", scheduleDrugDO.getDrug());
                try {
                    success = operation.executeOperation();
                    drugItem = ((DemoNoSQLTableDrug.DemoGetWithPartitionKeyAndSortKey) operation).getResult();
                    Log.w(LOG_TAG, drugItem.getQuantity() + "-" + scheduleDrugDO.getQuantity() + "=" + (drugItem.getQuantity() - scheduleDrugDO.getQuantity()));
                    drugItem.setQuantity(drugItem.getQuantity() - scheduleDrugDO.getQuantity());
                    mapper.save(drugItem);
                    success = true;

                } catch (final AmazonClientException ex) {
                    Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                    success = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (scheduleDrugList.size() == 1) {
                int position = pendingItems.indexOf(scheduleDrugList.get(0));
                if (success) {
                    pendingItems.remove(scheduleDrugList.get(0));
                    if (pendingItems.size() == 0) {
                        enableEmptyState("No pendig schedule");
                        disableFab();
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();
                    }
                    fab.show();
                    mAdapter.notifyItemRemoved(position);
                    clearSharedPref(scheduleDrugList.get(0));
                    //start service to check sottoscorta
                    Intent i = new Intent(getContext(), SottoscortaService.class);
                    i.putExtra(SottoscortaService.DRUG_EXTRA, drugItem);
                    i.putExtra(SottoscortaService.ACTION_EXTRA, "take");
                    getContext().startService(i);
                    Snackbar.make(fab, scheduleDrugList.get(0).getDrug() + " Taken!", Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(fab, scheduleDrugList.get(0).getDrug() + " Skipped!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                mProgress.setVisibility(View.GONE);
                if (success) {
                    Snackbar.make(fab, "Taken all!", Snackbar.LENGTH_LONG).show();
                    for (ScheduleDrugDO shedule : pendingItems) {
                        clearSharedPref(shedule);
                    }
                    int number_pending = pendingItems.size();
                    pendingItems.clear();
                    if (pendingItems.size() == 0) {
                        enableEmptyState("No pendig schedule");
                        disableFab();
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();
                    }
                    mAdapter.notifyItemRangeRemoved(0, number_pending);
                    fab.show();
                    fab.startAnimation(rotate_close);
                    Snackbar.make(fab, "Taken all!!", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(fab, "Error!", Snackbar.LENGTH_LONG).show();
            }
        }

    }

    private void clearSharedPref(ScheduleDrugDO scheduleDrugDO) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getContext().getString(R.string.pending_alarm),
                new HashSet<String>()));
        Log.w("AlarmService", "pending notification " + s.toString());
        String pending_id = scheduleDrugDO.getAlarmId().intValue() + "/" + scheduleDrugDO.getDrug();
        Log.w("AlarmService", "pending id " + pending_id);
        //if there are pending notification in the shared pref we delete it
        Boolean removed = s.remove(pending_id);
        Log.w("AlarmService", "removed " + removed);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getContext().getString(R.string.pending_alarm), s);
        editor.apply();
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

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    Log.w(LOG_TAG, "take");
                    ArrayList<ScheduleDrugDO> tmp = new ArrayList<>();
                    tmp.add(pendingItems.get(position));
                    new SaveTask(tmp).execute();
                } else {
                    Log.w(LOG_TAG, "skip");
                    clearSharedPref(pendingItems.get(position));
                    Snackbar.make(fab, pendingItems.get(position).getDrug() + " Skipped!", Snackbar.LENGTH_LONG).show();
                    pendingItems.remove(position);
                    if (pendingItems.size() == 0) {
                        enableEmptyState("No pendig schedule");
                        disableFab();
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();
                    }
                    fab.show();
                    mAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(getResources().getColor(R.color.red));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.x_swipe);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.clipRect(background);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(getResources().getColor(R.color.green));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.take_swipe);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.clipRect(background);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                c.restore();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
