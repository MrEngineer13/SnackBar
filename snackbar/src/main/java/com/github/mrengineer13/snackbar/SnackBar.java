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
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.Stack;

public class SnackBar {

    private static final String SAVED_MSGS = "SAVED_MSGS";

    private static final String SAVED_CURR_MSG = "SAVED_CURR_MSG";

    private static final int ANIMATION_DURATION = 300;

    public static final short LONG_SNACK = 5000;

    public static final short MED_SNACK = 3500;

    public static final short SHORT_SNACK = 2000;

    public static final short PERMANENT_SNACK = 0;

    private View mContainer;

    private View mParentView;

    private TextView mSnackMsg;

    private TextView mSnackBtn;

    private Stack<Snack> mSnacks = new Stack<Snack>();

    private Snack mCurrentSnack;

    private boolean mShowing;

    private OnMessageClickListener mClickListener;

    private OnVisibilityChangeListener mVisibilityChangeListener;

    private Handler mHandler;

    private float mPreviousY;

    private AnimationSet mOutAnimationSet;

    private AnimationSet mInAnimationSet;

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
        View v = activity.getLayoutInflater().inflate(R.layout.sb__snack, container);
        init(v);
    }

    public SnackBar(Context context, View v) {
        mContext = context;
        init(v);
    }

    private void init(View v) {
        mParentView = v;
        mContainer = v.findViewById(R.id.snackContainer);
        mContainer.setVisibility(View.GONE);
        mSnackMsg = (TextView) v.findViewById(R.id.snackMessage);
        mSnackBtn = (TextView) v.findViewById(R.id.snackButton);
        mSnackBtn.setOnClickListener(mButtonListener);

        mInAnimationSet = new AnimationSet(false);

        TranslateAnimation mSlideInAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);

        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);

        mInAnimationSet.addAnimation(mSlideInAnimation);
        mInAnimationSet.addAnimation(mFadeInAnimation);

        mOutAnimationSet = new AnimationSet(false);

        TranslateAnimation mSlideOutAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f);

        AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);

        mOutAnimationSet.addAnimation(mSlideOutAnimation);
        mOutAnimationSet.addAnimation(mFadeOutAnimation);

        mOutAnimationSet.setDuration(ANIMATION_DURATION);
        mOutAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sendOnHide();
                if (!mSnacks.empty()) {
                    show(mSnacks.pop());
                } else {
                    mCurrentSnack = null;
                    mContainer.setVisibility(View.GONE);
                    mShowing = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHandler = new Handler();
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
        if (isShowing()) {
            mSnacks.push(m);
        } else {
            show(m);
        }
        return this;
    }

    public SnackBar show(String message, String actionMessage, Style style, int actionIcon, Parcelable token, short duration) {
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null), actionIcon, token, duration, style);
        if (isShowing()) {
            mSnacks.push(m);
        } else {
            show(m);
        }
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
        if (isShowing()) {
            mSnacks.push(m);
        } else {
            show(m);
        }
        return this;
    }

    public SnackBar show(int messageResId, int actionMessageResId, Style style, int actionIcon, Parcelable token, short duration) {
        String message = mContext.getString(messageResId);
        String actionMessage = null;
        if (actionMessageResId > 0) {
            actionMessage = mContext.getString(actionMessageResId);
        }
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null), actionIcon, token, duration, style);
        if (isShowing()) {
            mSnacks.push(m);
        } else {
            show(m);
        }
        return this;
    }

    public int getHeight() {
        mContainer.measure(View.MeasureSpec.makeMeasureSpec(mParentView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mParentView.getHeight(), View.MeasureSpec.AT_MOST));
        return mContainer.getMeasuredHeight();
    }

    public View getContainerView() {
        return mContainer;
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

    private SnackBar show(Snack message) {
        show(message, false);
        return this;
    }

    private void show(Snack message, boolean immediately) {
        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
        sendOnShow();
        mCurrentSnack = message;
        mSnackMsg.setText(message.mMessage);
        if (message.mActionMessage != null) {
            mSnackMsg.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mSnackBtn.setVisibility(View.VISIBLE);
            mSnackBtn.setText(message.mActionMessage);
            mSnackBtn.setCompoundDrawablesWithIntrinsicBounds(message.mActionIcon, 0, 0, 0);
        } else {
            mSnackMsg.setGravity(Gravity.CENTER);
            mSnackBtn.setVisibility(View.GONE);
        }

        if (message.mBtnTextColor != null) {
            mSnackBtn.setTextColor(message.mBtnTextColor);
        } else {
            mSnackBtn.setTextColor(getActionTextColor(message.mStyle));
        }


        if (immediately) {
            mInAnimationSet.setDuration(0);
        } else {
            mInAnimationSet.setDuration(ANIMATION_DURATION);
        }
        mContainer.startAnimation(mInAnimationSet);

        if (message.mDuration > 0) {
            mHandler.postDelayed(mHideRunnable, message.mDuration);
        }

        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int[] location = new int[2];
                        mContainer.getLocationInWindow(location);
                        if (y > mPreviousY) {
                            float dy = y - mPreviousY;
                            mContainer.offsetTopAndBottom(Math.round(4 * dy));

                            if ((mContainer.getResources().getDisplayMetrics().heightPixels - location[1]) - 100 <= 0) {
                                mHandler.removeCallbacks(mHideRunnable);
                                mContainer.startAnimation(mOutAnimationSet);

                                if (!mSnacks.empty()) {
                                    mSnacks.clear();
                                }
                            }
                        }
                }

                mPreviousY = y;

                return true;
            }
        });
    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mClickListener != null && mCurrentSnack != null) {
                mClickListener.onMessageClick(mCurrentSnack.mToken);
                mCurrentSnack = null;
                mHandler.removeCallbacks(mHideRunnable);
                mHideRunnable.run();
            }
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
        mSnacks.clear();
        if (animate) mHideRunnable.run();
    }

    public void clear() {
        clear(true);
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (View.VISIBLE == mContainer.getVisibility()) {
                mContainer.startAnimation(mOutAnimationSet);
            }
        }
    };


    public void onRestoreInstanceState(Bundle state) {
        Snack currentSnack = state.getParcelable(SAVED_CURR_MSG);
        if (currentSnack != null) {
            show(currentSnack, true);
            Parcelable[] messages = state.getParcelableArray(SAVED_MSGS);
            for (Parcelable p : messages) {
                mSnacks.push((Snack) p);
            }
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle b = new Bundle();

        b.putParcelable(SAVED_CURR_MSG, mCurrentSnack);

        final int count = mSnacks.size();
        final Snack[] snacks = new Snack[count];
        int i = 0;
        for (Snack snack : mSnacks) {
            snacks[i++] = snack;
        }

        b.putParcelableArray(SAVED_MSGS, snacks);

        return b;
    }

    private boolean isShowing() {
        return mShowing;
    }

    public enum Style {
        DEFAULT,
        ALERT,
        CONFIRM,
        INFO
    }

    private static class Snack implements Parcelable {

        final String mMessage;

        final String mActionMessage;

        final int mActionIcon;

        final Parcelable mToken;

        final short mDuration;

        final ColorStateList mBtnTextColor;

        final Style mStyle;

        public Snack(String message, String actionMessage, int actionIcon,
                     Parcelable token, short duration, ColorStateList textColor) {
            mMessage = message;
            mActionMessage = actionMessage;
            mActionIcon = actionIcon;
            mToken = token;
            mDuration = duration;
            mBtnTextColor = textColor;
            mStyle = Style.DEFAULT;
        }

        public Snack(String message, String actionMessage, int actionIcon,
                     Parcelable token, short duration, Style style) {
            mMessage = message;
            mActionMessage = actionMessage;
            mActionIcon = actionIcon;
            mToken = token;
            mDuration = duration;
            mStyle = style;
            mBtnTextColor = null;
        }

        // reads data from parcel
        public Snack(Parcel p) {
            mMessage = p.readString();
            mActionMessage = p.readString();
            mActionIcon = p.readInt();
            mToken = p.readParcelable(p.getClass().getClassLoader());
            mDuration = (short) p.readInt();
            mBtnTextColor = p.readParcelable(p.getClass().getClassLoader());
            mStyle = Style.valueOf(p.readString());
        }

        // writes data to parcel
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(mMessage);
            out.writeString(mActionMessage);
            out.writeInt(mActionIcon);
            out.writeParcelable(mToken, 0);
            out.writeInt((int) mDuration);
            out.writeParcelable(mBtnTextColor, 0);
            out.writeString(mStyle.name());
        }

        public int describeContents() {
            return 0;
        }

        // creates snack array
        public static final Parcelable.Creator<Snack> CREATOR = new Parcelable.Creator<Snack>() {
            public Snack createFromParcel(Parcel in) {
                return new Snack(in);
            }

            public Snack[] newArray(int size) {
                return new Snack[size];
            }
        };
    }

    private void sendOnHide() {
        if (mVisibilityChangeListener != null) {
            mVisibilityChangeListener.onHide(mSnacks.size());
        }
    }

    private void sendOnShow() {
        if (mVisibilityChangeListener != null) {
            mVisibilityChangeListener.onShow(mSnacks.size());
        }
    }
}
