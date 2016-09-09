package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmService;
import com.mysampleapp.ObservableScrollView;
import com.mysampleapp.R;
import com.mysampleapp.SottoscortaService;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DrugDO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private ObservableScrollView scrollView;
    private FrameLayout header;
    private DynamoDBMapper mapper;
    private TextView textViewQty;


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
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        activity = (AppCompatActivity) getActivity();
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        scrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangedListener(this);
        // Store the reference of your image container
        header = (FrameLayout) view.findViewById(R.id.img_container);
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
        TextView textViewName = (TextView) view.findViewById(R.id.name);
        textViewName.setText(drugDO.getName());

        TextView textViewType = (TextView) view.findViewById(R.id.type);
        textViewType.setText(drugDO.getType());

        TextView textViewWeight = (TextView) view.findViewById(R.id.weight);
        textViewWeight.setText(String.valueOf(drugDO.getWeight().intValue()) + " mg");

        TextView textViewNotes = (TextView) view.findViewById(R.id.notes);
        textViewNotes.setText(drugDO.getNotes());

        textViewQty = (TextView) view.findViewById(R.id.quantity);
        textViewQty.setText(String.valueOf(drugDO.getQuantity().intValue()));

        TextView textViewMinQty = (TextView) view.findViewById(R.id.sottoscorta);
        textViewMinQty.setText(String.valueOf(drugDO.getMinqty().intValue()));

        ImageButton refill = (ImageButton) view.findViewById(R.id.refill);
        refill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPickerRefill dialogPicker = new DialogPickerRefill();
                dialogPicker.setOnClick(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drugDO.setQuantity(Integer.parseInt(getResources().getStringArray(R.array.refill)[which]) + drugDO.getQuantity());
                        new SaveTask().execute();
                    }
                });
                dialogPicker.show(activity.getSupportFragmentManager(), null);
            }
        });

        ImageButton email = (ImageButton) view.findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPickerDoctor dialogPicker = new DialogPickerDoctor();
                dialogPicker.setOnClick(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPref = getContext().getSharedPreferences(
                                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

                        String doctor_list = sharedPref.getString(getContext().getString(R.string.active_doctor_ordered), "");
                        Log.w("asd",doctor_list.toString());
                        String[] doctors = doctor_list.split("//");
                        Log.w("asd",doctors.toString());
                        String[] splitted = doctors[which].split("/");
                        Log.w("asd",splitted.toString());
                        Intent intentEmail = new Intent(Intent.ACTION_SEND);
                        intentEmail.setData(Uri.parse("mailto:"));
                        intentEmail.setType("message/rfc822");
                        String[] to = {splitted[2]};
                        intentEmail.putExtra(Intent.EXTRA_EMAIL, to);
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "refill pill");
                        intentEmail.putExtra(Intent.EXTRA_TEXT, "I need " + drugDO.getName());
                        if (intentEmail.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(Intent.createChooser(intentEmail, "Choose an email client from..."));
                        }
                    }
                });
                dialogPicker.show(activity.getSupportFragmentManager(), null);
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

    public void setResult(final DrugDO result) {
        this.drugDO = result;
    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(ARG_DRUGDO, drugDO);
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;

        public SaveTask() {
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.w("asdasdasdas",drugDO.getUserId());
                mapper.save(drugDO);
                success = true;

            } catch (final AmazonClientException ex) {
                Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (success) {
                textViewQty.setText(String.valueOf(drugDO.getQuantity().intValue()));
            } else {
                Snackbar snackbar = Snackbar
                        .make(fab, "refill failed!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new SaveTask().execute();
                            }
                        });
                snackbar.show();

            }
        }
    }

}
