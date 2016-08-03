package com.mysampleapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.DemoNoSQLDoctorResult;
import com.mysampleapp.demo.nosql.DemoNoSQLResult;
import com.mysampleapp.demo.nosql.DoctorDO;


public class DocFragment extends Fragment {

    private DemoNoSQLResult result;

    private AppCompatActivity activity;

    public DocFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DocFragment newInstance() {
        DocFragment fragment = new DocFragment();
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
        View view = inflater.inflate(R.layout.fragment_doc, container, false);

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (AppCompatActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);
        // TODO na io lo userei per la modifica con la matitina
        if (fab.isShown())
            fab.hide();

        activity.getSupportActionBar().setTitle(R.string.doctor);

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.doc_menu);

        DoctorDO doctorDO = ((DemoNoSQLDoctorResult)result).getResult();

        TextView textView = (TextView) view.findViewById(R.id.doc_name);
        textView.setText(doctorDO.getName());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setResult(final DemoNoSQLResult result) {
        this.result = result;
    }
}
