package com.mysampleapp.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.Set;

public class DemoNoSQLUserResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final UserDO result;

    DemoNoSQLUserResult(final UserDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getAddress();
        result.setAddress(DemoSampleDataGenerator.getRandomSampleString("address"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setAddress(originalValue);
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
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView addressKeyTextView;
        final TextView addressValueTextView;
        final TextView dateBirthKeyTextView;
        final TextView dateBirthValueTextView;
        final TextView emailKeyTextView;
        final TextView emailValueTextView;
        final TextView nameKeyTextView;
        final TextView nameValueTextView;
        final TextView phoneKeyTextView;
        final TextView phoneValueTextView;
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

            addressKeyTextView = new TextView(context);
            addressValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(addressKeyTextView, addressValueTextView);
            layout.addView(addressKeyTextView);
            layout.addView(addressValueTextView);

            dateBirthKeyTextView = new TextView(context);
            dateBirthValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(dateBirthKeyTextView, dateBirthValueTextView);
            layout.addView(dateBirthKeyTextView);
            layout.addView(dateBirthValueTextView);

            emailKeyTextView = new TextView(context);
            emailValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(emailKeyTextView, emailValueTextView);
            layout.addView(emailKeyTextView);
            layout.addView(emailValueTextView);

            nameKeyTextView = new TextView(context);
            nameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(nameKeyTextView, nameValueTextView);
            layout.addView(nameKeyTextView);
            layout.addView(nameValueTextView);

            phoneKeyTextView = new TextView(context);
            phoneValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(phoneKeyTextView, phoneValueTextView);
            layout.addView(phoneKeyTextView);
            layout.addView(phoneValueTextView);

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

            addressKeyTextView = (TextView) layout.getChildAt(3);
            addressValueTextView = (TextView) layout.getChildAt(4);

            dateBirthKeyTextView = (TextView) layout.getChildAt(5);
            dateBirthValueTextView = (TextView) layout.getChildAt(6);

            emailKeyTextView = (TextView) layout.getChildAt(7);
            emailValueTextView = (TextView) layout.getChildAt(8);

            nameKeyTextView = (TextView) layout.getChildAt(9);
            nameValueTextView = (TextView) layout.getChildAt(10);

            phoneKeyTextView = (TextView) layout.getChildAt(11);
            phoneValueTextView = (TextView) layout.getChildAt(12);

            surnameKeyTextView = (TextView) layout.getChildAt(13);
            surnameValueTextView = (TextView) layout.getChildAt(14);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        addressKeyTextView.setText("address");
        addressValueTextView.setText(result.getAddress());
        dateBirthKeyTextView.setText("dateBirth");
        dateBirthValueTextView.setText(result.getDateBirth().toString());
        emailKeyTextView.setText("email");
        emailValueTextView.setText(result.getEmail());
        nameKeyTextView.setText("name");
        nameValueTextView.setText(result.getName());
        phoneKeyTextView.setText("phone");
        phoneValueTextView.setText("" + result.getPhone().longValue());
        surnameKeyTextView.setText("surname");
        surnameValueTextView.setText(result.getSurname());
        return layout;
    }

    public UserDO getResult(){
        return result;
    }
}
