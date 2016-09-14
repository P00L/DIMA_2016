package com.mysampleapp.nosqldb.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.nosqldb.models.DoctorDO;

import java.util.Set;

public class DemoNoSQLDoctorResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final DoctorDO result;

    public DemoNoSQLDoctorResult(final DoctorDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        // TODO fixare con nostro layout
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView emailKeyTextView;
        final TextView emailValueTextView;
        final TextView activeKeyTextView;
        final TextView activeValueTextView;
        final TextView addressKeyTextView;
        final TextView addressValueTextView;
        final TextView nameKeyTextView;
        final TextView nameValueTextView;
        final TextView phoneNumberKeyTextView;
        final TextView phoneNumberValueTextView;
        final TextView surnameKeyTextView;
        final TextView surnameValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            userIdKeyTextView = new TextView(context);
            userIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userIdKeyTextView, userIdValueTextView);
            layout.addView(userIdKeyTextView);
            layout.addView(userIdValueTextView);

            emailKeyTextView = new TextView(context);
            emailValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(emailKeyTextView, emailValueTextView);
            layout.addView(emailKeyTextView);
            layout.addView(emailValueTextView);

            activeKeyTextView = new TextView(context);
            activeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(activeKeyTextView, activeValueTextView);
            layout.addView(activeKeyTextView);
            layout.addView(activeValueTextView);

            addressKeyTextView = new TextView(context);
            addressValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(addressKeyTextView, addressValueTextView);
            layout.addView(addressKeyTextView);
            layout.addView(addressValueTextView);

            nameKeyTextView = new TextView(context);
            nameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(nameKeyTextView, nameValueTextView);
            layout.addView(nameKeyTextView);
            layout.addView(nameValueTextView);

            phoneNumberKeyTextView = new TextView(context);
            phoneNumberValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(phoneNumberKeyTextView, phoneNumberValueTextView);
            layout.addView(phoneNumberKeyTextView);
            layout.addView(phoneNumberValueTextView);

            surnameKeyTextView = new TextView(context);
            surnameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(surnameKeyTextView, surnameValueTextView);
            layout.addView(surnameKeyTextView);
            layout.addView(surnameValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            emailKeyTextView = (TextView) layout.getChildAt(3);
            emailValueTextView = (TextView) layout.getChildAt(4);

            activeKeyTextView = (TextView) layout.getChildAt(5);
            activeValueTextView = (TextView) layout.getChildAt(6);

            addressKeyTextView = (TextView) layout.getChildAt(7);
            addressValueTextView = (TextView) layout.getChildAt(8);

            nameKeyTextView = (TextView) layout.getChildAt(9);
            nameValueTextView = (TextView) layout.getChildAt(10);

            phoneNumberKeyTextView = (TextView) layout.getChildAt(11);
            phoneNumberValueTextView = (TextView) layout.getChildAt(12);

            surnameKeyTextView = (TextView) layout.getChildAt(13);
            surnameValueTextView = (TextView) layout.getChildAt(14);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        emailKeyTextView.setText("email");
        emailValueTextView.setText(result.getEmail());
        activeKeyTextView.setText("active");
        activeValueTextView.setText("" + result.getActive());
        addressKeyTextView.setText("address");
        addressValueTextView.setText(result.getAddress());
        nameKeyTextView.setText("name");
        nameValueTextView.setText(result.getName());
        phoneNumberKeyTextView.setText("phoneNumber");
        phoneNumberValueTextView.setText(result.getPhoneNumber());
        surnameKeyTextView.setText("surname");
        surnameValueTextView.setText(result.getSurname());
        return layout;
    }

    public DoctorDO getResult(){
        return result;
    }
}
