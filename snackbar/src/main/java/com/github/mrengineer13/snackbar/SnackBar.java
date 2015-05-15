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
import android.graphics.Typeface;
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

        /**
         * Gets called when a message is shown
         *
         * @param stackSize the number of messages left to show
         */
        void onShow(int stackSize);

        /**
         * Gets called when a message is hidden
         *
         * @param stackSize the number of messages left to show
         */
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
        private ColorStateList mBackgroundColor;
        private int mHeight;
        private boolean mClear;
        private boolean mAnimateClear;
        private Typeface mTypeFace;

        /**
         * Constructs a new SnackBar
         *
         * @param activity the activity to inflate into
         */
        public Builder(Activity activity) {
            mContext = activity.getApplicationContext();
            mSnackBar = new SnackBar(activity);
        }

        /**
         * Constructs a new SnackBar
         *
         * @param context the context used to obtain resources
         * @param v       the view to inflate the SnackBar into
         */
        public Builder(Context context, View v) {
            mContext = context;
            mSnackBar = new SnackBar(context, v);
        }

        /**
         * Sets the message to display on the SnackBar
         *
         * @param message the literal string to display
         * @return this builder
         */
        public Builder withMessage(String message) {
            mMessage = message;
            return this;
        }

        /**
         * Sets the message to display on the SnackBar
         *
         * @param messageId the resource id of the string to display
         * @return this builder
         */
        public Builder withMessageId(int messageId) {
            mMessage = mContext.getString(messageId);
            return this;
        }

        /**
         * Sets the message to display as the action message
         *
         * @param actionMessage the literal string to display
         * @return this builder
         */
        public Builder withActionMessage(String actionMessage) {
            mActionMessage = actionMessage;
            return this;
        }

        /**
         * Sets the message to display as the action message
         *
         * @param actionMessageResId the resource id of the string to display
         * @return this builder
         */
        public Builder withActionMessageId(int actionMessageResId) {
            if (actionMessageResId > 0) {
                mActionMessage = mContext.getString(actionMessageResId);
            }

            return this;
        }

        /**
         * Sets the action icon
         *
         * @param id the resource id of the icon to display
         * @return this builder
         */
        public Builder withActionIconId(int id) {
            mActionIcon = id;
            return this;
        }

        /**
         * Sets the {@link com.github.mrengineer13.snackbar.SnackBar.Style} for the action message
         *
         * @param style the {@link com.github.mrengineer13.snackbar.SnackBar.Style} to use
         * @return this builder
         */
        public Builder withStyle(Style style) {
            mTextColor = getActionTextColor(style);
            return this;
        }

        /**
         * The token used to restore the SnackBar state
         *
         * @param token the parcelable containing the saved SnackBar
         * @return this builder
         */
        public Builder withToken(Parcelable token) {
            mToken = token;
            return this;
        }

        /**
         * Sets the duration to show the message
         *
         * @param duration the number of milliseconds to show the message
         * @return this builder
         */
        public Builder withDuration(Short duration) {
            mDuration = duration;
            return this;
        }

        /**
         * Sets the {@link android.content.res.ColorStateList} for the action message
         *
         * @param colorId the
         * @return this builder
         */
        public Builder withTextColorId(int colorId) {
            ColorStateList color = mContext.getResources().getColorStateList(colorId);
            mTextColor = color;
            return this;
        }

        /**
         * Sets the {@link android.content.res.ColorStateList} for the SnackBar background
         *
         * @param colorId the SnackBar Background color
         * @return this builder
         */
        public Builder withBackgroundColorId(int colorId) {
            ColorStateList color = mContext.getResources().getColorStateList(colorId);
            mBackgroundColor = color;
            return this;
        }

        /**
         * Sets the height for SnackBar
         *
         * @param height the height of SnackBar
         * @return this builder
         */
        public Builder withSnackBarHeight(int height) {
            mHeight = height;
            return this;
        }

        /**
         * Sets the OnClickListener for the action button
         *
         * @param onClickListener the listener to inform of click events
         * @return this builder
         */
        public Builder withOnClickListener(OnMessageClickListener onClickListener) {
            mSnackBar.setOnClickListener(onClickListener);
            return this;
        }

        /**
         * Sets the visibilityChangeListener for the SnackBar
         *
         * @param visibilityChangeListener the listener to inform of visibility changes
         * @return this builder
         */
        public Builder withVisibilityChangeListener(OnVisibilityChangeListener visibilityChangeListener) {
            mSnackBar.setOnVisibilityChangeListener(visibilityChangeListener);
            return this;
        }

        /**
         * Clears all of the queued SnackBars, animates the message being hidden
         *
         * @return this builder
         */
        public Builder withClearQueued() {
            return withClearQueued(true);
        }

        /**
         * Clears all of the queued SnackBars
         *
         * @param animate whether or not to animate the messages being hidden
         * @return this builder
         */
        public Builder withClearQueued(boolean animate) {
            mAnimateClear = animate;
            mClear = true;
            return this;
        }

        /**
         * Sets the Typeface for the SnackBar
         *
         * @param typeFace the typeface to apply to the SnackBar
         * @return this builder
         */
        public Builder withTypeFace(Typeface typeFace) {
            mTypeFace = typeFace;
            return this;
        }

        /**
         * Shows the first message in the SnackBar
         *
         * @return the SnackBar
         */
        public SnackBar show() {
            Snack message = new Snack(mMessage,
                    (mActionMessage != null ? mActionMessage.toUpperCase() : null),
                    mActionIcon,
                    mToken,
                    mDuration,
                    mTextColor != null ? mTextColor : getActionTextColor(Style.DEFAULT),
                    mBackgroundColor != null ? mBackgroundColor : mContext.getResources().getColorStateList(R.color.sb__snack_bkgnd),
                    mHeight != 0 ? mHeight : 0,
                    mTypeFace);

            if (mClear) {
                mSnackBar.clear(mAnimateClear);
            }

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

    /**
     * Calculates the height of the SnackBar
     *
     * @return the height of the SnackBar
     */
    public int getHeight() {
        mParentView.measure(View.MeasureSpec.makeMeasureSpec(mParentView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mParentView.getHeight(), View.MeasureSpec.AT_MOST));
        return mParentView.getMeasuredHeight();
    }

    /**
     * Getter for the SnackBars parent view
     *
     * @return the parent view
     */
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

    private SnackBar setOnClickListener(OnMessageClickListener listener) {
        mClickListener = listener;
        return this;
    }

    private SnackBar setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        mVisibilityChangeListener = listener;
        return this;
    }

    /**
     * Clears all of the queued messages
     *
     * @param animate whether or not to animate the messages being hidden
     */
    public void clear(boolean animate) {
        mSnackContainer.clearSnacks(animate);
    }

    /**
     * Clears all of the queued messages
     */
    public void clear() {
        clear(true);
    }

    /**
     * Hides all snacks
     */
    public void hide() {
        mSnackContainer.hide();
        clear();
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
