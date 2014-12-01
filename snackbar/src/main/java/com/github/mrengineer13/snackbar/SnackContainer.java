package com.github.mrengineer13.snackbar;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar.OnVisibilityChangeListener;
import com.github.mrengineer13.snackbar.SnackBar.Style;

import java.util.Stack;

class SnackContainer extends FrameLayout {

    private static final int ANIMATION_DURATION = 300;

    private static final String SAVED_MSGS = "SAVED_MSGS";
    private static final String SAVED_CURR_MSG = "SAVED_CURR_MSG";

    private Stack<SnackHolder> mSnacks = new Stack<SnackHolder>();
    private SnackHolder mCurrentSnackHolder;

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;

    private float mPreviousY;

    SnackContainer(ViewGroup container) {
        super(container.getContext());

        setId(R.id.snackContainer);
        setVisibility(View.GONE);
        container.addView(this);

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
                removeAllViews();

                if (mSnacks.contains(mCurrentSnackHolder)) {
                    sendOnHide(mCurrentSnackHolder);
                    mSnacks.remove(mCurrentSnackHolder);
                    mCurrentSnackHolder = null;
                }

                if (!isEmpty()) {
                    showSnack(mSnacks.peek());
                } else {
                    setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInAnimationSet.cancel();
        mOutAnimationSet.cancel();
        removeCallbacks(mHideRunnable);
        mSnacks.clear();
    }

    /*
     * Q Management *
     */

    public boolean isEmpty() {
        return mSnacks.isEmpty();
    }

    public Snack peek() {
        return mSnacks.peek().snack;
    }

    public Snack popSnack() {
        return mSnacks.pop().snack;
    }

    public void clearSnacks(boolean animate) {
        mSnacks.clear();
    }

    /*
     * Showing Logic *
     */

    public boolean isShowing() {
        return !mSnacks.empty();
    }

    public void hide() {
        removeCallbacks(mHideRunnable);
        mHideRunnable.run();
    }

    public void showSnack(Snack snack, View snackView, OnVisibilityChangeListener listener) {
        showSnack(snack, snackView, listener, false);
    }


    public void showSnack(Snack snack, View snackView, OnVisibilityChangeListener listener, boolean immediately) {
        if (snackView.getParent() != null && snackView.getParent() != this) {
            ((ViewGroup) snackView.getParent()).removeView(snackView);
        }

        SnackHolder holder = new SnackHolder(snack, snackView, listener);
        mSnacks.push(holder);
        if (mSnacks.size() == 1) showSnack(holder, immediately);
    }

    private void showSnack(final SnackHolder holder) {
        showSnack(holder, false);
    }

    private void showSnack(final SnackHolder holder, boolean showImmediately) {
        mCurrentSnackHolder = holder;

        setVisibility(View.VISIBLE);

        sendOnShow(holder);

        addView(holder.snackView);
        holder.messageView.setText(holder.snack.mMessage);
        if (holder.snack.mActionMessage != null) {
            holder.messageView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setText(holder.snack.mActionMessage);
            holder.button.setCompoundDrawablesWithIntrinsicBounds(holder.snack.mActionIcon, 0, 0, 0);
        } else {
            holder.messageView.setGravity(Gravity.CENTER);
            holder.button.setVisibility(View.GONE);
        }

        if (holder.snack.mBtnTextColor != null) {
            holder.button.setTextColor(holder.snack.mBtnTextColor);
        } else {
            holder.button.setTextColor(getActionTextColor(holder.snack.mStyle));
        }


        if (showImmediately) {
            mInAnimationSet.setDuration(0);
        } else {
            mInAnimationSet.setDuration(ANIMATION_DURATION);
        }
        startAnimation(mInAnimationSet);

        if (holder.snack.mDuration > 0) {
            postDelayed(mHideRunnable, holder.snack.mDuration);
        }

        holder.snackView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int[] location = new int[2];
                        holder.snackView.getLocationInWindow(location);
                        if (y > mPreviousY) {
                            float dy = y - mPreviousY;
                            holder.snackView.offsetTopAndBottom(Math.round(4 * dy));

                            if ((getResources().getDisplayMetrics().heightPixels - location[1]) - 100 <= 0) {
                                removeCallbacks(mHideRunnable);
                                sendOnHide(holder);
                                startAnimation(mOutAnimationSet);

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

    private ColorStateList getActionTextColor(Style style) {
        switch (style) {
            case ALERT:
                return getResources().getColorStateList(R.color.sb__button_text_color_red);
            case INFO:
                return getResources().getColorStateList(R.color.sb__button_text_color_yellow);
            case CONFIRM:
                return getResources().getColorStateList(R.color.sb__button_text_color_green);
            case DEFAULT:
                return getResources().getColorStateList(R.color.sb__default_button_text_color);
            default:
                return getResources().getColorStateList(R.color.sb__default_button_text_color);
        }
    }

    private void sendOnHide(SnackHolder snackHolder) {
        if (snackHolder.visListener != null) {
            snackHolder.visListener.onHide(mSnacks.size());
        }
    }

    private void sendOnShow(SnackHolder snackHolder) {
        if (snackHolder.visListener != null) {
            snackHolder.visListener.onShow(mSnacks.size());
        }
    }

    /*
     * Runnable stuff
     */

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (View.VISIBLE == getVisibility()) {
                startAnimation(mOutAnimationSet);
            }
        }
    };

    /*
     * Restoration *
     */

    public void restoreState(Bundle state, View v) {
        Snack currentSnack = state.getParcelable(SAVED_CURR_MSG);
        if (currentSnack != null) {
            showSnack(currentSnack, v, null, true);
        }

        Parcelable[] messages = state.getParcelableArray(SAVED_MSGS);
        for (Parcelable message : messages) {
            showSnack((Snack) message, v, null, false);
        }
    }

    public Bundle saveState() {
        Bundle outState = new Bundle();
        if (mCurrentSnackHolder != null) {
            outState.putParcelable(SAVED_CURR_MSG, mCurrentSnackHolder.snack);
            mSnacks.remove(mCurrentSnackHolder);
        }

        final int count = mSnacks.size();
        final Snack[] snacks = new Snack[count];
        int i = 0;
        for (SnackHolder holder : mSnacks) {
            snacks[i++] = holder.snack;
        }

        outState.putParcelableArray(SAVED_MSGS, snacks);
        return outState;
    }

    private static class SnackHolder {
        final View snackView;
        final TextView messageView;
        final TextView button;

        final Snack snack;
        final OnVisibilityChangeListener visListener;

        private SnackHolder(Snack snack, View snackView, OnVisibilityChangeListener listener) {
            this.snackView = snackView;
            button = (TextView) snackView.findViewById(R.id.snackButton);
            messageView = (TextView) snackView.findViewById(R.id.snackMessage);

            this.snack = snack;
            visListener = listener;
        }
    }
}
