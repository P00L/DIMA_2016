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
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DrugDO;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DrugFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DrugFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DrugFragment extends Fragment {

    private static final String ARG_DRUGDO = "param1";
    private OnFragmentInteractionListener mListener;
    private DrugDO drugDO;
    private AppCompatActivity activity;
    private FloatingActionButton fab;


    // TODO: Rename and change types and number of parameters
    public static DrugFragment newInstance(DrugDO drugDO) {
        DrugFragment fragment = new DrugFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DRUGDO, drugDO);
        fragment.setArguments(args);
        return fragment;
    }
    public DrugFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments() != null)
            drugDO = getArguments().getParcelable(ARG_DRUGDO);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drug, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_modify);
        if (!fab.isShown()) {
            fab.show();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrugFormFragment fragment = DrugFormFragment.newInstance(drugDO, true, new ArrayList<DrugDO>());
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        activity.getSupportActionBar().setTitle(R.string.drug);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_drug);

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
        TextView textViewName = (TextView) view.findViewById(R.id.drug_name);
        textViewName.setText("Name: "+drugDO.getName());

        TextView textViewType = (TextView) view.findViewById(R.id.drug_type);
        textViewType.setText("Type: "+drugDO.getType());

        TextView textViewWeight = (TextView) view.findViewById(R.id.drug_weight);
        textViewWeight.setText("Weight: "+String.valueOf(drugDO.getWeight().intValue()));

        TextView textViewNotes = (TextView) view.findViewById(R.id.drug_notes);
        textViewNotes.setText("Notes: "+drugDO.getNotes());

        TextView textViewQty = (TextView) view.findViewById(R.id.drug_qty);
        textViewQty.setText("Quantity: "+String.valueOf(drugDO.getQuantity().intValue()));

        TextView textViewMinQty = (TextView) view.findViewById(R.id.drug_minqty);
        textViewMinQty.setText("Sottoscorta: "+String.valueOf(drugDO.getMinqty().intValue()));

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

    public void setResult(final DrugDO result) {
        this.drugDO = result;
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(ARG_DRUGDO, drugDO);
    }

}
