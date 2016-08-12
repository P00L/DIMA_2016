package com.mysampleapp.demo.nosql;

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

import java.util.Set;

public class DemoNoSQLScheduleDrugResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final ScheduleDrugDO result;

    DemoNoSQLScheduleDrugResult(final ScheduleDrugDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final double originalValue = result.getAlarmId();
        result.setAlarmId(DemoSampleDataGenerator.getRandomSampleNumber());
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setAlarmId(originalValue);
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
        final TextView alarmIdKeyTextView;
        final TextView alarmIdValueTextView;
        final TextView dayKeyTextView;
        final TextView dayValueTextView;
        final TextView drugKeyTextView;
        final TextView drugValueTextView;
        final TextView hourKeyTextView;
        final TextView hourValueTextView;
        final TextView notesKeyTextView;
        final TextView notesValueTextView;
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

            alarmIdKeyTextView = new TextView(context);
            alarmIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(alarmIdKeyTextView, alarmIdValueTextView);
            layout.addView(alarmIdKeyTextView);
            layout.addView(alarmIdValueTextView);

            dayKeyTextView = new TextView(context);
            dayValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(dayKeyTextView, dayValueTextView);
            layout.addView(dayKeyTextView);
            layout.addView(dayValueTextView);

            drugKeyTextView = new TextView(context);
            drugValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(drugKeyTextView, drugValueTextView);
            layout.addView(drugKeyTextView);
            layout.addView(drugValueTextView);

            hourKeyTextView = new TextView(context);
            hourValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(hourKeyTextView, hourValueTextView);
            layout.addView(hourKeyTextView);
            layout.addView(hourValueTextView);

            notesKeyTextView = new TextView(context);
            notesValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(notesKeyTextView, notesValueTextView);
            layout.addView(notesKeyTextView);
            layout.addView(notesValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            alarmIdKeyTextView = (TextView) layout.getChildAt(3);
            alarmIdValueTextView = (TextView) layout.getChildAt(4);

            dayKeyTextView = (TextView) layout.getChildAt(5);
            dayValueTextView = (TextView) layout.getChildAt(6);

            drugKeyTextView = (TextView) layout.getChildAt(7);
            drugValueTextView = (TextView) layout.getChildAt(8);

            hourKeyTextView = (TextView) layout.getChildAt(9);
            hourValueTextView = (TextView) layout.getChildAt(10);

            notesKeyTextView = (TextView) layout.getChildAt(11);
            notesValueTextView = (TextView) layout.getChildAt(12);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        alarmIdKeyTextView.setText("alarmId");
        alarmIdValueTextView.setText("" + result.getAlarmId().longValue());
        dayKeyTextView.setText("day");
        dayValueTextView.setText(result.getDay().toString());
        drugKeyTextView.setText("drug");
        drugValueTextView.setText(result.getDrug());
        hourKeyTextView.setText("hour");
        hourValueTextView.setText(result.getHour().toString());
        notesKeyTextView.setText("notes");
        notesValueTextView.setText(result.getNotes());
        return layout;
    }
}
