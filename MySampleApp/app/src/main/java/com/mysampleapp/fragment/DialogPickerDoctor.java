package com.mysampleapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.mysampleapp.R;


public class DialogPickerDoctor extends DialogFragment {

    private DialogInterface.OnClickListener onClick;

    public void setOnClick(DialogInterface.OnClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] dotors={"a","b","c"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick doctor")
                .setItems(dotors,onClick);
        return builder.create();
    }
}
