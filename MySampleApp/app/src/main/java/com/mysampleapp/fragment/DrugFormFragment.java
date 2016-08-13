package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Gravity;
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

    private final static int NAME_STEP = 0;
    private EditText name_text;
    private final static int TYPE_STEP = 1;
    private EditText type_text;
    private final static int QTY_STEP = 2;
    private EditText qty_text;
    private final static int WEIGHT_STEP = 3;
    private EditText weight_text;
    private final static int MINQTY_STEP = 4;
    private EditText minqty_text;
    private final static int NOTES_STEP = 5;
    private EditText notes_text;
    private AppCompatActivity activity;


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
        //#####################################################################
        if (savedInstanceState != null){
            Log.w("entrato", "entrato");
            drugDO = savedInstanceState.getParcelable("drugDoParc");
        }
        else{
            drugDO = new DrugDO();
        }
        //#####################################################################
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);

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

        if (fab.isShown())
            fab.hide();

        activity.getSupportActionBar().setTitle(R.string.add_drug);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.drug_menu);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity)activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity)activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                //TODO FORSE MEGLIO POPPARE DAL BACK STACK DA DOVE SI ARRIVA VISTO CHE QUI SI ARRIVA DA TRE PARTI
                Fragment fragment = DrugListFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

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
            case NAME_STEP:
                view = createNameStep();
                break;
            case TYPE_STEP:
                view = createTypeStep();
                break;
            case QTY_STEP:
                view = createQuantityStep();
                break;
            case WEIGHT_STEP:
                view = createWeightStep();
                break;
            case MINQTY_STEP:
                view = createSottoscortaStep();
                break;
            case NOTES_STEP:
                view = createNotesStep();
                break;
        }
        return view;
    }

    //no check
    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        name_text = new EditText(getActivity());
        name_text.setSingleLine(true);
        name_text.setHint("name");
        name_text.setInputType(InputType.TYPE_CLASS_TEXT);
        if(drugDO.getName()!=null)
            name_text.setText(drugDO.getName());

        name_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmpty(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        name_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!isEmpty(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return name_text;
    }

    //check no numbers
    private View createTypeStep() {
        // In this case we generate the view by inflating a XML file
        type_text = new EditText(getActivity());
        type_text.setSingleLine(true);
        type_text.setHint("type");
        type_text.setInputType(InputType.TYPE_CLASS_TEXT);
        if(drugDO.getType()!=null)
            type_text.setText(drugDO.getType());
        type_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasNoNumbers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        name_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(hasNoNumbers(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return type_text;
    }

    private View createQuantityStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        qty_text = new EditText(getActivity());
        qty_text.setSingleLine(true);
        qty_text.setHint("quantity");
        qty_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(drugDO.getQuantity()!=null)
            qty_text.setText(String.valueOf(drugDO.getQuantity().intValue()));
        qty_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidQtyWeigh(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        qty_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isValidQtyWeigh(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return qty_text;
    }

    private View createWeightStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        weight_text = new EditText(getActivity());
        weight_text.setSingleLine(true);
        weight_text.setHint("weight");
        weight_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(drugDO.getWeight()!=null)
            weight_text.setText(String.valueOf(drugDO.getWeight().intValue()));
        weight_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidQtyWeigh(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        weight_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isValidQtyWeigh(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return weight_text;
    }

    private View createSottoscortaStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        minqty_text = new EditText(getActivity());
        minqty_text.setSingleLine(true);
        minqty_text.setHint("sottoscorta");
        minqty_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(drugDO.getMinqty()!=null)
            minqty_text.setText(String.valueOf(drugDO.getMinqty().intValue()));
        minqty_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidQtyWeigh(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        minqty_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isValidQtyWeigh(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });
        return minqty_text;
    }

    private View createNotesStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        notes_text = new EditText(getActivity());
        notes_text.setHint("notes");
        notes_text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        notes_text.setMinLines(3);
        notes_text.setMaxLines(7);
        notes_text.setLines(5);
        notes_text.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        if(drugDO.getNotes()!=null)
            notes_text.setText(drugDO.getNotes().toString());
        return notes_text;
    }


    @Override
    public void onStepOpening(int stepNumber) {
        //TODO aggiungere i controlli sui campi
        switch (stepNumber) {
            case NAME_STEP:
                isEmpty(name_text.getText().toString());
                break;
            case TYPE_STEP:
                hasNoNumbers(type_text.getText().toString());
                break;
            case QTY_STEP:
                isValidQtyWeigh(qty_text.getText().toString());
                break;
            case WEIGHT_STEP:
                isValidQtyWeigh(weight_text.getText().toString());
                break;
            case MINQTY_STEP:
                isValidQtyWeigh(minqty_text.getText().toString());
                break;
            case NOTES_STEP:
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

    }

    public boolean isValidQtyWeigh(String number){
        boolean onlynumbers = false;

        if(number.matches("[0-9]+")){
            onlynumbers = true;
            verticalStepperForm.setActiveStepAsCompleted();
        }
        else{
            String numErrorString;
            numErrorString = getResources().getString(R.string.error_not_a_number);
            verticalStepperForm.setActiveStepAsUncompleted(numErrorString);
        }
        return onlynumbers;
    }

    public boolean hasNoNumbers(String type){
        boolean typeIsCorrect = false;

        //check if correct!!
        if(type.matches("[a-zA-Z]+")) {
            typeIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);

        } else {
            String typeErrorString;
            typeErrorString = getResources().getString(R.string.error_has_numbers);

            verticalStepperForm.setActiveStepAsUncompleted(typeErrorString);
            // Equivalent to: verticalStepperForm.setStepAsUncompleted(TITLE_STEP_NUM, titleError);

        }
        return typeIsCorrect;
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        drugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        // Saving name field
        if(name_text != null) {
            if(!name_text.getText().toString().isEmpty())
                drugDO.setName(name_text.getText().toString());
        }

        // Saving type field
        if(type_text != null) {
            if(!type_text.getText().toString().isEmpty())
                drugDO.setType(type_text.getText().toString());
        }

        // Saving quantity field
        if(qty_text != null) {
            if(!qty_text.getText().toString().isEmpty())
                drugDO.setQuantity(Double.parseDouble(qty_text.getText().toString()));
        }
        // Saving weight field
        if(weight_text != null) {
            if(!qty_text.getText().toString().isEmpty())
                drugDO.setWeight(Double.parseDouble(weight_text.getText().toString()));
        }

        // Saving sottoscorta field
        if(minqty_text != null) {
            if(!minqty_text.getText().toString().isEmpty())
                drugDO.setMinqty(Double.parseDouble(minqty_text.getText().toString()));
        }

        // Saving notes field
        if(notes_text != null) {
            if(!notes_text.getText().toString().isEmpty())
               drugDO.setNotes(notes_text.getText().toString());
        }

        savedInstanceState.putParcelable("drugDoParc", drugDO);
        // The call to super method must be at the end here
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setDrug(DrugDO drug){
        this.drugDO = drug;
    }
}
