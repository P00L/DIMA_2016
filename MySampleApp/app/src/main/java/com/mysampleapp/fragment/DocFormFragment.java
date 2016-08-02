package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.DoctorDO;
import com.mysampleapp.demo.nosql.DrugDO;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DocFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DocFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocFormFragment extends Fragment implements VerticalStepperForm {

    private OnFragmentInteractionListener mListener;

    private VerticalStepperFormLayout verticalStepperForm;
    private DynamoDBMapper mapper;
    private DoctorDO docDO;
    private EditText email_text;
    private EditText active_text;
    private EditText address_text;
    private EditText name_text;
    private EditText phoneNumber_text;
    private EditText surname_text;


    public DocFormFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DocFormFragment newInstance() {
        DocFormFragment fragment = new DocFormFragment();
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
        View view = inflater.inflate(R.layout.fragment_doc_form, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        docDO = new DoctorDO();

        String[] mySteps = {"Name", "Surname", "e-mail", "Active", "Phone number", "Address"};
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        int colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) view.findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, getActivity())
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();

        if (fab.isShown())
            fab.hide();

        return view;
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


    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createSurnameStep();
                break;
            case 2:
                view = createEmailStep();
                break;
            case 3:
                view = createActiveStep();
                break;
            case 4:
                view = createSPhoneNumberStep();
                break;
            case 5:
                view = createAddressStep();
                break;
        }
        return view;
    }

    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        name_text = new EditText(getActivity());
        name_text.setSingleLine(true);
        name_text.setHint("name");
        name_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return name_text;
    }

    private View createSurnameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        surname_text = new EditText(getActivity());
        surname_text.setSingleLine(true);
        surname_text.setHint("surname");
        surname_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return surname_text;
    }

    private View createEmailStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        surname_text = new EditText(getActivity());
        surname_text.setSingleLine(true);
        surname_text.setHint("surname");
        surname_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return surname_text;
    }

    private View createActiveStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        email_text = new EditText(getActivity());
        email_text.setSingleLine(true);
        email_text.setHint("email");
        email_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return email_text;
    }

    private View createSPhoneNumberStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        phoneNumber_text = new EditText(getActivity());
        phoneNumber_text.setSingleLine(true);
        phoneNumber_text.setHint("phone number");
        phoneNumber_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return phoneNumber_text;
    }

    private View createAddressStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        address_text = new EditText(getActivity());
        address_text.setSingleLine(true);
        address_text.setHint("address");
        address_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return address_text;
    }


    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case 1:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case 2:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case 3:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case 4:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case 5:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
        }
    }

    @Override
    public void sendData() {
        String tmp;

        docDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        docDO.setName(name_text.getText().toString());
        docDO.setSurname(surname_text.getText().toString());
        docDO.setEmail(email_text.getText().toString());
        // TODO FIX
        docDO.setActive(Boolean.TRUE);
        tmp = phoneNumber_text.getText().toString();
        docDO.setPhoneNumber(Double.parseDouble(tmp));
        docDO.setAddress(address_text.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mapper.save(docDO);
                } catch (final AmazonClientException ex) {
                    Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                }
            }
        }).start();

        Fragment fragment = DocListFragment.newInstance();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        activity.getSupportActionBar().setTitle(R.string.doctors);
    }
}
