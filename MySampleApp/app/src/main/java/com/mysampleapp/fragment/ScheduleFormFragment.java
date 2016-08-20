package com.mysampleapp.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.SystemClock;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmReceiver;
import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DrugDO;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

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

    public final static String ALARM_ID_EXTRA = "alarm_id";
    public final static String DRUG_EXTRA = "drug_name";
    private OnFragmentInteractionListener mListener;
    private AppCompatActivity activity;
    private DynamoDBMapper mapper;
    DemoNoSQLOperation operation;
    DrugDO[] druglist;
    String[] drugnames;
    private ScheduleDrugDO scheduleDrugDO;
    ProgressDialog mProgressDialog;


    public static final String NEW_ALARM_ADDED = "new_alarm_added";

    // Information about the steps/fields of the form
    private static final int DRUG_STEP_NUM = 0;
    private static final int NOTES_STEP_NUM = 1;
    private static final int TIME_STEP_NUM = 2;
    private static final int DAYS_STEP_NUM = 3;

    // drug step
    private AutoCompleteTextView autoDrugTextView;
    private static final int MIN_CHARACTERS_TITLE = 3;

    // notes step
    private EditText notesEditText;

    // Time step
    private TextView timeTextView;
    private TimePickerDialog timePicker;
    private Pair<Integer, Integer> time;
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";

    // Week days step
    private String[] weekDaysString = {"L", "MA", "ME", "G", "V", "S", "D"};
    private String[] daysaved;
    String daysToSave;
    private boolean[] weekDaysBool;
    private LinearLayout daysStepContent;
    public static final String STATE_WEEK_DAYS = "week_days";

    private VerticalStepperFormLayout verticalStepperForm;

    public ScheduleFormFragment() {
        // Required empty public constructor
    }

    public static ScheduleFormFragment newInstance() {
        ScheduleFormFragment fragment = new ScheduleFormFragment();
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
        View view = inflater.inflate(R.layout.fragment_schedule_form, container, false);
        //activity = (AppCompatActivity) getActivity();
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Drug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getContext(), "ASD");

        if (savedInstanceState != null) {
            Log.w("entrato", "entrato");
            scheduleDrugDO = savedInstanceState.getParcelable("scheduleDrugDoParc");
        } else {
            if (scheduleDrugDO == null)
                scheduleDrugDO = new ScheduleDrugDO();
        }

        new MyAsyncTask(view).execute();
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.hide();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        //inizialize vertical stepper
        //initializeActivity(view);
        //new MyAsyncTask(view).execute();
        initializeActivity(view);

        activity.getSupportActionBar().setTitle(R.string.add_schedule);
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_schedule);

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((HomeActivity) activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity) activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle toolbar home button click
                Fragment fragment = ScheduleListFragment.newInstance();
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initializeActivity(View view) {

        //se trova gia salvato in savedInstanceState
        if(scheduleDrugDO.getHour()!=null && scheduleDrugDO.getHour().matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")){
            String[] hourmin = scheduleDrugDO.getHour().split(":");
            Log.w("hourmin", hourmin[0]);
            Log.w("hourmin", hourmin[1]);
            setTime(Integer.getInteger(hourmin[0]), Integer.getInteger(hourmin[1]));
            setTimePicker(Integer.getInteger(hourmin[0]), Integer.getInteger(hourmin[1]));
        }
        else{
            // Time step vars
            time = new Pair<>(8, 30);
            setTimePicker(8, 30);
        }

        // Week days step vars
        weekDaysBool = new boolean[7];

        // Vertical Stepper form vars
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        int colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        String[] stepsTitles = {"Drug", "Notes", "Hour", "Days",};
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
        //TODO SETTARE TUTTI I VALORI DI SCGEDULEDRUGDO presi dagli input form
        scheduleDrugDO.setDrug("drug");
        scheduleDrugDO.setNotes("notes");
        scheduleDrugDO.setHour(timeTextView.getText().toString());

        boolean addFirst = true;
        for(int i = 0; i < weekDaysString.length; i++){
            if(weekDaysBool[i]){
                //se è il primo elemento
                if(addFirst){
                    daysToSave = weekDaysString[i];
                    addFirst=false;
                }
                else{
                    //se siamo dopo il primo aggiungo il separatore
                    daysToSave = daysToSave + "/" + weekDaysString[i];
                }
            }
        }
        scheduleDrugDO.setDay(daysToSave);
        new SaveTask().execute();
    }


    private View addDrugStep() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View addDrugView = inflater.inflate(R.layout.drug_choice, null, false);
        addDrugView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // Get a reference to the AutoCompleteTextView in the layout
        autoDrugTextView = (AutoCompleteTextView) addDrugView.findViewById(R.id.autocomplete_drug);

        if (scheduleDrugDO.getDrug() != null)
            autoDrugTextView.setText(scheduleDrugDO.getDrug());

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

    private View createNotesStep() {
        notesEditText = new EditText(getActivity());
        notesEditText.setHint(R.string.anything_useful);
        notesEditText.setSingleLine(true);
        notesEditText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(scheduleDrugDO.getNotes()!=null)
            notesEditText.setText(scheduleDrugDO.getNotes());
        notesEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });
        return notesEditText;
    }

    private View createAlarmTimeStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout timeStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        if(scheduleDrugDO.getHour()!=null){
            timeTextView = (TextView) timeStepContent.findViewById(R.id.time);
            timeTextView.setText(scheduleDrugDO.getHour());
        }
        else
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
        if(scheduleDrugDO.getDay()!=null){
            daysToSave = scheduleDrugDO.getDay();
            daysaved = scheduleDrugDO.getDay().split("/");
        }
        int count = 0;
        for (int i = 0; i < weekDaysString.length; i++) {
            final int index = i;
            final LinearLayout dayLayout = getDayLayout(index);
            //se ho trovato giorni salvati e splittati
            if(daysaved!=null){
                if(daysaved[count].equals(weekDaysString[index])) {
                    activateDay(index, dayLayout, false);
                    count++;
                }
                else
                    deactivateDay(index, dayLayout, false);
            }
            else{
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

    private boolean checkTitleStep(String title) {
        boolean titleIsCorrect = false;

        if (title.length() >= MIN_CHARACTERS_TITLE) {
            titleIsCorrect = true;

            verticalStepperForm.setActiveStepAsCompleted();
            // Equivalent to: verticalStepperForm.setStepAsCompleted(TITLE_STEP_NUM);

        } else {
            String titleError = "ASD";

            verticalStepperForm.setActiveStepAsUncompleted(titleError);
            // Equivalent to: verticalStepperForm.setStepAsUncompleted(TITLE_STEP_NUM, titleError);

        }

        return titleIsCorrect;
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
        if (drugnames != null) {
            for (String s : drugnames) {
                if (s.equals(drugname))
                    exists = true;
            }
            if (exists)
                verticalStepperForm.setActiveStepAsCompleted();
            else {
                if (drugname.isEmpty()) {
                    String emptycontent;
                    emptycontent = getResources().getString(R.string.error_empty_content);
                    verticalStepperForm.setActiveStepAsUncompleted(emptycontent);
                } else {
                    String nodrug;
                    nodrug = getResources().getString(R.string.error_drug_not_exists);
                    verticalStepperForm.setActiveStepAsUncompleted(nodrug);
                }
            }
            return exists;
        } else {
            Log.w("drugnameNULL", "drugnameNULL");
            return exists;
        }
    }

    // SAVING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (scheduleDrugDO == null)
            scheduleDrugDO = new ScheduleDrugDO();
        scheduleDrugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

        // Saving drug field
        if (autoDrugTextView != null) {
            if (!autoDrugTextView.getText().toString().isEmpty())
                scheduleDrugDO.setDrug(autoDrugTextView.getText().toString());
        }
        // Saving notes field
        if (notesEditText != null) {
            if (!notesEditText.getText().toString().isEmpty())
                scheduleDrugDO.setNotes(notesEditText.getText().toString());
        }
        // Saving hour field
        if (timeTextView != null) {
            if (!timeTextView.getText().toString().isEmpty())
                scheduleDrugDO.setHour(timeTextView.getText().toString());
        }
        // Saving days field
        if(daysToSave != null){
            if(!daysToSave.isEmpty()){
                boolean addFirst = true;
                for(int i = 0; i < weekDaysString.length; i++){
                    if(weekDaysBool[i]){
                        //se è il primo elemento
                        if(addFirst){
                            daysToSave = weekDaysString[i];
                            addFirst=false;
                        }
                        else{
                            //se siamo dopo il primo aggiungo il separatore
                            daysToSave = daysToSave + "/" + weekDaysString[i];
                        }
                    }
                }
                scheduleDrugDO.setDay(daysToSave);
            }
        }

        savedInstanceState.putParcelable("scheduleDrugDoParc", scheduleDrugDO);
        // The call to super method must be at the end here
        super.onSaveInstanceState(savedInstanceState);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private View view;

        public MyAsyncTask(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            operation.executeOperation();
            druglist = ((DemoNoSQLTableDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
            drugnames = new String[druglist.length];
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            int n = 0;
            for (DrugDO drugDO : druglist) {
                drugnames[n] = drugDO.getName();
                Log.w("nomedrug", drugnames[n]);
                n++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, drugnames);
            autoDrugTextView.setAdapter(adapter);
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
            mProgressDialog.setCancelable(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //setting the alarmID field retriving the value from shared pref
            SharedPreferences sharedPref = getContext().getSharedPreferences(
                    getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
            // getting the last used number for the alarm id or 0 as default value for the first
            int old_alarm_id = sharedPref.getInt(getContext().getString(R.string.alarm_id), 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            int alarm_id = old_alarm_id + 1;
            Log.w("alarm id", alarm_id + "");
            editor.putInt(getString(R.string.alarm_id), alarm_id);
            editor.apply();
            scheduleDrugDO.setAlarmId(Double.parseDouble(alarm_id + ""));
            scheduleDrugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
            //save into db
            try {
                mapper.save(scheduleDrugDO);
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
                mProgressDialog.dismiss();
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                // Define our intention of executing AlertReceiver
                Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);
                int alarmID = scheduleDrugDO.getAlarmId().intValue();
                alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA, alarmID);
                alertIntent.putExtra(ScheduleFormFragment.DRUG_EXTRA, scheduleDrugDO.getDrug());
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarmID, alertIntent, PendingIntent.FLAG_ONE_SHOT);

                //TODO INVOCARE METODI CHE CALCOLA IL TEMPO PER IL PROSSIMO ALARM DA  ricordarsi di non fare cntrollo solo sul
                //giorno ma anche sull'ora dello stesso giorno
                long trigger_millis = SystemClock.elapsedRealtime() + 15 * 1000;

                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigger_millis, alarmIntent);

                //TODO DEVE DIVENTARE UNA POP
                Fragment fragment = new ScheduleListFragment();
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

            } else {
                mProgressDialog.dismiss();
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Error")
                        .setTitle("an error as occurred");

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                dialog.show();

            }
        }
    }
}
