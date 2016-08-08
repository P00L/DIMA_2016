package com.mysampleapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mysampleapp.R;
import com.mysampleapp.adapter.DocAdapter;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDoctor;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DoctorDO;

public class DocListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DoctorDO[] items;
    private AppCompatActivity activity;
    ProgressDialog mProgressDialog;
    DemoNoSQLOperation operation;

    public DocListFragment() {
        // Required empty public constructor
    }

    public static DocListFragment newInstance() {
        DocListFragment fragment = new DocListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc_list, container, false);
        activity = (AppCompatActivity) getActivity();
        DemoNoSQLTableBase demoTable= DemoNoSQLTableFactory.instance(getContext())
                .getNoSQLTableByTableName("Doctor");
        operation = (DemoNoSQLOperation)demoTable.getOperationByName(getContext(),"ASD");

        new MyAsyncTask().execute();

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        // on click fab launch form doc
        FloatingActionButton fab = (FloatingActionButton)  activity.findViewById(R.id.fab);
        if (!fab.isShown())
            fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = DocFormFragment.newInstance();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        // set action bar title
        activity.getSupportActionBar().setTitle(R.string.doctors);
        // set nav menu item checked
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.doc_menu);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
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

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        public MyAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("We are working for you");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            operation.executeOperation();
            items = ((DemoNoSQLTableDoctor.DemoQueryWithPartitionKeyOnly)operation).getResultArray();
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {

            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new DocAdapter(getContext(), items);
            mRecyclerView.setAdapter(mAdapter);
            mProgressDialog.dismiss();
        }
    }
}
