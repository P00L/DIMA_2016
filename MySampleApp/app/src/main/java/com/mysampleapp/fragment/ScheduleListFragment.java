package com.mysampleapp.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmReceiver;
import com.mysampleapp.DetailsTransition;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.adapter.ItemClickListenerAnimation;
import com.mysampleapp.adapter.ScheduleAdapter;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.DoctorDO;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


public class ScheduleListFragment extends Fragment implements ItemClickListenerAnimation {

    private final static String LOG_TAG = DrugListFragment.class.getSimpleName();
    private static final String ARG_SCHEDULELIST = "param1";
    private AppCompatActivity activity;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ScheduleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ScheduleDrugDO> items;
    private ProgressDialog mProgressDialog;
    private DemoNoSQLOperation operation;
    private TextView noDataTextView;
    private FloatingActionButton fab;
    private Animation rotate_open;
    private ProgressBar mProgress;
    private Paint p = new Paint();
    private MyAsyncTask myAsyncTask;

    public ScheduleListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ScheduleListFragment newInstance() {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            items = getArguments().getParcelableArrayList(ARG_SCHEDULELIST);
        }
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "all");
        noDataTextView = (TextView) view.findViewById(R.id.no_data);
        rotate_open = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_360);
        mProgress = (ProgressBar) view.findViewById(R.id.progress_bar);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        initSwipe();
        if (!fab.isShown()) {
            fab.show();
        }

        fab.setImageResource(R.drawable.plus);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ScheduleFormFragment.newInstance(new ScheduleDrugDO(), false, items);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        activity.getSupportActionBar().setTitle(R.string.scheduler);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_schedule);

        if (items == null) {
            myAsyncTask = new MyAsyncTask(this);
            myAsyncTask.execute();
        } else {
            if (items.size() > 0) {
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                Collections.sort(items, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter = new ScheduleAdapter(getContext(), items, this);
                mRecyclerView.setAdapter(mAdapter);
                enableFab();
            } else {
                Log.w(LOG_TAG, "restore no data string");
                enableFab();
                enableEmptyState("Click \"+\" \n to insert a schedule");
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
        ScheduleListFragment listClass;
        Boolean successQuery = false;

        public MyAsyncTask(ScheduleListFragment listFragment) {
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
            if (success) {
                Log.w(LOG_TAG, "SUCCESS " + items.size() + "");
                if (successQuery) {
                    Log.w(LOG_TAG, "SUCCESS " + "SUCCESS");
                    enableFab();
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    //TODO controlloare se server sortatre
                    Collections.sort(items, (new Comparator<ScheduleDrugDO>() {
                        @Override
                        public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                            return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                        }
                    }));
                    mAdapter = new ScheduleAdapter(getContext(), items, listClass);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    Log.w(LOG_TAG, "SUCCESS " + "FAIL");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a schedule");
                }

            } else {
                if (successQuery) {
                    Log.w(LOG_TAG, "FAIL " + "SUCCESS");
                    enableFab();
                    enableEmptyState("Click \"+\" \n to insert a schedule");
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
    }

    private class DeleteTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;
        private ScheduleDrugDO scheduleDrugDO;
        private DynamoDBMapper mapper;

        public DeleteTask(ScheduleDrugDO scheduleDrugDO) {
            success = false;
            this.scheduleDrugDO = scheduleDrugDO;
            this.mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mapper.delete(scheduleDrugDO);
                success = true;
            } catch (final AmazonClientException ex) {
                Log.e("ASD", "Failed deleting item : " + ex.getMessage(), ex);
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (success) {
                Snackbar.make(fab, scheduleDrugDO.getDrug()+" Deleted!", Snackbar.LENGTH_LONG).show();
                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

                //getting data from shared pref and add the new alarm manager as pending
                //returning the pending list of id alarm manager as string or an empty set
                Set<String> s = new HashSet<String>(sharedPref.getStringSet(getContext().getString(R.string.pending_alarm),
                        new HashSet<String>()));
                SharedPreferences.Editor editor = sharedPref.edit();
                s.remove(scheduleDrugDO.getAlarmId().intValue() + "/" + scheduleDrugDO.getDrug());
                editor.putStringSet(getContext().getString(R.string.pending_alarm), s);
                //persist immediatly the data in the shared pref
                editor.apply();

                //clear alarm manager accordng to alarm id
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), scheduleDrugDO.getAlarmId().intValue(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(alarmIntent);

            }
            else {
                items.add(scheduleDrugDO);
                Collections.sort(items, (new Comparator<ScheduleDrugDO>() {
                    @Override
                    public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                        return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                    }
                }));
                mAdapter.notifyItemInserted(items.indexOf(scheduleDrugDO));
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (myAsyncTask !=null) {
            myAsyncTask.cancel(true);
            mProgress.setVisibility(View.GONE);
        }
        getArguments().putParcelableArrayList(ARG_SCHEDULELIST, items);
    }

    //handle on click to perform animation
    @Override
    public void onClick(ImageView imageView, int position, boolean isLongClick) {

        fab.startAnimation(rotate_open);

        //see github project to more detail
        Fragment scheduleFragment = ScheduleFragment.newInstance(items.get(position));

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

    private void enableFab() {
        Log.w(LOG_TAG, "enable fab");
        fab.setEnabled(true);
        fab.setAlpha(1f);

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
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(newText);
                    return true;
                } else
                    return false;
            }
        });
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            Boolean delete = true;
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ScheduleDrugDO scheduleDrugDO = items.get(position);

                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    items.remove(scheduleDrugDO);
                    if (items.size() == 0) {
                        enableEmptyState(getResources().getString(R.string.enable_empty_doc));
                        enableFab();
                    }
                    mAdapter.notifyItemRemoved(position);
                    Snackbar snackbar = Snackbar
                            .make(fab, "Deleting on DISMISS", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    items.add(scheduleDrugDO);
                                    disableEmptyState();
                                    delete = false;
                                    Collections.sort(items, (new Comparator<ScheduleDrugDO>() {
                                        @Override
                                        public int compare(ScheduleDrugDO s1, ScheduleDrugDO s2) {
                                            return s1.getDrug().compareTo(s2.getDrug());   //or whatever your sorting algorithm
                                        }
                                    }));
                                    mAdapter.notifyItemInserted(position);

                                }
                            });
                    snackbar.setActionTextColor(getResources().getColor(R.color.input_error_color));
                    snackbar.setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            Log.w("ASD", "ASD");
                            if(delete)
                                new DeleteTask(scheduleDrugDO).execute();
                            else
                                Snackbar.make(fab, scheduleDrugDO.getDrug()+" Restored!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    snackbar.show();
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
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_swipe);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.clipRect(background);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_swipe);
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

