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

package com.mrengineer13.snackbar.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.github.mrengineer13.about.AboutActivity;
import com.github.mrengineer13.snackbar.SnackBar;


public class SnackBarActivity extends ActionBarActivity {

    public static final String SAVED_SNACKBAR = "SAVED_SNACKBAR";

    public static final String SAVED_COUNT = "SAVED_COUNT";

    static final int SHORT_MSG = 0, LONG_MSG = 1;

    static final int SHORT_SNACK = 0, MED_SNACK = 1, LONG_SNACK = 2;

    static final int RED = 0, ORANGE = 1, YELLOW = 2, GREEN = 3, BLUE = 4, PURPLE = 5, PINK = 6, DEFAULT = 7, ALERT = 8, CONFIRM = 9, INFO = 10;

    static final int ACTION_BTN = 0, NO_ACTION_BTN = 1;

    private Spinner mMsgLengthOptions, mDurationOptions, mActionBtnOptions, mActionBtnColorOptions;

    private String[] mSnackNames;

    private int mSnackIndex = 0;

    private SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);

        mSnackNames = getResources().getStringArray(R.array.snack_names);
        mSnackBar = new SnackBar(this);

        mMsgLengthOptions = (Spinner) findViewById(R.id.message_length_selector);
        mDurationOptions = (Spinner) findViewById(R.id.snack_duration_selector);
        mActionBtnOptions = (Spinner) findViewById(R.id.action_btn_presence_selector);
        mActionBtnColorOptions = (Spinner) findViewById(R.id.action_btn_color);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.snack_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(AboutActivity.getAboutActivityIntent(this, "MrEngineer13","https://github.com/MrEngineer13",
                    "MrEngineer13@live.com","@MrEngineer13", "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K6PSQMTJYG5VJ",
                    true, "MrEngineer13", null));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateClicked(View view){
        String message = "";
        short duration = 0;
        SnackBar.Style style = SnackBar.Style.DEFAULT;

        int selectedMessageLength = mMsgLengthOptions.getSelectedItemPosition();
        switch (selectedMessageLength){
            case SHORT_MSG:
                message = "This is a one-line message.";
                break;
            case LONG_MSG:
                message = "This message is a lot longer, it should stretch at least two lines. ";
                break;
        }

        int selectedDuration = mDurationOptions.getSelectedItemPosition();
        switch (selectedDuration){
            case SHORT_SNACK:
                duration = SnackBar.SHORT_SNACK;
                break;
            case MED_SNACK:
                duration = SnackBar.MED_SNACK;
                break;
            case LONG_SNACK:
                duration = SnackBar.LONG_SNACK;
                break;
        }

        int selectedActionBtnColor = mActionBtnColorOptions.getSelectedItemPosition();
        switch (selectedActionBtnColor){
            case RED:
                style = SnackBar.Style.RED;
                break;
            case ORANGE:
                style = SnackBar.Style.ORANGE;
                break;
            case YELLOW:
                style = SnackBar.Style.YELLOW;
                break;
            case GREEN:
                style = SnackBar.Style.GREEN;
                break;
            case BLUE:
                style = SnackBar.Style.BLUE;
                break;
            case PURPLE:
                style = SnackBar.Style.PURPLE;
                break;
            case PINK:
                style = SnackBar.Style.PINK;
                break;
            case DEFAULT:
                style = SnackBar.Style.DEFAULT;
                break;
            case ALERT:
                style = SnackBar.Style.ALERT;
                break;
            case CONFIRM:
                style = SnackBar.Style.CONFIRM;
                break;
            case INFO:
                style = SnackBar.Style.INFO;
                break;
        }

        int selectedActionBtnExistance = mActionBtnOptions.getSelectedItemPosition();
        switch (selectedActionBtnExistance){
            case ACTION_BTN:
                mSnackBar.show(message, "Action", style, duration);
                break;
            case NO_ACTION_BTN:
                mSnackBar.show(message, duration);
                break;
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        // use this to save your snacks for later
        saveState.putBundle(SAVED_SNACKBAR, mSnackBar.onSaveInstanceState());
        // just for saving the number of times the button has been pressed
        saveState.putInt(SAVED_COUNT, mSnackIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
        // use this to load your snacks for later
        mSnackBar.onRestoreInstanceState(loadState.getBundle(SAVED_SNACKBAR));
        // might as well load the counter too
        mSnackIndex = loadState.getInt(SAVED_COUNT);
    }
}
