package com.mysampleapp.fragment;

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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.ArrayList;


public class DocFragment extends Fragment {

    private static final String ARG_DOCDO = "param1";
    private DoctorDO doctorDO;
    private AppCompatActivity activity;
    private FloatingActionButton fab;
    private Animation rotate_close;

    public DocFragment() {
        // Required empty public constructor
    }

    public static DocFragment newInstance(DoctorDO doctorDO) {
        DocFragment fragment = new DocFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DOCDO, doctorDO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            doctorDO = getArguments().getParcelable(ARG_DOCDO);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.w("docdo", doctorDO.getName());
        activity = (AppCompatActivity) getActivity();
        rotate_close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_360);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        if (!fab.isShown()) {
            fab.show();
        }
        fab.setImageResource(R.drawable.ic_action_modify);
        //fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //il fab manda al form di edit e passa doctorDO come parametro da salvare nel Bundle
                DocFormFragment fragment = DocFormFragment.newInstance(doctorDO, true, new ArrayList<DoctorDO>());
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        activity.getSupportActionBar().setTitle(R.string.doctor);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_doc);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(rotate_close);
                activity.getSupportFragmentManager().popBackStack();
            }
        });

        // TODO: Rename method, update a

        TextView textView = (TextView) view.findViewById(R.id.doc_name);
        textView.setText(doctorDO.getName());

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(ARG_DOCDO, doctorDO);
    }

}
