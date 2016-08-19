package com.mysampleapp.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.Toast;

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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    private boolean[] weekDays;
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
        DemoNoSQLTableBase demoTable= DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Drug");
        operation = (DemoNoSQLOperation)demoTable.getOperationByName(getContext(),"ASD");

        if (savedInstanceState != null){
            Log.w("entrato", "entrato");
            scheduleDrugDO = savedInstanceState.getParcelable("scheduleDrugDoParc");
        }
        else{
            if(scheduleDrugDO == null)
                scheduleDrugDO = new ScheduleDrugDO();
        }

        new MyAsyncTask(view).execute();
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);
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
        ((HomeActivity)activity).getToggle().setHomeAsUpIndicator(R.drawable.ic_action_prev);
        ((HomeActivity)activity).getToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
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

        // Time step vars
        time = new Pair<>(8, 30);
        setTimePicker(8, 30);

        // Week days step vars
        weekDays = new boolean[7];

        // Vertical Stepper form vars
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        int colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        String[] stepsTitles =  {"Drug", "Notes", "Hour", "Days",};
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
        executeDataSending();
    }


    //TODO UNICA COSA DA TENERE
    private void executeDataSending() {

        //get shared pref file
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        // getting the last used number for the alarm id or 0 as default value for the first
        int old_alarm_id = sharedPref.getInt(getContext().getString(R.string.alarm_id), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        final int alarm_id = old_alarm_id + 1;
        Log.w("alarm id",alarm_id+"");
        editor.putInt(getString(R.string.alarm_id), alarm_id);
        editor.apply();
        final String drugName = "drug"+alarm_id;

        scheduleDrugDO = new ScheduleDrugDO();
        scheduleDrugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        scheduleDrugDO.setNotes("notes"+alarm_id);
        scheduleDrugDO.setDrug(drugName);
        Set<String> day_list = new HashSet<String>();
        day_list.add("1");
        day_list.add("2");
        day_list.add("3");
        day_list.add("4");
        day_list.add("5");

        scheduleDrugDO.setDay(day_list);
        Set<Double> hour_list = new HashSet<Double>();
        Random r = new Random();
        hour_list.add(r.nextDouble());
        hour_list.add(r.nextDouble());
        scheduleDrugDO.setHour(hour_list);
        scheduleDrugDO.setAlarmId(Double.parseDouble(alarm_id+""));

        //TODO fare la save poi se va a buon fine far partire l'alarm
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //#####################################################################
                    //errore Null or empty value for key: public java.lang.String com.mysampleapp.demo.nosql.DoctorDO.getEmail()
                    mapper.save(scheduleDrugDO);
                } catch (final AmazonClientException ex) {
                    Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                }

                //TODO UNA VOLTA CHE E' SALVATO NEL DB POSSIAMO SETTARE L'ALARM MANAGER IN UN BACKGROUND TASK
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                // Define our intention of executing AlertReceiver
                Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);

                alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA, alarm_id);
                alertIntent.putExtra(ScheduleFormFragment.DRUG_EXTRA, drugName);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), alarm_id, alertIntent, PendingIntent.FLAG_ONE_SHOT);

                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                15 * 1000, alarmIntent);

                Fragment fragment = new ScheduleListFragment();
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();


            }
        }).start();
    }


    private View addDrugStep() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View addDrugView = inflater.inflate(R.layout.drug_choice, null ,false);
        addDrugView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        // Get a reference to the AutoCompleteTextView in the layout
        autoDrugTextView = (AutoCompleteTextView) addDrugView.findViewById(R.id.autocomplete_drug);

        if(scheduleDrugDO.getDrug()!=null)
            autoDrugTextView.setText(scheduleDrugDO.getDrug());

        autoDrugTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfExists(autoDrugTextView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        autoDrugTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkIfExists(autoDrugTextView.getText().toString())) {
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

        String[] weekDays = {"L","M","M","G","V","S","D"};
        for(int i = 0; i < weekDays.length; i++) {
            final int index = i;
            final LinearLayout dayLayout = getDayLayout(index);
            if(index < 5) {
                activateDay(index, dayLayout, false);
            } else {
                deactivateDay(index, dayLayout, false);
            }

            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((boolean)v.getTag()) {
                        deactivateDay(index, dayLayout, true);
                    } else {
                        activateDay(index, dayLayout, true);
                    }
                }
            });

            final TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
            dayText.setText(weekDays[index]);
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

        if(title.length() >= MIN_CHARACTERS_TITLE) {
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
        weekDays[index] = true;

        dayLayout.setTag(true);

        Drawable bg = ContextCompat.getDrawable(getContext(),
                ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
        dayText.setTextColor(Color.rgb(255, 255, 255));

        if(check) {
            checkDays();
        }
    }

    private void deactivateDay(int index, LinearLayout dayLayout, boolean check) {
        weekDays[index] = false;

        dayLayout.setTag(false);

        dayLayout.setBackgroundResource(0);

        TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
        int colour = ContextCompat.getColor(getContext(), R.color.com_facebook_button_send_background_color);
        dayText.setTextColor(colour);

        if(check) {
            checkDays();
        }
    }

    private boolean checkDays() {
        boolean thereIsAtLeastOneDaySelected = false;
        for(int i = 0; i < weekDays.length && !thereIsAtLeastOneDaySelected; i++) {
            if(weekDays[i]) {
                verticalStepperForm.setStepAsCompleted(DAYS_STEP_NUM);
                thereIsAtLeastOneDaySelected = true;
            }
        }
        if(!thereIsAtLeastOneDaySelected) {
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

    private boolean checkIfExists(String drugname){
        boolean exists = false;
        if(drugnames!=null){
            for (String s : drugnames) {
                if(s.equals(drugname))
                    exists = true;
            }
            if(exists)
                verticalStepperForm.setActiveStepAsCompleted();
            else {
                if(drugname.isEmpty()){
                    String emptycontent;
                    emptycontent = getResources().getString(R.string.error_empty_content);
                    verticalStepperForm.setActiveStepAsUncompleted(emptycontent);
                }
                else{
                    String nodrug;
                    nodrug = getResources().getString(R.string.error_drug_not_exists);
                    verticalStepperForm.setActiveStepAsUncompleted(nodrug);
                }
            }
            return exists;
        }
        else{
            Log.w("drugnameNULL", "drugnameNULL");
            return exists;
        }
    }

    // SAVING THE STATE

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if(scheduleDrugDO == null)
            scheduleDrugDO = new ScheduleDrugDO();
        scheduleDrugDO.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());

        // Saving drug field
        if(autoDrugTextView != null) {
            if(!autoDrugTextView.getText().toString().isEmpty())
                scheduleDrugDO.setDrug(autoDrugTextView.getText().toString());
        }
        // Saving notes field
        if(notesEditText != null) {
            if(!notesEditText.getText().toString().isEmpty())
                scheduleDrugDO.setNotes(notesEditText.getText().toString());
        }
        // Saving hour field --- Set è una collezione, utilizza metodo add, conviene usare String
        // ed eventualmente splittare sul divisore
        if(timeTextView != null){
            if(!timeTextView.getText().toString().isEmpty())
                Log.w("cambiare", "cambiare");
            // scheduleDrugDO.setHour();

        }
        // Saving days field

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
            druglist = ((DemoNoSQLTableDrug.DemoQueryWithPartitionKeyOnly)operation).getResultArray();
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
            checkIfExists("");
        }
    }
}
