package com.example.test.testassigment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.test.testassigment.data.JsonHelp;
import com.example.test.testassigment.utils.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    private CalPresenter mCalPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(calendarFragment == null){
            // create the fragment
            calendarFragment = CalendarFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), calendarFragment, R.id.content_frame);
        }

        mCalPresenter = new CalPresenter(new JsonHelp(this), calendarFragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
