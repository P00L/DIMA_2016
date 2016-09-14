package com.mysampleapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.mysampleapp.R;


public class DialogPickerRefill extends DialogFragment {

    private DialogInterface.OnClickListener onClick;

    public void setOnClick(DialogInterface.OnClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] delay = getResources().getStringArray(R.array.refill);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick quantity:")
                .setItems(delay, onClick);
        return builder.create();
    }
}
