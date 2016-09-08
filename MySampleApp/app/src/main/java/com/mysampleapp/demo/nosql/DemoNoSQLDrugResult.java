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

public class DemoNoSQLDrugResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final DrugDO result;

    DemoNoSQLDrugResult(final DrugDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final double originalValue = result.getMinqty();
        result.setMinqty(DemoSampleDataGenerator.getRandomSampleNumber());
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setMinqty(originalValue);
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
        final TextView nameKeyTextView;
        final TextView nameValueTextView;
        final TextView minqtyKeyTextView;
        final TextView minqtyValueTextView;
        final TextView notesKeyTextView;
        final TextView notesValueTextView;
        final TextView quantityKeyTextView;
        final TextView quantityValueTextView;
        final TextView typeKeyTextView;
        final TextView typeValueTextView;
        final TextView weightKeyTextView;
        final TextView weightValueTextView;
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

            nameKeyTextView = new TextView(context);
            nameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(nameKeyTextView, nameValueTextView);
            layout.addView(nameKeyTextView);
            layout.addView(nameValueTextView);

            minqtyKeyTextView = new TextView(context);
            minqtyValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(minqtyKeyTextView, minqtyValueTextView);
            layout.addView(minqtyKeyTextView);
            layout.addView(minqtyValueTextView);

            notesKeyTextView = new TextView(context);
            notesValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(notesKeyTextView, notesValueTextView);
            layout.addView(notesKeyTextView);
            layout.addView(notesValueTextView);

            quantityKeyTextView = new TextView(context);
            quantityValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(quantityKeyTextView, quantityValueTextView);
            layout.addView(quantityKeyTextView);
            layout.addView(quantityValueTextView);

            typeKeyTextView = new TextView(context);
            typeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(typeKeyTextView, typeValueTextView);
            layout.addView(typeKeyTextView);
            layout.addView(typeValueTextView);

            weightKeyTextView = new TextView(context);
            weightValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(weightKeyTextView, weightValueTextView);
            layout.addView(weightKeyTextView);
            layout.addView(weightValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            nameKeyTextView = (TextView) layout.getChildAt(3);
            nameValueTextView = (TextView) layout.getChildAt(4);

            minqtyKeyTextView = (TextView) layout.getChildAt(5);
            minqtyValueTextView = (TextView) layout.getChildAt(6);

            notesKeyTextView = (TextView) layout.getChildAt(7);
            notesValueTextView = (TextView) layout.getChildAt(8);

            quantityKeyTextView = (TextView) layout.getChildAt(9);
            quantityValueTextView = (TextView) layout.getChildAt(10);

            typeKeyTextView = (TextView) layout.getChildAt(11);
            typeValueTextView = (TextView) layout.getChildAt(12);

            weightKeyTextView = (TextView) layout.getChildAt(13);
            weightValueTextView = (TextView) layout.getChildAt(14);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        nameKeyTextView.setText("name");
        nameValueTextView.setText(result.getName());
        minqtyKeyTextView.setText("minqty");
        minqtyValueTextView.setText("" + result.getMinqty().longValue());
        notesKeyTextView.setText("notes");
        notesValueTextView.setText(result.getNotes());
        quantityKeyTextView.setText("sottoscorta");
        quantityValueTextView.setText("" + result.getQuantity().longValue());
        typeKeyTextView.setText("type");
        typeValueTextView.setText(result.getType());
        weightKeyTextView.setText("weight");
        weightValueTextView.setText("" + result.getWeight().longValue());
        return layout;
    }

    public DrugDO getResult(){
        return result;
    }
}
