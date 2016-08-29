package com.mysampleapp.adapter;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by poolm on 01/08/2016.
 * passed the view shared between the two fragment
 */

public interface ItemClickListenerAnimation {
    void onClick(ImageView view, int position, boolean isLongClick);
}
