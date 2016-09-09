package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmService;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DrugDO;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.ArrayList;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFormFragment extends Fragment implements VerticalStepperForm {

    private final static String LOG_TAG = ScheduleFormFragment.class.getSimpleName();

    private static final String ARG_SCHEDULEDO = "param1";
    private static final String ARG_EDITMODE = "param2";
    private static final String ARG_SCHEDULELIST = "param3";
    private static final String ARG_DRUGLIST = "param4";
    private static final String ARG_SCHEDULEDOTMP = "param5";
    private static final String ARG_SCHEDULEDOLD = "param6";
    private static final String ARG_ASSIGNEDTMP = "param7";

    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private DynamoDBMapper mapper;
    private DemoNoSQLOperation operation;
    private ArrayList<ScheduleDrugDO> scheduleDrugDOArrayList;
    private ArrayList<DrugDO> druglist;
    private String[] drugnames;
    private ScheduleDrugDO scheduleDrugDO_tmp;
    private ScheduleDrugDO scheduleDrugDO;
    private ScheduleDrugDO scheduleDrugDO_old;
    private boolean editMode = false;
    private Boolean assigned_tmp = false;
    private ProgressDialog mProgressDialog;


    // Information about the steps/fields of the form
    private static final int DRUG_STEP_NUM = 0;
    private static final int QUANTITY_STEP_NUM = 1;
    private static final int NOTES_STEP_NUM = 2;
    private static final int TIME_STEP_NUM = 3;
    private static final int DAYS_STEP_NUM = 4;
    private static final int CONFIRM_STEP_NUM = 5;

    // drug step
    private AutoCompleteTextView autoDrugTextView;
    private static final int MIN_CHARACTERS_TITLE = 3;

    // notes step
    private Spinner qunatitySpinner;

    // notes step
    private EditText notesEditText;

    // Time step
    private TextView timeTextView;
    private TimePickerDialog timePicker;
    private Pair<Integer, Integer> time;


    // Week days step
    private String[] weekDaysString = {"L", "MA", "ME", "G", "V", "S", "D"};
    private String[] daysaved;
    String daysToSave;
    private boolean[] weekDaysBool;
    private LinearLayout daysStepContent;

    private VerticalStepperFormLayout verticalStepperForm;

    public ScheduleFormFragment() {
        // Required empty public constructor
    }

    public static ScheduleFormFragment newInstance(ScheduleDrugDO scheduleDrugDO, Boolean editMode, ArrayList<ScheduleDrugDO> scheduleDrugDOArrayList) {
        ScheduleFormFragment fragment = new ScheduleFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SCHEDULEDO, scheduleDrugDO);
        args.putBoolean(ARG_EDITMODE, editMode);
        args.putParcelableArrayList(ARG_SCHEDULELIST, scheduleDrugDOArrayList);

        ScheduleDrugDO scheduleDrugDO_duplicate = new ScheduleDrugDO();
        scheduleDrugDO_duplicate.setHour(scheduleDrugDO.getHour());
        scheduleDrugDO_duplicate.setUserId(scheduleDrugDO.getUserId());
        scheduleDrugDO_duplicate.setDay(scheduleDrugDO.getDay());
        scheduleDrugDO_duplicate.setNotes(scheduleDrugDO.getNotes());
        scheduleDrugDO_duplicate.setAlarmId(scheduleDrugDO.getAlarmId());
        scheduleDrugDO_duplicate.setQuantity(scheduleDrugDO.getQuantity());
        scheduleDrugDO_duplicate.setDrug(scheduleDrugDO.getDrug());

        args.putParcelable(ARG_SCHEDULEDOTMP, scheduleDrugDO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            scheduleDrugDO = getArguments().getParcelable(ARG_SCHEDULEDO);
            scheduleDrugDO_old = getArguments().getParcelable(ARG_SCHEDULEDOLD);
            scheduleDrugDO_tmp = getArguments().getParcelable(ARG_SCHEDULEDOTMP);
            editMode = getArguments().getBoolean(ARG_EDITMODE);
            scheduleDrugDOArrayList = getArguments().getParcelableArrayList(ARG_SCHEDULELIST);
            druglist = getArguments().getParcelableArrayList(ARG_DRUGLIST);
            assigned_tmp = getArguments().getBoolean(ARG_ASSIGNEDTMP);
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_form, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.hide();

        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Drug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "ASD");

        if (drugnames == null) {
            new MyAsyncTask().execute();
        }

        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();


        //inizialize vertical stepper
        //initializeActivity(view);
        //new MyAsyncTask(view).execute();
        initializeActivity(view);

        if (editMode) {
            if (!assigned_tmp) {
                if (scheduleDrugDO_old == null)
                    scheduleDrugDO_old = new ScheduleDrugDO();
                scheduleDrugDO_old.setHour(scheduleDrugDO_tmp.getHour());
                scheduleDrugDO_old.setUserId(scheduleDrugDO_tmp.getUserId());
                scheduleDrugDO_old.setDay(scheduleDrugDO_tmp.getDay());
                scheduleDrugDO_old.setNotes(scheduleDrugDO_tmp.getNotes());
                scheduleDrugDO_old.setAlarmId(scheduleDrugDO_tmp.getAlarmId());
                scheduleDrugDO_old.setQuantity(scheduleDrugDO_tmp.getQuantity());
                scheduleDrugDO_old.setDrug(scheduleDrugDO_tmp.getDrug());
                assigned_tmp = true;
            }
            activity.getSupportActionBar().setTitle(R.string.edit_schedule_drug);
            verticalStepperForm.setStepAsCompleted(DRUG_STEP_NUM);
            verticalStepperForm.setStepAsCompleted(QUANTITY_STEP_NUM);
            verticalStepperForm.setStepAsCompleted(NOTES_STEP_NUM);
            verticalStepperForm.setStepAsCompleted(TIME_STEP_NUM);
            verticalStepperForm.setStepAsCompleted(DAYS_STEP_NUM);
            verticalStepperForm.setStepAsCompleted(CONFIRM_STEP_NUM);
        } else
            activity.getSupportActionBar().setTitle(R.string.add_schedule);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_schedule);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.x);
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void initializeActivity(View view) {
        //se trova gia salvato in savedInstanceState
        if (scheduleDrugDO_tmp.getHour() != null && scheduleDrugDO_tmp.getHour().matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")) {
            String[] hourmin = scheduleDrugDO_tmp.getHour().split(":");
            Log.w("hourmin", hourmin[0]);
            Log.w("hourmin", hourmin[1]);
            //setTime(Integer.getInteger(hourmin[0]), Integer.getInteger(hourmin[1]));
            setTimePicker(Integer.parseInt(hourmin[0]), Integer.parseInt(hourmin[1]));
        } else {
            // Time step vars
            time = new Pair<>(8, 30);
            setTimePicker(8, 30);
        }

        // Week days step vars
        weekDaysBool = new boolean[7];

        // Vertical Stepper form vars
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        int colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        String[] stepsTitles = {"Drug", "Quantity", "Notes", "Hour", "Days",};
        //String[] stepsSubtitles = getResources().getStringArray(R.array.steps_subtitles);

        // Here we find and initialize the form
        verticalStepperForm = (VerticalStepperFormLayout) view.findViewById(R.id.vertical_stepper_form);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, stepsTitles, this, getActivity())
                //.stepsSubtitles(stepsSubtitles)
                //.materialDesignInDisabledSteps(true) // false by default
                //.showVerticalLineWhenStepsAreCollapsed(true) // false by default
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .init();

    }

    // METHODS THAT HAVE TO BE IMPLEMENTED TO MAKE THE LIBRARY WORK
    // (Implementation of the interface "VerticalStepperForm")

    @Override
    public View createStepContentView(int stepNumber) {
        // Here we generate the content view of the correspondent step and we return it so it gets
        // automatically added to the step layout (AKA stepContent)
        View view = null;
        switch (stepNumber) {
            case DRUG_STEP_NUM:
                view = addDrugStep();
                break;
            case QUANTITY_STEP_NUM:
                view = createQuantityStep();
                break;
            case NOTES_STEP_NUM:
                view = createNotesStep();
                break;
            case TIME_STEP_NUM:
                view = createAlarmTimeStep();
                break;
            case DAYS_STEP_NUM:
                view = createAlarmDaysStep();
                break;
        }
        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case DRUG_STEP_NUM:
                checkIfExists(autoDrugTextView.getText().toString());
                break;
            case QUANTITY_STEP_NUM:
                verticalStepperForm.setStepAsCompleted(stepNumber);
                break;
            case NOTES_STEP_NUM:
                verticalStepperForm.setStepAsCompleted(stepNumber);
                break;
            case TIME_STEP_NUM:
                // As soon as they are open, these two steps are marked as completed because they
                // have default values
                verticalStepperForm.setStepAsCompleted(stepNumber);
                // In this case, the instruction above is equivalent to:
                // verticalStepperForm.setActiveStepAsCompleted();
                break;
            case DAYS_STEP_NUM:
                // When this step is open, we check the days to verify that at least one is selected
                checkDays();
                //verticalStepperForm.setStepAsCompleted(stepNumber);
                break;
        }
    }

    @Override
    public void sendData() {
        scheduleDrugDO_tmp.setDrug(autoDrugTextView.getText().toString());
        scheduleDrugDO_tmp.setNotes(notesEditText.getText().toString());
        scheduleDrugDO_tmp.setHour(timeTextView.getText().toString());

        switch (qunatitySpinner.getSelectedItemPosition()) {
            case 0:
                scheduleDrugDO_tmp.setQuantity(1.0);
                break;
            case 1:
                scheduleDrugDO_tmp.setQuantity(0.5);
                break;
            case 2:
                scheduleDrugDO_tmp.setQuantity(0.25);
                break;
            case 3:
                scheduleDrugDO_tmp.setQuantity(2.0);
                break;

        }

        boolean addFirst = true;
        for (int i = 0; i < weekDaysString.length; i++) {
            if (weekDaysBool[i]) {
                //se è il primo elemento
                if (addFirst) {
                    daysToSave = weekDaysString[i];
                    addFirst = false;
                } else {
                    //se siamo dopo il primo aggiungo il separatore
                    daysToSave = daysToSave + "/" + weekDaysString[i];
                }
            }
        }
        scheduleDrugDO_tmp.setDay(daysToSave);
        new SaveTask().execute();
    }


    private View addDrugStep() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View addDrugView = inflater.inflate(R.layout.drug_choice, null, false);
        addDrugView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // Get a reference to the AutoCompleteTextView in the layout
        autoDrugTextView = (AutoCompleteTextView) addDrugView.findViewById(R.id.autocomplete_drug);
        //set threshold of number of letter to show up to display
        autoDrugTextView.setThreshold(1);
        FloatingActionButton fab2 = (FloatingActionButton) addDrugView.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = DrugFormFragment.newInstance(new DrugDO(), false, druglist);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        if (scheduleDrugDO_tmp.getDrug() != null)
            autoDrugTextView.setText(scheduleDrugDO_tmp.getDrug());

        if (druglist != null) {
            drugnames = new String[druglist.size()];
            int n = 0;
            for (DrugDO drugDO : druglist) {
                drugnames[n] = drugDO.getName();
                n++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, drugnames);
            autoDrugTextView.setAdapter(adapter);
        }

        autoDrugTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfExists(autoDrugTextView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        autoDrugTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (checkIfExists(autoDrugTextView.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return addDrugView;
    }

    private View createQuantityStep() {
        qunatitySpinner = new Spinner(getActivity());
        qunatitySpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ArrayList<String> quantity_hint = new ArrayList<>();
        quantity_hint.add("1");
        quantity_hint.add("1/2");
        quantity_hint.add("1/4");
        quantity_hint.add("2");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, quantity_hint);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        qunatitySpinner.setAdapter(adapter);
        if (scheduleDrugDO_tmp.getQuantity() != null) {
            Log.w(LOG_TAG, "sottoscorta " + scheduleDrugDO.getQuantity().toString());
            switch (scheduleDrugDO.getQuantity().toString()) {
                case "1":
                    qunatitySpinner.setSelection(0);
                    break;
                case "0.5":
                    qunatitySpinner.setSelection(1);
                    break;
                case "0.25":
                    qunatitySpinner.setSelection(2);
                    break;
                case "2":
                    qunatitySpinner.setSelection(3);
                    break;
            }
        }

        return qunatitySpinner;
    }

    private View createNotesStep() {
        notesEditText = new EditText(getActivity());
        notesEditText.setHint(R.string.anything_useful);
        notesEditText = new EditText(getActivity());
        notesEditText.setHint("notes");
        notesEditText.setSingleLine(false);
        notesEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        notesEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        notesEditText.setLines(5);
        notesEditText.setMaxLines(20);
        notesEditText.setVerticalScrollBarEnabled(true);
        notesEditText.setMovementMethod(ScrollingMovementMethod.getInstance());
        notesEditText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        notesEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (scheduleDrugDO_tmp.getNotes() != null)
            notesEditText.setText(scheduleDrugDO_tmp.getNotes());
        notesEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        notesEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        return notesEditText;
    }

    private View createAlarmTimeStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout timeStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        if (scheduleDrugDO_tmp.getHour() != null) {
            timeTextView = (TextView) timeStepContent.findViewById(R.id.time);
            timeTextView.setText(scheduleDrugDO_tmp.getHour());
        } else
            timeTextView = (TextView) timeStepContent.findViewById(R.id.time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });
        return timeStepContent;
    }

    private View createAlarmDaysStep() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        daysStepContent = (LinearLayout) inflater.inflate(
                R.layout.step_days_of_week_layout, null, false);

        //controllo che non abbia una instance salvata
        if (scheduleDrugDO_tmp.getDay() != null) {
            daysToSave = scheduleDrugDO_tmp.getDay();
            daysaved = scheduleDrugDO_tmp.getDay().split("/");
        }

        int count = 0;
        for (int i = 0; i < weekDaysString.length; i++) {
            final int index = i;
            final LinearLayout dayLayout = getDayLayout(index);
            //se ho trovato giorni salvati e splittati
            if (daysaved != null) {
                if (count < daysaved.length) {
                    if (daysaved[count].equals(weekDaysString[index])) {
                        activateDay(index, dayLayout, false);
                        count++;
                    } else
                        deactivateDay(index, dayLayout, false);
                } else {
                    deactivateDay(index, dayLayout, false);
                }
            } else {
                if (index < 5) {
                    activateDay(index, dayLayout, false);
                } else {
                    deactivateDay(index, dayLayout, false);
                }
            }

            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((boolean) v.getTag()) {
                        deactivateDay(index, dayLayout, true);
                    } else {
                        activateDay(index, dayLayout, true);
                    }
                }
            });

            final TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
            dayText.setText(weekDaysString[index]);
        }
        return daysStepContent;
    }

    private void setTimePicker(int hour, int minutes) {
        timePicker = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, hour, minutes, true);
    }

    private void setTime(int hour, int minutes) {
        time = new Pair<>(hour, minutes);
        String hourString = ((time.first > 9) ?
                String.valueOf(time.first) : ("0" + time.first));
        String minutesString = ((time.second > 9) ?
                String.valueOf(time.second) : ("0" + time.second));
        String time = hourString + ":" + minutesString;
        timeTextView.setText(time);
    }

    private void activateDay(int index, LinearLayout dayLayout, boolean check) {
        weekDaysBool[index] = true;

        dayLayout.setTag(true);

        Drawable bg = ContextCompat.getDrawable(getContext(),
                ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
        dayText.setTextColor(Color.rgb(255, 255, 255));

        if (check) {
            checkDays();
        }
    }

    private void deactivateDay(int index, LinearLayout dayLayout, boolean check) {
        weekDaysBool[index] = false;

        dayLayout.setTag(false);

        dayLayout.setBackgroundResource(0);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
        int colour = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        dayText.setTextColor(colour);

        if (check) {
            checkDays();
        }
    }

    private boolean checkDays() {
        boolean thereIsAtLeastOneDaySelected = false;
        for (int i = 0; i < weekDaysBool.length && !thereIsAtLeastOneDaySelected; i++) {
            if (weekDaysBool[i]) {
                verticalStepperForm.setStepAsCompleted(DAYS_STEP_NUM);
                thereIsAtLeastOneDaySelected = true;
            }
        }
        if (!thereIsAtLeastOneDaySelected) {
            String atleastone = getResources().getString(R.string.at_least_one);
            verticalStepperForm.setActiveStepAsUncompleted(atleastone);
        }

        return thereIsAtLeastOneDaySelected;
    }

    private LinearLayout getDayLayout(int i) {
        int id = daysStepContent.getResources().getIdentifier(
                "day_" + i, "id", getContext().getPackageName());
        return (LinearLayout) daysStepContent.findViewById(id);
    }

    private boolean checkIfExists(String drugname) {
        boolean exists = false;
        if (drugnames != null && drugnames.length > 0 && drugname != null) {
            for (String s : drugnames) {
                if (s != null)
                    if (s.equals(drugname))
                        exists = true;
            }
            if (exists) {
                autoDrugTextView.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                verticalStepperForm.setActiveStepAsCompleted();
            }
            else {
                if (drugname.isEmpty()) {
                    String emptycontent;
                    emptycontent = getResources().getString(R.string.error_empty_content);
                    autoDrugTextView.getBackground().mutate().setColorFilter(getResources().getColor(R.color.input_error_color), PorterDuff.Mode.SRC_ATOP);
                    verticalStepperForm.setActiveStepAsUncompleted(emptycontent);
                } else {
                    String nodrug;
                    nodrug = getResources().getString(R.string.error_drug_not_exists);
                    autoDrugTextView.getBackground().mutate().setColorFilter(getResources().getColor(R.color.input_error_color), PorterDuff.Mode.SRC_ATOP);
                    verticalStepperForm.setActiveStepAsUncompleted(nodrug);
                }
            }
            return exists;
        } else {
            Log.w("drugnameNULL", "drugnameNULL");
            return exists;
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {


        public MyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            operation.executeOperation();
            druglist = ((DemoNoSQLTableDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
            drugnames = new String[druglist.size()];
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            int n = 0;
            for (DrugDO drugDO : druglist) {
                drugnames[n] = drugDO.getName();
                n++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, drugnames);
            autoDrugTextView.setAdapter(adapter);
            checkIfExists(autoDrugTextView.getText().toString());
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;

        public SaveTask() {
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            //if it isn't edit mode we have to generate a new alarm id otherwise we keep using the old one
            if (!editMode) {
                Log.w("ScheduleFormFragment", "generate new alarm id");
                //setting the alarmID field retriving the value from shared pref
                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
                // getting the last used number for the alarm id or 0 as default value for the first
                int old_alarm_id = sharedPref.getInt(getContext().getString(R.string.alarm_id), 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                int alarm_id = old_alarm_id + 1;
                Log.w("ScheduleFormFragment", "alarm id " + alarm_id);
                editor.putInt(getString(R.string.alarm_id), alarm_id);
                editor.apply();
                scheduleDrugDO_tmp.setAlarmId(Double.parseDouble(alarm_id + ""));
            }
            Log.w("ScheduleFormFragment", "alarm id " + scheduleDrugDO_tmp.getAlarmId().intValue());
            scheduleDrugDO_tmp.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
            //save into db
            try {
                if (editMode) {
                    mapper.delete(scheduleDrugDO_old);
                }
                mapper.save(scheduleDrugDO_tmp);
                success = true;
            } catch (final AmazonClientException ex) {
                Log.e("ScheduleFormFragment", "Failed saving item : " + ex.getMessage(), ex);
                success = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //handle success or fail of db insertion
            if (success) {

                scheduleDrugDO.setHour(scheduleDrugDO_tmp.getHour());
                scheduleDrugDO.setUserId(scheduleDrugDO_tmp.getUserId());
                scheduleDrugDO.setDay(scheduleDrugDO_tmp.getDay());
                scheduleDrugDO.setNotes(scheduleDrugDO_tmp.getNotes());
                scheduleDrugDO.setAlarmId(scheduleDrugDO_tmp.getAlarmId());
                scheduleDrugDO.setQuantity(scheduleDrugDO_tmp.getQuantity());
                scheduleDrugDO.setDrug(scheduleDrugDO_tmp.getDrug());
                mProgressDialog.dismiss();

                //start service to keep update or set alarm
                Intent i = new Intent(getContext(), AlarmService.class);
                i.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO_tmp);
                if (editMode) {
                    i.putExtra(AlarmService.ACTION_EXTRA, "update");
                    i.putExtra(AlarmService.SCHEDULE_OLD_EXTRA, scheduleDrugDO_old);
                } else
                    i.putExtra(AlarmService.ACTION_EXTRA, "set");
                getContext().startService(i);

                scheduleDrugDOArrayList.add(scheduleDrugDO_tmp);
                activity.getSupportFragmentManager().popBackStack();

            } else {
                mProgressDialog.dismiss();
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

        scheduleDrugDO_tmp.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

        // Saving drug field
        if (autoDrugTextView != null) {
            if (!autoDrugTextView.getText().toString().isEmpty())
                scheduleDrugDO_tmp.setDrug(autoDrugTextView.getText().toString());
        }
        // Saving notes field
        if (notesEditText != null) {
            if (!notesEditText.getText().toString().isEmpty())
                scheduleDrugDO_tmp.setNotes(notesEditText.getText().toString());
        }
        // Saving sottoscorta field
        if (qunatitySpinner != null) {
            switch (qunatitySpinner.getSelectedItemPosition()) {
                case 0:
                    scheduleDrugDO_tmp.setQuantity(1.0);
                    break;
                case 1:
                    scheduleDrugDO_tmp.setQuantity(0.5);
                    break;
                case 2:
                    scheduleDrugDO_tmp.setQuantity(0.25);
                    break;
                case 3:
                    scheduleDrugDO_tmp.setQuantity(2.0);
                    break;

            }
        }
        // Saving hour field
        if (timeTextView != null) {
            if (!timeTextView.getText().toString().isEmpty())
                scheduleDrugDO_tmp.setHour(timeTextView.getText().toString());
        }
        // Saving days field
        if (weekDaysBool != null) {
            boolean addFirst = true;
            for (int i = 0; i < weekDaysString.length; i++) {
                if (weekDaysBool[i]) {
                    //se è il primo elemento
                    if (addFirst) {
                        daysToSave = weekDaysString[i];
                        addFirst = false;
                    } else {
                        //se siamo dopo il primo aggiungo il separatore
                        daysToSave = daysToSave + "/" + weekDaysString[i];
                    }
                }
            }
            scheduleDrugDO_tmp.setDay(daysToSave);
        }

        getArguments().putParcelable(ARG_SCHEDULEDO, scheduleDrugDO);
        getArguments().putParcelable(ARG_SCHEDULEDOLD, scheduleDrugDO_old);
        getArguments().putParcelable(ARG_ASSIGNEDTMP, scheduleDrugDO_tmp);
        getArguments().putBoolean(ARG_EDITMODE, editMode);
        getArguments().putBoolean(ARG_ASSIGNEDTMP, assigned_tmp);
        getArguments().putParcelableArrayList(ARG_DRUGLIST, druglist);
        getArguments().putParcelableArrayList(ARG_SCHEDULELIST, scheduleDrugDOArrayList);
    }
}
