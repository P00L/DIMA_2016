package com.mysampleapp.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mysampleapp.AlarmReceiver;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFormFragment extends Fragment {

    public final static String ALARM_ID_EXTRA = "alarm_id";
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;

    public ScheduleFormFragment() {
        // Required empty public constructor
    }

    public static ScheduleFormFragment newInstance() {
        ScheduleFormFragment fragment = new ScheduleFormFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_form, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);

        fab.hide();

        activity.getSupportActionBar().setTitle(R.string.add_schedule);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_doc);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity)activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity)activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                Fragment fragment = ScheduleListFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

            }
        });

        Button button = (Button) view.findViewById(R.id.fire);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // fire the alarm
                Log.w("ScheduleFormFragment","FIRE IN THE HOLE");

                AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
                // Define our intention of executing AlertReceiver
                Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);

                //get shared pref file
                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
                // getting the last used number for the alarm id or 0 as default value for the first
                int old_alarm_id = sharedPref.getInt(getContext().getString(R.string.alarm_id),0);
                SharedPreferences.Editor editor = sharedPref.edit();
                int alarm_id = old_alarm_id +1;
                editor.putInt(getString(R.string.alarm_id), alarm_id);
                editor.apply();

                alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA,alarm_id);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarm_id, alertIntent,PendingIntent.FLAG_ONE_SHOT);

                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                15*1000, alarmIntent);

                Toast.makeText(getContext(), "set alarm 15 second from now", Toast.LENGTH_SHORT).show();
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
}
