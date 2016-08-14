package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.regex.Pattern;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DocEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DocEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocEditFragment extends Fragment implements VerticalStepperForm {

    private static final int MIN_NAME_LENGTH = 3;

    private VerticalStepperFormLayout verticalStepperForm;
    private DynamoDBMapper mapper;
    private DoctorDO docDO;

    private static final int NAME_STEP = 0;
    private EditText name_text;
    private static final int SURNAME_STEP = 1;
    private EditText surname_text;
    private final static int EMAIL_STEP = 2;
    private EditText email_text;
    // diventa una checkbox
    private final static int ACTIVE_STEP = 3;
    private EditText active_text;
    private final static int PHONE_NUMBER_STEP = 4;
    private EditText phoneNumber_text;
    private final static int ADDRESS_STEP = 5;
    private EditText address_text;

    private AppCompatActivity activity;


    private OnFragmentInteractionListener mListener;

    public DocEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DocEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocEditFragment newInstance(DoctorDO doc) {
        DocEditFragment fragment = new DocEditFragment();
        Bundle args = new Bundle();
        //salva i parametri di doc
        args.putParcelable("doctorDoParc", doc);
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
        View view = inflater.inflate(R.layout.fragment_doc_edit, container, false);
        docDO = getArguments().getParcelable("doctorDoParc");
        /*
        //#####################################################################
        if (savedInstanceState != null){
            Log.w("entrato", "entrato");
            docDO = savedInstanceState.getParcelable("doctorDoParc");
        }
        else{
            docDO = new DoctorDO();
        }
        //#####################################################################
        */
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

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


        activity.getSupportActionBar().setTitle(R.string.edit_doc);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_doc);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity)activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity)activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                //TODO FORSE MEGLIO POPPARE DAL BACK STACK DA DOVE SI ARRIVA VISTO CHE QUI SI ARRIVA DA TRE PARTI
                Fragment fragment = DocListFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

            }
        });
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case NAME_STEP:
                view = createNameStep();
                break;
            case SURNAME_STEP:
                view = createSurnameStep();
                break;
            case EMAIL_STEP:
                view = createEmailStep();
                break;
            case ACTIVE_STEP:
                view = createActiveStep();
                break;
            case PHONE_NUMBER_STEP:
                view = createSPhoneNumberStep();
                break;
            case ADDRESS_STEP:
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

        if(docDO.getName()!=null)
            name_text.setText(docDO.getName());

        name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameSurnameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        name_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkNameSurnameStep(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return name_text;
    }

    private View createSurnameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        surname_text = new EditText(getActivity());
        surname_text.setSingleLine(true);
        surname_text.setHint("surname");
        surname_text.setInputType(InputType.TYPE_CLASS_TEXT);
        if (docDO.getSurname()!=null)
            surname_text.setText(docDO.getSurname());
        surname_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameSurnameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        surname_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkNameSurnameStep(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return surname_text;
    }

    private View createEmailStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        email_text = new EditText(getActivity());
        email_text.setSingleLine(true);
        email_text.setHint("email");
        email_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if(docDO.getEmail()!=null){
            Log.w("email", docDO.getEmail().toString());
            email_text.setText(docDO.getEmail());
        }

        email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        email_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isValidEmail(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return email_text;
    }

    private View createActiveStep() {
        //TODO convertire a checkbox o simile
        // Here we generate programmatically the view that will be added by the system to the step content layout
        active_text = new EditText(getActivity());
        active_text.setSingleLine(true);
        active_text.setHint("active");
        active_text.setInputType(InputType.TYPE_CLASS_TEXT);
        return active_text;
    }

    private View createSPhoneNumberStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        phoneNumber_text = new EditText(getActivity());
        phoneNumber_text.setSingleLine(true);
        phoneNumber_text.setHint("phone number");
        phoneNumber_text.setInputType(InputType.TYPE_CLASS_PHONE);
        if(docDO.getPhoneNumber()!=null)
            phoneNumber_text.setText(String.valueOf(docDO.getPhoneNumber().intValue()));
        phoneNumber_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidCellPhone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        phoneNumber_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isValidCellPhone(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return phoneNumber_text;
    }

    // no check su address!
    private View createAddressStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        address_text = new EditText(getActivity());
        address_text.setSingleLine(true);
        address_text.setHint("address");
        address_text.setInputType(InputType.TYPE_CLASS_TEXT);

        if(docDO.getAddress()!=null)
            address_text.setText(docDO.getAddress());

        address_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmpty(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        address_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!isEmpty(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return address_text;
    }


    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case NAME_STEP:
                checkNameSurnameStep(name_text.getText().toString());
                break;
            case SURNAME_STEP:
                checkNameSurnameStep(surname_text.getText().toString());
                break;
            case EMAIL_STEP:
                isValidEmail(email_text.getText().toString());
                break;
            case ACTIVE_STEP:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
            case PHONE_NUMBER_STEP:
                isValidCellPhone(phoneNumber_text.getText().toString());
                break;
            case ADDRESS_STEP:
                isEmpty(address_text.getText().toString());
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
        Log.w("emailtextvalue", email_text.getText().toString());
        Log.w("emailvalue", docDO.getEmail());
        // TODO FIX
        docDO.setActive(Boolean.TRUE);
        tmp = phoneNumber_text.getText().toString();
        docDO.setPhoneNumber(Double.parseDouble(tmp));
        docDO.setAddress(address_text.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //#####################################################################
                    //errore Null or empty value for key: public java.lang.String com.mysampleapp.demo.nosql.DoctorDO.getEmail()
                    mapper.save(docDO);
                } catch (final AmazonClientException ex) {
                    Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                }
            }
        }).start();

        Fragment fragment = new DocListFragment();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    // name/surname checker
    private boolean checkNameSurnameStep(String word) {
        boolean wordIsCorrect = false;

        //check if correct!!
        if(word.length() >= MIN_NAME_LENGTH && word.matches("[a-zA-Z]+")) {
            wordIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);

        } else {
            String wordErrorString;
            if(word.length() < MIN_NAME_LENGTH)
                wordErrorString = getResources().getString(R.string.error_title_min_characters);
            else
                wordErrorString = getResources().getString(R.string.error_has_numbers);

            verticalStepperForm.setActiveStepAsUncompleted(wordErrorString);
            // Equivalent to: verticalStepperForm.setStepAsUncompleted(TITLE_STEP_NUM, titleError);

        }

        return wordIsCorrect;
    }
    // email checker
    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;

        if(pattern.matcher(email).matches()){
            verticalStepperForm.setActiveStepAsCompleted();
        }
        else{
            String emailerror = getResources().getString(R.string.error_email);
            verticalStepperForm.setActiveStepAsUncompleted(emailerror);
        }
        return pattern.matcher(email).matches();
    }
    // phone number checker
    private boolean isValidCellPhone(String number) {
        Pattern pattern = Patterns.PHONE;

        if(pattern.matcher(number).matches()){
            verticalStepperForm.setActiveStepAsCompleted();
        }
        else{
            String numbererror = getResources().getString(R.string.error_phone_number);
            verticalStepperForm.setActiveStepAsUncompleted(numbererror);
        }
        return pattern.matcher(number).matches();
    }

    private boolean isEmpty(String content){
        boolean isempty = false;
        if(!content.isEmpty()){
            verticalStepperForm.setActiveStepAsCompleted();
            return isempty;
        }
        else{
            isempty = true;
            String emptycontent;
            emptycontent = getResources().getString(R.string.error_empty_content);
            verticalStepperForm.setActiveStepAsUncompleted(emptycontent);
        }
        return isempty;
    }

    // SAVING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        docDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        // Saving name field
        if(name_text != null) {
            if(!name_text.getText().toString().isEmpty())
                docDO.setName(name_text.getText().toString());
            //savedInstanceState.putString(STATE_NAME, name_text.getText().toString());
        }

        // Saving surname field
        if(surname_text != null) {
            if(!surname_text.getText().toString().isEmpty())
                docDO.setSurname(surname_text.getText().toString());
            //savedInstanceState.putString(STATE_SURNAME, surname_text.getText().toString());
        }

        // Saving email field
        if(email_text != null) {
            Log.w("emailnotnull", email_text.getText().toString());
            if(!email_text.getText().toString().isEmpty()){
                docDO.setEmail(email_text.getText().toString());
                Log.w("docdoval", docDO.getEmail().toString());
            }
            //savedInstanceState.putString(STATE_EMAIL, email_text.getText().toString());
        }
        // TODO:checkbox
        // Saving active field
        if(active_text != null) {
            //savedInstanceState.putBoolean(STATE_ACTIVE, active_text);
        }

        // Saving phone_number field
        if(phoneNumber_text != null) {
            if(!phoneNumber_text.getText().toString().isEmpty())
                docDO.setPhoneNumber(Double.parseDouble(phoneNumber_text.getText().toString()));
            //savedInstanceState.putString(STATE_PHONE_NUMBER, phoneNumber_text.getText().toString());
        }

        // Saving address field
        if(address_text != null) {
            if(!address_text.getText().toString().isEmpty())
                docDO.setAddress(address_text.getText().toString());
            //savedInstanceState.putString(STATE_ADDRESS, address_text.getText().toString());
        }

        savedInstanceState.putParcelable("doctorDoParc", docDO);
        // The call to super method must be at the end here
        super.onSaveInstanceState(savedInstanceState);
    }


    public void setDoctor(DoctorDO doctor){
        this.docDO = doctor;
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
}
