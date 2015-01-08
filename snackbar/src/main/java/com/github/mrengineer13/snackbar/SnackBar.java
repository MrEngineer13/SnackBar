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
import android.view.LayoutInflater;
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

    private OnMessageClickListener mClickListener;

    private OnVisibilityChangeListener mVisibilityChangeListener;

    public interface OnMessageClickListener {

        void onMessageClick(Parcelable token);
    }

    public interface OnVisibilityChangeListener {

        void onShow(int stackSize);

        void onHide(int stackSize);
    }

    public SnackBar(Activity activity) {
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.sb__snack, container, false);
        init(container, v);
    }

    public SnackBar(Context context, View v) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sb__snack_container, ((ViewGroup) v));
        View snackLayout = inflater.inflate(R.layout.sb__snack, ((ViewGroup) v), false);
        init((ViewGroup) v, snackLayout);
    }

    private void init(ViewGroup container, View v) {
        mSnackContainer = (SnackContainer) container.findViewById(R.id.snackContainer);
        if (mSnackContainer == null) {
            mSnackContainer = new SnackContainer(container);
        }

        mParentView = v;
        TextView snackBtn = (TextView) v.findViewById(R.id.snackButton);
        snackBtn.setOnClickListener(mButtonListener);
    }

    public static class Builder {

        private SnackBar mSnackBar;

        private Context mContext;
        private String mMessage;
        private String mActionMessage;
        private int mActionIcon = 0;
        private Parcelable mToken;
        private short mDuration = MED_SNACK;
        private ColorStateList mTextColor;


        public Builder(Activity activity) {
            mContext = activity.getApplicationContext();
            mSnackBar = new SnackBar(activity);
        }

        public Builder(Context context, View v) {
            mContext = context;
            mSnackBar = new SnackBar(context, v);
        }

        public Builder withMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder withMessageId(int messageId) {
            mMessage = mContext.getString(messageId);
            return this;
        }

        public Builder withActionMessage(String actionMessage) {
            mActionMessage = actionMessage;
            return this;
        }

        public Builder withActionMessageId(int actionMessageResId) {
            if (actionMessageResId > 0) {
                mActionMessage = mContext.getString(actionMessageResId);
            }

            return this;
        }

        public Builder withActionIconId(int id) {
            mActionIcon = id;
            return this;
        }

        public Builder withStyle(Style style) {
            mTextColor = getActionTextColor(style);
            return this;
        }

        public Builder withToken(Parcelable token) {
            mToken = token;
            return this;
        }

        public Builder withDuration(Short duration) {
            mDuration = duration;
            return this;
        }

        public Builder withTextColorId(int colorId) {
            ColorStateList color = mContext.getResources().getColorStateList(colorId);
            mTextColor = color;
            return this;
        }

        public Builder withOnClickListener(OnMessageClickListener onClickListener) {
            mSnackBar.setOnClickListener(onClickListener);
            return this;
        }

        public Builder withVisibilityChangeListener(OnVisibilityChangeListener visibilityChangeListener) {
            mSnackBar.setOnVisibilityChangeListener(visibilityChangeListener);
            return this;
        }

        public SnackBar show() {
            Snack message = new Snack(mMessage,
                    (mActionMessage != null ? mActionMessage.toUpperCase() : null),
                    mActionIcon,
                    mToken,
                    mDuration,
                    mTextColor != null ? mTextColor : getActionTextColor(Style.DEFAULT));

            mSnackBar.showMessage(message);

            return mSnackBar;
        }

        private ColorStateList getActionTextColor(Style style) {
            switch (style) {
                case ALERT:
                    return mContext.getResources().getColorStateList(R.color.sb__button_text_color_red);
                case INFO:
                    return mContext.getResources().getColorStateList(R.color.sb__button_text_color_yellow);
                case CONFIRM:
                    return mContext.getResources().getColorStateList(R.color.sb__button_text_color_green);
                case DEFAULT:
                    return mContext.getResources().getColorStateList(R.color.sb__default_button_text_color);
                default:
                    return mContext.getResources().getColorStateList(R.color.sb__default_button_text_color);
            }
        }
    }

    private void showMessage(Snack message) {
        mSnackContainer.showSnack(message, mParentView, mVisibilityChangeListener);
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
