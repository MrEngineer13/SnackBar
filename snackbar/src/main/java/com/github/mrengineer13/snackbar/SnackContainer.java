package com.github.mrengineer13.snackbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
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

import java.util.LinkedList;
import java.util.Queue;

class SnackContainer extends FrameLayout {

    private static final int ANIMATION_DURATION = 300;

    private static final String SAVED_MSGS = "SAVED_MSGS";

    private Queue<SnackHolder> mSnacks = new LinkedList<SnackHolder>();

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;

    private float mPreviousY;

    public SnackContainer(Context context) {
        super(context);
        init();
    }

    public SnackContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    SnackContainer(ViewGroup container) {
        super(container.getContext());

        container.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setVisibility(View.GONE);
        setId(R.id.snackContainer);
        init();
    }

    private void init() {
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

                if (!mSnacks.isEmpty()) {
                    sendOnHide(mSnacks.poll());
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

    public Snack pollSnack() {
        return mSnacks.poll().snack;
    }

    public void clearSnacks(boolean animate) {
        mSnacks.clear();
        removeCallbacks(mHideRunnable);
        if (animate) mHideRunnable.run();
    }

    /*
     * Showing Logic *
     */

    public boolean isShowing() {
        return !mSnacks.isEmpty();
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
        mSnacks.offer(holder);
        if (mSnacks.size() == 1) showSnack(holder, immediately);
    }

    private void showSnack(final SnackHolder holder) {
        showSnack(holder, false);
    }

    private void showSnack(final SnackHolder holder, boolean showImmediately) {

        setVisibility(View.VISIBLE);

        sendOnShow(holder);

        addView(holder.snackView);
        holder.messageView.setText(holder.snack.mMessage);
        if (holder.snack.mActionMessage != null) {
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setText(holder.snack.mActionMessage);
            holder.button.setCompoundDrawablesWithIntrinsicBounds(holder.snack.mActionIcon, 0, 0, 0);
        } else {
            holder.button.setVisibility(View.GONE);
        }

        holder.button.setTypeface(holder.snack.mTypeface);
        holder.messageView.setTypeface(holder.snack.mTypeface);

        holder.button.setTextColor(holder.snack.mBtnTextColor);
        holder.snackView.setBackgroundColor(holder.snack.mBackgroundColor.getDefaultColor());
        if(holder.snack.mHeight > 0)
            holder.snackView.getLayoutParams().height = this.getPxFromDp(holder.snack.mHeight);

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

                                if (!mSnacks.isEmpty()) {
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
        Parcelable[] messages = state.getParcelableArray(SAVED_MSGS);
        boolean showImmediately = true;

        for (Parcelable message : messages) {
            showSnack((Snack) message, v, null, showImmediately);
            showImmediately = false;
        }
    }

    public Bundle saveState() {
        Bundle outState = new Bundle();

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

    /*
     * Helpers
     */
    private int getPxFromDp(int dp) {
        Resources rs = getResources();
        int pxConverter = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, rs.getDisplayMetrics());
        int px = pxConverter * dp;
        return px;
    }

}
