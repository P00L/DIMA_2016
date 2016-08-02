package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
public class DrugFormFragment extends Fragment implements VerticalStepperForm {

    private OnFragmentInteractionListener mListener;

    private VerticalStepperFormLayout verticalStepperForm;
    private DynamoDBMapper mapper;
    private DrugDO drugDO;
    private EditText name_text;
    private EditText notes_text;
    private EditText minqty_text;
    private EditText qty_text;
    private EditText type_text;
    private EditText weight_text;


    public DrugFormFragment() {
        // Required empty public constructor
    }

    public static DrugFormFragment newInstance() {
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
        String[] mySteps = {"Name", "Type", "Quantity", "Weight", "Sottoscorta", "Notes"};
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
                view = createTypeStep();
                break;
            case 2:
                view = createQuantityStep();
                break;
            case 3:
                view = createWeightStep();
                break;
            case 4:
                view = createSottoscortaStep();
                break;
            case 5:
                view = createNotesStep();
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

    private View createTypeStep() {
        // In this case we generate the view by inflating a XML file
        type_text = new EditText(getActivity());
        type_text.setSingleLine(true);
        type_text.setHint("type");
        type_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return type_text;
    }

    private View createQuantityStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        qty_text = new EditText(getActivity());
        qty_text.setSingleLine(true);
        qty_text.setHint("quantity");
        qty_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        return qty_text;
    }

    private View createWeightStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        weight_text = new EditText(getActivity());
        weight_text.setSingleLine(true);
        weight_text.setHint("weight");
        weight_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        return weight_text;
    }

    private View createSottoscortaStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        minqty_text = new EditText(getActivity());
        minqty_text.setSingleLine(true);
        minqty_text.setHint("sottoscorta");
        minqty_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        return minqty_text;
    }

    private View createNotesStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        notes_text = new EditText(getActivity());
        notes_text.setHint("weight");
        notes_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return notes_text;
    }


    @Override
    public void onStepOpening(int stepNumber) {
        //TODO aggiungere i controlli sui campi
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

    // metodo in carico di salvare i dati nel database dopo aver cliccato ok
    @Override
    public void sendData() {
        // database send data
        // TODO aggiungere da qualche parte i controllo se il campo viene lasciato vuoto se opzionale
        String tmp;

        drugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        drugDO.setName(name_text.getText().toString());
        tmp = minqty_text.getText().toString();
        drugDO.setMinqty(Double.parseDouble(tmp));
        drugDO.setNotes(notes_text.getText().toString());
        tmp = qty_text.getText().toString();
        drugDO.setQuantity(Double.parseDouble(tmp));
        drugDO.setType(type_text.getText().toString());
        tmp = weight_text.getText().toString();
        drugDO.setWeight(Double.parseDouble(tmp));

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

        Fragment fragment = DrugListFragment.newInstance();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        activity.getSupportActionBar().setTitle(R.string.drugs);

    }
}
