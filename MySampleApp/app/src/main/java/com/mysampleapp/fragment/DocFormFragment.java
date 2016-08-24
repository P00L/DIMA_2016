package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.ArrayList;
import java.util.regex.Pattern;

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

    private static final int MIN_NAME_LENGTH = 3;
    private static final String ARG_DOCDO = "param1";
    private static final String ARG_EDITMODE = "param2";
    private static final String ARG_DOCLIST = "param3";

    private OnFragmentInteractionListener mListener;
    private ArrayList<DoctorDO> doc_list;
    private VerticalStepperFormLayout verticalStepperForm;
    private DynamoDBMapper mapper;
    private DoctorDO docDO;
    private boolean editMode = false;
    private ProgressDialog mProgressDialog;

    private static final int NAME_STEP = 0;
    private EditText name_text;
    private static final int SURNAME_STEP = 1;
    private EditText surname_text;
    private final static int EMAIL_STEP = 2;
    private EditText email_text;
    private final static int ACTIVE_STEP = 3;
    private CheckBox cbactive;
    private final static int PHONE_NUMBER_STEP = 4;
    private EditText phoneNumber_text;
    private final static int ADDRESS_STEP = 5;
    private EditText address_text;
    private final static int CONFIRM_STEP = 6;

    private AppCompatActivity activity;


    public DocFormFragment() {
        // Required empty public constructor
    }

    public static DocFormFragment newInstance(DoctorDO doctorDO,Boolean editMode,ArrayList<DoctorDO> doc_list) {
        DocFormFragment fragment = new DocFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DOCDO, doctorDO);
        args.putBoolean(ARG_EDITMODE, editMode);
        args.putParcelableArrayList(ARG_DOCLIST, doc_list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            docDO = getArguments().getParcelable(ARG_DOCDO);
            editMode = getArguments().getBoolean(ARG_EDITMODE);
            doc_list = getArguments().getParcelableArrayList(ARG_DOCLIST);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc_form, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        String[] mySteps = {"Name", "Surname", "E-mail", "Active", "Phone Number", "Address"};
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

        fab.hide();

        if (editMode) {
            activity.getSupportActionBar().setTitle(R.string.edit_doc);
            verticalStepperForm.setStepAsCompleted(NAME_STEP);
            verticalStepperForm.setStepAsCompleted(SURNAME_STEP);
            verticalStepperForm.setStepAsCompleted(EMAIL_STEP);
            verticalStepperForm.setStepAsCompleted(ACTIVE_STEP);
            verticalStepperForm.setStepAsCompleted(PHONE_NUMBER_STEP);
            verticalStepperForm.setStepAsCompleted(ADDRESS_STEP);
            verticalStepperForm.setStepAsCompleted(CONFIRM_STEP);

        } else
            activity.getSupportActionBar().setTitle(R.string.add_doc);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_doc);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_x);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                MyDialogFragment backConfirmation = new MyDialogFragment();
                backConfirmation.setOnConfirmBack(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                backConfirmation.setOnNotConfirmBack(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //discard data
                        activity.getSupportFragmentManager().popBackStack();
                    }
                });
                backConfirmation.show(activity.getSupportFragmentManager(), null);
            }
        });
    }

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
        void onFragmentInteraction(Uri uri);
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
        name_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (docDO.getName() != null)
            name_text.setText(docDO.getName());

        name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameSurnameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        name_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkNameSurnameStep(v.getText().toString())) {
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
        surname_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (docDO.getSurname() != null)
            surname_text.setText(docDO.getSurname());
        surname_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameSurnameStep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        surname_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkNameSurnameStep(v.getText().toString())) {
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
        email_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (docDO.getEmail() != null) {
            Log.w("email", docDO.getEmail().toString());
            email_text.setText(docDO.getEmail());
        }

        email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        email_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isValidEmail(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return email_text;
    }

    private View createActiveStep() {
        //TODO convertire a checkbox o simile
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.checkbox_active, null, false);

        cbactive = (CheckBox) rootView.findViewById(R.id.checkbox_active);

        if (docDO.getActive() != null) {
            if (docDO.getActive())
                cbactive.setChecked(true);
            else
                cbactive.setChecked(false);
        }


        cbactive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(getContext(), "checkato", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });
        /*
        // Here we generate programmatically the view that will be added by the system to the step content layout
        active_text = new EditText(getActivity());
        active_text.setSingleLine(true);
        active_text.setHint("active");
        active_text.setInputType(InputType.TYPE_CLASS_TEXT);
        */
        //return active_text;
        return rootView;
    }

    private View createSPhoneNumberStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        phoneNumber_text = new EditText(getActivity());
        phoneNumber_text.setSingleLine(true);
        phoneNumber_text.setHint("phone number");
        phoneNumber_text.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumber_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (docDO.getPhoneNumber() != null)
            phoneNumber_text.setText(String.valueOf(docDO.getPhoneNumber().intValue()));
        phoneNumber_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidCellPhone(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        phoneNumber_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isValidCellPhone(v.getText().toString())) {
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
        address_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (docDO.getAddress() != null)
            address_text.setText(docDO.getAddress());

        address_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmpty(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        address_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!isEmpty(v.getText().toString())) {
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
        //Log.w("emailtextvalue", email_text.getText().toString());
        //Log.w("emailvalue", docDO.getEmail().toString());
        // TODO FIX controllo
        if (cbactive.isChecked())
            docDO.setActive(Boolean.TRUE);
        else
            docDO.setActive(Boolean.FALSE);
        tmp = phoneNumber_text.getText().toString();
        docDO.setPhoneNumber(Double.parseDouble(tmp));
        docDO.setAddress(address_text.getText().toString());

        new SaveTask().execute();
    }

    // name/surname checker
    private boolean checkNameSurnameStep(String word) {
        boolean wordIsCorrect = false;

        //check if correct!!
        if (word.length() >= MIN_NAME_LENGTH && word.matches("[a-zA-Z]+")) {
            wordIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);

        } else {
            String wordErrorString;
            if (word.length() < MIN_NAME_LENGTH)
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

        if (pattern.matcher(email).matches()) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            String emailerror = getResources().getString(R.string.error_email);
            verticalStepperForm.setActiveStepAsUncompleted(emailerror);
        }
        return pattern.matcher(email).matches();
    }

    // phone number checker
    private boolean isValidCellPhone(String number) {
        Pattern pattern = Patterns.PHONE;

        if (pattern.matcher(number).matches()) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            String numbererror = getResources().getString(R.string.error_phone_number);
            verticalStepperForm.setActiveStepAsUncompleted(numbererror);
        }
        return pattern.matcher(number).matches();
    }

    private boolean isEmpty(String content) {
        boolean isempty = false;
        if (!content.isEmpty()) {
            verticalStepperForm.setActiveStepAsCompleted();
            return isempty;
        } else {
            isempty = true;
            String emptycontent;
            emptycontent = getResources().getString(R.string.error_empty_content);
            verticalStepperForm.setActiveStepAsUncompleted(emptycontent);
        }
        return isempty;
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;

        public SaveTask() {
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO NON RIMANE SULLA ROTAZIONE
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Save data");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //#####################################################################
                //errore Null or empty value for key: public java.lang.String com.mysampleapp.demo.nosql.DoctorDO.getEmail()
                mapper.save(docDO);
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
                mProgressDialog.dismiss();
                //TODO CONTROLLORA SE CONTROLLO SU EDIT MODE NON SETTARE ADD SULLA LISTA DUMMY
                doc_list.add(docDO);
                activity.getSupportFragmentManager().popBackStack();
            } else {
                mProgressDialog.dismiss();
                //TODO se qualcosa non va bisogna resettare docDO ai valori precedenti se viene premuto discard
                //o forse meglio usare un oggetto temporaneo per salvare tutto se va bene settare anche quello giusto
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Error")
                        .setTitle("an error as occurred");
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DoctorDO docDo_tmp = new DoctorDO();
        docDo_tmp.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        // Saving name field
        if (name_text != null) {
            if (!name_text.getText().toString().isEmpty())
                docDo_tmp.setName(name_text.getText().toString());
        }
        // Saving surname field
        if (surname_text != null) {
            if (!surname_text.getText().toString().isEmpty())
                docDo_tmp.setSurname(surname_text.getText().toString());
        }
        // Saving email field
        if (email_text != null) {
            Log.w("emailnotnull", email_text.getText().toString());
            if (!email_text.getText().toString().isEmpty()) {
                docDo_tmp.setEmail(email_text.getText().toString());
                Log.w("docdoval", docDo_tmp.getEmail().toString());
            }
        }
        // Saving active field
        if (cbactive != null) {
            if (cbactive.isChecked())
                docDo_tmp.setActive(true);
            else
                docDo_tmp.setActive(false);
        }
        // Saving phone_number field
        if (phoneNumber_text != null) {
            if (!phoneNumber_text.getText().toString().isEmpty())
                docDo_tmp.setPhoneNumber(Double.parseDouble(phoneNumber_text.getText().toString()));
        }
        // Saving address field
        if (address_text != null) {
            if (!address_text.getText().toString().isEmpty())
                docDo_tmp.setAddress(address_text.getText().toString());
        }

        getArguments().putParcelable(ARG_DOCDO, docDo_tmp);
        getArguments().putBoolean(ARG_EDITMODE, editMode);
        getArguments().putParcelableArrayList(ARG_DOCLIST, doc_list);
    }
}
