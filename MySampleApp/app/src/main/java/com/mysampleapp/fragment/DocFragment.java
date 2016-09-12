package com.mysampleapp.fragment;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mysampleapp.ObservableScrollView;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.ArrayList;


public class DocFragment extends Fragment implements ObservableScrollView.OnScrollChangedListener {

    private static final String ARG_DOCDO = "param1";
    private DoctorDO doctorDO;
    private AppCompatActivity activity;
    private FloatingActionButton fab;
    private Animation rotate_close;
    private ImageButton active;
    private ObservableScrollView scrollView;
    private FrameLayout header;

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
        active = (ImageButton) view.findViewById(R.id.active_image_button);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.w("docdo", doctorDO.getName());
        activity = (AppCompatActivity) getActivity();
        rotate_close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_360);
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        scrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangedListener(this);
        // Store the reference of your image container
        header = (FrameLayout) view.findViewById(R.id.img_container);
        active = (ImageButton) view.findViewById(R.id.active_image_button);
        if (doctorDO.getActive())
            active.setImageResource(R.drawable.btn_star_big_on_pressed);
        else
            active.setImageResource(android.R.drawable.btn_star);

        TextView textViewName = (TextView) view.findViewById(R.id.name);
        textViewName.setText(doctorDO.getName());

        TextView textViewSurname = (TextView) view.findViewById(R.id.surname);
        textViewSurname.setText(doctorDO.getSurname());

        TextView textViewAddress = (TextView) view.findViewById(R.id.address);
        textViewAddress.setText(doctorDO.getAddress());

        TextView textViewEmail = (TextView) view.findViewById(R.id.email);
        textViewEmail.setText(doctorDO.getEmail());

        TextView textViewPhone = (TextView) view.findViewById(R.id.phone);
        textViewPhone.setText(doctorDO.getPhoneNumber());

        LinearLayout emailLinearLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        emailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEmail = new Intent(Intent.ACTION_SEND);
                intentEmail.setData(Uri.parse("mailto:"));
                intentEmail.setType("message/rfc822");
                String[] to = {doctorDO.getEmail()};
                intentEmail.putExtra(Intent.EXTRA_EMAIL, to);
                if (intentEmail.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intentEmail, "Choose an email client from..."));
                }
            }
        });

        LinearLayout phoneLinearLayout = (LinearLayout) view.findViewById(R.id.phone_layout);
        phoneLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + doctorDO.getPhoneNumber()));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        LinearLayout mapsLinearLayout = (LinearLayout) view.findViewById(R.id.maps_layout);
        mapsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + doctorDO.getAddress();
                Uri mapUri = Uri.parse(map);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(mapUri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        if (!fab.isShown()) {
            fab.show();
        }
        fab.setImageResource(R.drawable.modify);
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
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.prev);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(rotate_close);
                activity.getSupportFragmentManager().popBackStack();
            }
        });

        // TODO: Rename method, updat

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
