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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mysampleapp.ObservableScrollView;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.nosqldb.models.ScheduleDrugDO;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment implements ObservableScrollView.OnScrollChangedListener {

    private static final String ARG_SCHEDULEDRUGDO = "param1";
    private AppCompatActivity activity;
    private OnFragmentInteractionListener mListener;
    private ScheduleDrugDO scheduleDrugDO;
    private FloatingActionButton fab;
    private Animation rotate_close;
    private ObservableScrollView scrollView;
    private FrameLayout header;

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
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.w("docdo", doctorDO.getName());
        activity = (AppCompatActivity) getActivity();
        scrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangedListener(this);
        // Store the reference of your image container
        header = (FrameLayout) view.findViewById(R.id.img_container);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.modify);
        rotate_close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_360);
        if (!fab.isShown()) {
            fab.show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //il fab manda al form di edit e passa doctorDO come parametro da salvare nel Bundle
                ScheduleFormFragment fragment = ScheduleFormFragment.newInstance(scheduleDrugDO, true, new ArrayList<ScheduleDrugDO>());
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
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.prev);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                fab.startAnimation(rotate_close);
                activity.getSupportFragmentManager().popBackStack();

            }
        });

        TextView drugTextView = (TextView) view.findViewById(R.id.drug);
        drugTextView.setText(scheduleDrugDO.getDrug());

        TextView qtyTextView = (TextView) view.findViewById(R.id.qty);

        switch (scheduleDrugDO.getQuantity().toString()) {
            case "1.0":
                qtyTextView.setText("1");
                break;
            case "0.5":
                qtyTextView.setText("1/2");
                break;
            case "0.25":
                qtyTextView.setText("1/4");
                break;
            case "2.0":
                qtyTextView.setText("2");
                break;
        }

        TextView hourTextView = (TextView) view.findViewById(R.id.hour);
        hourTextView.setText(scheduleDrugDO.getHour()+"");


        TextView dayTextView = (TextView) view.findViewById(R.id.day);
        dayTextView.setText(scheduleDrugDO.getDay()+"");


        TextView notesTextView = (TextView) view.findViewById(R.id.notes);
        notesTextView.setText(scheduleDrugDO.getNotes());
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

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        int scrollY = scrollView.getScrollY();
        // Add parallax effect
        header.setTranslationY(scrollY * 0.2f);
        if (deltaY > 0) {
            fab.hide();
            //fab.animate().translationY(fab.getHeight() + 32).setInterpolator(new AccelerateInterpolator(2)).start();
        } else if (deltaY < 0)
            fab.show();
        //fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
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
