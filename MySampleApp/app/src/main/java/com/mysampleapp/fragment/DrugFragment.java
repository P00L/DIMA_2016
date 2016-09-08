package com.mysampleapp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mysampleapp.ObservableScrollView;
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
 */
public class DrugFragment extends Fragment implements ObservableScrollView.OnScrollChangedListener {

    private static final String ARG_DRUGDO = "param1";
    private OnFragmentInteractionListener mListener;
    private DrugDO drugDO;
    private AppCompatActivity activity;
    private FloatingActionButton fab;
    private Animation rotate_close;
    private Button sendEmailButton;
    private ObservableScrollView scrollView;
    private FrameLayout header;



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
        if (getArguments() != null)
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
        scrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangedListener(this);
        // Store the reference of your image container
        header = (FrameLayout) view.findViewById(R.id.img_container);
        sendEmailButton = (Button) view.findViewById(R.id.button_send);
        fab.setImageResource(R.drawable.modify);
        rotate_close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_360);
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

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPickerDoctor dialogPicker = new DialogPickerDoctor();
                dialogPicker.setOnClick(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO SETTARE TUTTI I CAMPI
                        //io metterei nelle shared pref i dottori attivi per non fare la query qui
                        //e terrei aggiornate le shared pref sulla modifica/aggiunta/cancellazione del dottore
                        Log.w("DrugFragment",which+"");
                        Intent intentEmail = new Intent(Intent.ACTION_SEND);
                        intentEmail.setData(Uri.parse("mailto:"));
                        intentEmail.setType("message/rfc822");
                        String[] to = {"fusari.pool@gmail.com"};
                        intentEmail.putExtra(Intent.EXTRA_EMAIL, to);
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "prova mail");
                        intentEmail.putExtra(Intent.EXTRA_TEXT, "finite pastiglie xxx");
                        if (intentEmail.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(Intent.createChooser(intentEmail, "Choose an email client from..."));
                        }
                    }
                });
                dialogPicker.show(activity.getSupportFragmentManager(), null);
            }
        });

        activity.getSupportActionBar().setTitle(R.string.drug);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_drug);

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
        TextView textViewName = (TextView) view.findViewById(R.id.name_val);
        textViewName.setText(drugDO.getName());

        TextView textViewType = (TextView) view.findViewById(R.id.type_val);
        textViewType.setText(drugDO.getType());

        TextView textViewWeight = (TextView) view.findViewById(R.id.weight_val);
        textViewWeight.setText(String.valueOf(drugDO.getWeight().intValue()));

        TextView textViewNotes = (TextView) view.findViewById(R.id.notes_val);
        textViewNotes.setText(drugDO.getNotes());

        TextView textViewQty = (TextView) view.findViewById(R.id.qty_val);
        textViewQty.setText(String.valueOf(drugDO.getQuantity().intValue()));

        TextView textViewMinQty = (TextView) view.findViewById(R.id.minqty_val);
        textViewMinQty.setText(String.valueOf(drugDO.getMinqty().intValue()));

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

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        int scrollY = scrollView.getScrollY();
        // Add parallax effect
        header.setTranslationY(scrollY * 0.2f);
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
