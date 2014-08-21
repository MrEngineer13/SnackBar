package com.mrengineer13.snackbar.sample;

import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mrengineer13.snackbar.SnackBar;


public class SnackBarActivity extends ActionBarActivity {

    public static final String SAVED_SNACKBAR = "SAVED_SNACKBAR";

    public static final String SAVED_COUNT = "SAVED_COUNT";

    private String[] mSnackNames;

    private int mSnackIndex = 0;

    private SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);

        mSnackNames = getResources().getStringArray(R.array.snack_names);
        mSnackBar = new SnackBar(this);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSnackClicked(View v){
        mSnackBar.show(mSnackNames[mSnackIndex++ % mSnackNames.length], SnackBar.MED_SNACK);
    }

    public void onPourSyrupClicked(View v){
        mSnackBar.show(String.format(getString(R.string.syrup_added), mSnackNames[mSnackIndex++ % mSnackNames.length].toLowerCase()), getString(R.string.undo));
        mSnackBar.setOnClickListener(new SnackBar.OnMessageClickListener() {
            @Override
            public void onMessageClick(Parcelable token) {
                mSnackBar.clear();
                mSnackBar.show(getString(R.string.crisis_averted));
            }
        });
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
