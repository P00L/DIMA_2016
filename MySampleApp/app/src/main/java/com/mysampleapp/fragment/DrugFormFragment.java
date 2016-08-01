package com.mysampleapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DrugDO;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DrugFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DrugFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugFormFragment extends Fragment implements VerticalStepperForm{

    private OnFragmentInteractionListener mListener;
    private VerticalStepperFormLayout verticalStepperForm;
    private DynamoDBMapper mapper;
    private DrugDO drugDO;
    private EditText email_text;
    private EditText name;


    public DrugFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrugFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DrugFormFragment newInstance(String param1, String param2) {
        DrugFormFragment fragment = new DrugFormFragment();
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
        View view = inflater.inflate(R.layout.fragment_drug_form, container, false);

        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        drugDO = new DrugDO();
        String[] mySteps = {"Name", "Email", "Phone Number"};
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
        email_text = (EditText) view.findViewById(R.id.email);
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
                view = createEmailStep();
                break;
            case 2:
                view = createPhoneNumberStep();
                break;
        }
        return view;
    }


    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        name = new EditText(getActivity());
        name.setSingleLine(true);
        name.setHint("Your name");
        return name;
    }

    private View createEmailStep() {
// In this case we generate the view by inflating a XML file
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout emailLayoutContent = (LinearLayout) inflater.inflate(R.layout.email_step_layout, null, false);
        EditText email = (EditText) emailLayoutContent.findViewById(R.id.email);
        return emailLayoutContent;
    }

    private View createPhoneNumberStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        EditText name = new EditText(getActivity());
        name.setSingleLine(true);
        name.setHint("Your name");
        return name;
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
                // As soon as the phone number step is open, we mark it as completed in order to show the "Continue"
                // button (We do it because this field is optional, so the user can skip it without giving any info)
                verticalStepperForm.setStepAsCompleted(2);
                // In this case, the instruction above is equivalent to:
                // verticalStepperForm.setActiveStepAsCompleted();
                break;
        }
    }

    @Override
    public void sendData() {
        String siringa = email_text.getText().toString();
        String siringona = name.getText().toString();
        // database send data
        drugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        drugDO.setName(siringa);
        drugDO.setMinqty(5.2);
        drugDO.setNotes(siringona);
        drugDO.setQuantity(5.2);
        drugDO.setType("type");
        drugDO.setWeight(5.2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mapper.save(drugDO);
                } catch (final AmazonClientException ex) {
                    Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                }
            }
        }).start();

        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }
}
