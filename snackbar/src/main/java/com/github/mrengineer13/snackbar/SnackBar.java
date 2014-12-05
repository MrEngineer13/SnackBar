/*
 * Copyright (c) 2014 MrEngineer13
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrengineer13.snackbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SnackBar {

    public static final short LONG_SNACK = 5000;

    public static final short MED_SNACK = 3500;

    public static final short SHORT_SNACK = 2000;

    public static final short PERMANENT_SNACK = 0;

    private SnackContainer mSnackContainer;

    private View mParentView;

    private TextView mSnackMsg;

    private TextView mSnackBtn;

    private OnMessageClickListener mClickListener;

    private OnVisibilityChangeListener mVisibilityChangeListener;

    private Context mContext;

    public interface OnMessageClickListener {

        void onMessageClick(Parcelable token);
    }

    public interface OnVisibilityChangeListener {

        void onShow(int stackSize);

        void onHide(int stackSize);
    }

    public SnackBar(Activity activity) {
        mContext = activity.getApplicationContext();
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.sb__snack, container, false);
        init(container, v);
    }

    public SnackBar(Context context, View v) {
        mContext = context;
        View layout = View.inflate(context, R.layout.sb__snack, (ViewGroup) v.getParent());
        init((ViewGroup) v, layout);
    }

    private void init(ViewGroup container, View v) {
        mSnackContainer = (SnackContainer) container.findViewById(R.id.snackContainer);
        if (mSnackContainer == null) {
            mSnackContainer = new SnackContainer(container);
        }

        mParentView = v;
        mSnackMsg = (TextView) v.findViewById(R.id.snackMessage);
        mSnackBtn = (TextView) v.findViewById(R.id.snackButton);
        mSnackBtn.setOnClickListener(mButtonListener);
    }

    public static SnackBar create(Activity activity) {
        return new SnackBar(activity);
    }

    public static SnackBar create(Context context, View view) {
        return new SnackBar(context, view);
    }

    public SnackBar show(String message) {
        show(message, null);
        return this;
    }

    public SnackBar show(String message, String actionMessage) {
        show(message, actionMessage, Style.DEFAULT);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor) {
        show(message, actionMessage, textColor, 0);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style actionStyle) {
        show(message, actionMessage, actionStyle, 0);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor, int actionIcon) {
        show(message, actionMessage, textColor, actionIcon, null);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style actionStyle, int actionIcon) {
        show(message, actionMessage, actionStyle, actionIcon, null);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int actionIcon, Parcelable token) {
        show(message, actionMessage, Style.DEFAULT, actionIcon, token);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor, int actionIcon, Parcelable token) {
        show(message, actionMessage, textColor, actionIcon, token, MED_SNACK);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style actionStyle, int actionIcon, Parcelable token) {
        show(message, actionMessage, actionStyle, actionIcon, token, MED_SNACK);
        return this;
    }

    public SnackBar show(String message, short duration) {
        show(message, null, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, short duration) {
        show(message, actionMessage, Style.DEFAULT, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor, short duration) {
        show(message, actionMessage, textColor, 0, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style actionStyle, short duration) {
        show(message, actionMessage, actionStyle, 0, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor, int actionIcon, short duration) {
        show(message, actionMessage, textColor, actionIcon, null, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style actionStyle, int actionIcon, short duration) {
        show(message, actionMessage, actionStyle, actionIcon, null, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int actionIcon, Parcelable token, short duration) {
        show(message, actionMessage, Style.DEFAULT, actionIcon, token, duration);
        return this;
    }

    public SnackBar show(String message, String actionMessage, int textColor, int actionIcon, Parcelable token, short duration) {
        ColorStateList color = mContext.getResources().getColorStateList(textColor);
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null),
                actionIcon, token, duration, color);
        mSnackContainer.showSnack(m, mParentView, mVisibilityChangeListener);
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style style, int actionIcon, Parcelable token, short duration) {
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null), actionIcon, token, duration, style);
        mSnackContainer.showSnack(m, mParentView, mVisibilityChangeListener);
        return this;
    }

    //////////////////////////////////////////////////////////////////////////////////////

    public SnackBar show(int message) {
        show(message, -1);
        return this;
    }

    public SnackBar show(int message, int actionMessage) {
        show(message, actionMessage, Style.DEFAULT);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int textColor) {
        show(message, actionMessage, textColor, 0);
        return this;
    }

    public SnackBar show(int message, int actionMessage, Style actionStyle) {
        show(message, actionMessage, actionStyle, 0);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int textColor, int actionIcon) {
        show(message, actionMessage, textColor, actionIcon, null);
        return this;
    }

    public SnackBar show(int message, int actionMessage, Style actionStyle, int actionIcon) {
        show(message, actionMessage, actionStyle, actionIcon, null);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int actionIcon, Parcelable token) {
        show(message, actionMessage, Style.DEFAULT, actionIcon, token);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int textColor, int actionIcon, Parcelable token) {
        show(message, actionMessage, textColor, actionIcon, token, MED_SNACK);
        return this;
    }

    public SnackBar show(int message, int actionMessage, Style actionStyle, int actionIcon, Parcelable token) {
        show(message, actionMessage, actionStyle, actionIcon, token, MED_SNACK);
        return this;
    }

    public SnackBar show(int message, short duration) {
        show(message, 0, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, short duration) {
        show(message, actionMessage, Style.DEFAULT, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int textColor, short duration) {
        show(message, actionMessage, textColor, 0, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, Style actionStyle, short duration) {
        show(message, actionMessage, actionStyle, 0, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int textColor, int actionIcon, short duration) {
        show(message, actionMessage, textColor, actionIcon, null, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, Style actionStyle, int actionIcon, short duration) {
        show(message, actionMessage, actionStyle, actionIcon, null, duration);
        return this;
    }

    public SnackBar show(int message, int actionMessage, int actionIcon, Parcelable token, short duration) {
        show(message, actionMessage, Style.DEFAULT, actionIcon, token, duration);
        return this;
    }

    public SnackBar show(int messageResId, int actionMessageResId, int textColor, int actionIcon, Parcelable token,
                         short duration) {
        ColorStateList color = mContext.getResources().getColorStateList(textColor);
        String message = mContext.getString(messageResId);
        String actionMessage = null;
        if (actionMessageResId > 0) {
            actionMessage = mContext.getString(actionMessageResId);
        }
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null),
                actionIcon, token, duration, color);
        mSnackContainer.showSnack(m, mParentView, mVisibilityChangeListener);
        return this;
    }

    public SnackBar show(int messageResId, int actionMessageResId, Style style, int actionIcon, Parcelable token, short duration) {
        String message = mContext.getString(messageResId);
        String actionMessage = null;
        if (actionMessageResId > 0) {
            actionMessage = mContext.getString(actionMessageResId);
        }
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null), actionIcon, token, duration, style);
        mSnackContainer.showSnack(m, mParentView, mVisibilityChangeListener);
        return this;
    }

    public int getHeight() {
        mParentView.measure(View.MeasureSpec.makeMeasureSpec(mParentView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mParentView.getHeight(), View.MeasureSpec.AT_MOST));
        return mParentView.getMeasuredHeight();
    }

    public View getContainerView() {
        return mParentView;
    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mClickListener != null && mSnackContainer.isShowing()) {
                mClickListener.onMessageClick(mSnackContainer.peek().mToken);
            }
            mSnackContainer.hide();
        }
    };

    public SnackBar setOnClickListener(OnMessageClickListener listener) {
        mClickListener = listener;
        return this;
    }

    public SnackBar setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        mVisibilityChangeListener = listener;
        return this;
    }

    public void clear(boolean animate) {
        mSnackContainer.clearSnacks(animate);
    }

    public void clear() {
        clear(true);
    }

    /**
     * All snacks will be restored using the view from this Snackbar
     */
    public void onRestoreInstanceState(Bundle state) {
        mSnackContainer.restoreState(state, mParentView);
    }

    public Bundle onSaveInstanceState() {
        return mSnackContainer.saveState();
    }

    public enum Style {
        DEFAULT,
        ALERT,
        CONFIRM,
        INFO
    }
}
