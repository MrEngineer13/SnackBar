package com.mrengineer13.snackbar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Stack;

public class SnackBar {

    private static final String SAVED_MSGS = "SAVED_MSGS";

    private static final String SAVED_CURR_MSG = "SAVED_CURR_MSG";

    private static final int ANIMATION_DURATION = 600;

    public static final short LONG_SNACK = 5000;

    public static final short MED_SNACK = 3500;

    public static final short SHORT_SNACK = 2000;

    private View mContainer;

    private TextView mSnackMsg;

    private TextView mSnackBtn;

    private Stack<Snack> mSnacks = new Stack<Snack>();

    private Snack mCurrentSnack;

    private boolean mShowing;

    private OnMessageClickListener mClickListener;

    private Handler mHandler;

    private AlphaAnimation mFadeInAnimation;

    private AlphaAnimation mFadeOutAnimation;

    private float mPreviousY;

    public interface OnMessageClickListener {

        void onMessageClick(Parcelable token);
    }

    public SnackBar(Activity activity) {
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.sb__snack, container);
        init(v);
    }

    public SnackBar(View v) {
        init(v);
    }

    private void init(View v) {
        mContainer = v.findViewById(R.id.snackContainer);
        mContainer.setVisibility(View.GONE);
        mSnackMsg = (TextView) v.findViewById(R.id.snackMessage);
        mSnackBtn = (TextView) v.findViewById(R.id.snackButton);
        mSnackBtn.setOnClickListener(mButtonListener);

        mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);

        mFadeOutAnimation.setDuration(ANIMATION_DURATION);
        mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
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


    public void show(String message) {
        show(message, null);
    }

    public void show(String message, String actionMessage) {
        show(message, actionMessage, 0);
    }

    public void show(String message, String actionMessage, int actionIcon) {
        show(message, actionMessage, actionIcon, null);
    }

    public void show(String message, String actionMessage, int actionIcon, Parcelable token) {
        show(message, actionMessage, actionIcon, token, MED_SNACK);
    }

    public void show(String message, short duration) {
        show(message, null, duration);
    }

    public void show(String message, String actionMessage, short duration) {
        show(message, actionMessage, 0, duration);
    }

    public void show(String message, String actionMessage, int actionIcon, short duration) {
        show(message, actionMessage, actionIcon, null, duration);
    }

    public void show(String message, String actionMessage, int actionIcon, Parcelable token, short duration) {
        Snack m = new Snack(message, (actionMessage != null ? actionMessage.toUpperCase() : null), actionIcon, token, duration);
        if (isShowing()) {
            mSnacks.push(m);
        } else {
            show(m);
        }
    }

    private void show(Snack message) {
        show(message, false);
    }

    private void show(Snack message, boolean immediately) {
        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
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

        System.out.println("immediately " + immediately);

        if (immediately) {
            mFadeInAnimation.setDuration(0);
        } else {
            mFadeInAnimation.setDuration(ANIMATION_DURATION);
        }
        mContainer.startAnimation(mFadeInAnimation);
        mHandler.postDelayed(mHideRunnable, message.mDuration);

        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int[] location = new int[2];
                        mContainer.getLocationInWindow(location);
                        if (y > mPreviousY){
                            float dy = y - mPreviousY;
                            mContainer.offsetTopAndBottom(Math.round(4 * dy));

                            if ((mContainer.getResources().getDisplayMetrics().heightPixels - location[1]) - 100 <= 0){
                                mHandler.removeCallbacks(mHideRunnable);
                                mContainer.startAnimation(mFadeOutAnimation);

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
    public void setOnClickListener(OnMessageClickListener listener) {
        mClickListener = listener;
    }

    public void clear() {
        mSnacks.clear();
        mHideRunnable.run();
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mContainer.startAnimation(mFadeOutAnimation);
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

    private boolean isShowing(){
        return mShowing;
    }

    private static class Snack implements Parcelable {

        final String mMessage;

        final String mActionMessage;

        final int mActionIcon;

        final Parcelable mToken;

        final short mDuration;

        public Snack(String message, String actionMessage, int actionIcon, Parcelable token, short duration) {
            mMessage = message;
            mActionMessage = actionMessage;
            mActionIcon = actionIcon;
            mToken = token;
            mDuration = duration;
        }

        // reads data from parcel
        public Snack(Parcel p) {
            mMessage = p.readString();
            mActionMessage = p.readString();
            mActionIcon = p.readInt();
            mToken = p.readParcelable(p.getClass().getClassLoader());
            mDuration = (short) p.readInt();
        }

        // writes data to parcel
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(mMessage);
            out.writeString(mActionMessage);
            out.writeInt(mActionIcon);
            out.writeParcelable(mToken, 0);
            out.writeInt((int)mDuration);
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
}
