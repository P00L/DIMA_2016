package com.mysampleapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.mysampleapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class DialogPickerDoctor extends DialogFragment {

    private DialogInterface.OnClickListener onClick;

    public void setOnClick(DialogInterface.OnClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get shared pref file
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new sottoscorta pendig id notification to update always same notification
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getContext().getString(R.string.active_doctor),
                new HashSet<String>()));
        String[] doctors = new String[s.size()];
        String doctor_picker_order = "";
        int i = 0;
        Boolean first = true;
        for (String ss : s) {
            String[] splitted = ss.split("/");
            doctors[i] = splitted[0] + " " + splitted[1];
            if (first) {
                doctor_picker_order = ss;
                first = false;
            }
            else
                doctor_picker_order = doctor_picker_order + "//" + ss;
            i++;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getContext().getString(R.string.active_doctor_ordered), doctor_picker_order);
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick doctor")
                .setItems(doctors, onClick);
        return builder.create();
    }
}
