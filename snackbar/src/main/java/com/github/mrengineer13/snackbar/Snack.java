package com.github.mrengineer13.snackbar;

import android.content.res.ColorStateList;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.mrengineer13.snackbar.SnackBar.Style;

class Snack implements Parcelable {

    final String mMessage;

    final String mActionMessage;

    final int mActionIcon;

    final Parcelable mToken;

    final short mDuration;

    final ColorStateList mBtnTextColor;

    Snack(String message, String actionMessage, int actionIcon,
                 Parcelable token, short duration, ColorStateList textColor) {
        mMessage = message;
        mActionMessage = actionMessage;
        mActionIcon = actionIcon;
        mToken = token;
        mDuration = duration;
        mBtnTextColor = textColor;
    }
    // reads data from parcel
    Snack(Parcel p) {
        mMessage = p.readString();
        mActionMessage = p.readString();
        mActionIcon = p.readInt();
        mToken = p.readParcelable(p.getClass().getClassLoader());
        mDuration = (short) p.readInt();
        mBtnTextColor = p.readParcelable(p.getClass().getClassLoader());
    }

    // writes data to parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mMessage);
        out.writeString(mActionMessage);
        out.writeInt(mActionIcon);
        out.writeParcelable(mToken, 0);
        out.writeInt((int) mDuration);
        out.writeParcelable(mBtnTextColor, 0);
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
