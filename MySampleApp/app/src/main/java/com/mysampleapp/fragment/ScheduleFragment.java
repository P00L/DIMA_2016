package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    private static final String ARG_SCHEDULEDRUGDO = "param1";
    private AppCompatActivity activity;
    private OnFragmentInteractionListener mListener;
    private ScheduleDrugDO scheduleDrugDO;
    private FloatingActionButton fab;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    public static ScheduleFragment newInstance(ScheduleDrugDO scheduleDrugDO) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SCHEDULEDRUGDO, scheduleDrugDO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            scheduleDrugDO = getArguments().getParcelable(ARG_SCHEDULEDRUGDO);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //Log.w("docdo", doctorDO.getName());
        activity = (AppCompatActivity) getActivity();
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_modify);
        if (!fab.isShown()) {
            fab.show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //il fab manda al form di edit e passa doctorDO come parametro da salvare nel Bundle
                ScheduleFormFragment fragment = ScheduleFormFragment.newInstance(scheduleDrugDO,true,new ArrayList<ScheduleDrugDO>());
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        activity.getSupportActionBar().setTitle(R.string.schedule);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_schedule);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity)activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity)activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                fab.setImageResource(R.drawable.ic_action_plus);
                RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(500);
                rotate.setInterpolator(new LinearInterpolator());
                fab.startAnimation(rotate);
                activity.getSupportFragmentManager().popBackStack();

            }
        });

        // TODO: Rename method, update a

        TextView textView = (TextView) view.findViewById(R.id.schedule_name);
        textView.setText(scheduleDrugDO.getDrug());
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

    public void setResult(ScheduleDrugDO scheduleDrugDO) {
        this.scheduleDrugDO = scheduleDrugDO;

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

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(ARG_SCHEDULEDRUGDO, scheduleDrugDO);
    }

}
