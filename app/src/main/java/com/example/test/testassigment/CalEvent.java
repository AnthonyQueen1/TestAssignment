package com.example.test.testassigment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;


/**
 * Created by antho on 12/3/2016.
 */

public final class CalEvent {

    private static final SimpleDateFormat indateformat = new SimpleDateFormat("M/d/yyyy");
    private static final SimpleDateFormat outdateformat = new SimpleDateFormat("MMM d, yyyy");
    private static final SimpleDateFormat outday = new SimpleDateFormat("EEEE");

    private final String[] months = {"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sept", "Nov", "Dec"};
    private final String[] days   = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @NonNull
    private String mTitle;
    private final String mId;
    private Date mDate1;

    private Date mDate2;

    public CalEvent (@NonNull String title, String time1, String time2){
        mId = UUID.randomUUID().toString();
        mTitle = title;
        try{
            mDate1 = indateformat.parse(time1);
        } catch (Exception e){
            mDate1 = null;
        }
        try{
            mDate2 = indateformat.parse(time2);
        } catch (Exception e){
            mDate2 = null;
        }
    }

    public String getTitle(){
        return mTitle;
    }

    public String getDates(){
        if(mDate1 == null || mDate2 == null) return "Error parsing dates";

        if(mDate1.compareTo(mDate2) == 0) return outdateformat.format(mDate1);
        return outdateformat.format(mDate1) + " - " + outdateformat.format(mDate2);
    }

    public String getDayOfWeek(){
        if(mDate1 == null || mDate2 == null) return "Error parsing dates";
        if(mDate1.compareTo(mDate2) == 0) return outday.format(mDate1);
        return outday.format(mDate1) + " - " + outday.format(mDate2);
    }

    public static boolean isValidInput(String s){
        try {
            indateformat.parse(s);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public void setTitle(String title){mTitle = title; }
    public void setDate1(String date) throws ParseException {
        mDate1 = indateformat.parse(date);
    }
    public void setDate2(String date) throws ParseException {
        mDate2 = indateformat.parse(date);
    }

    public static class Comparators {
        public static Comparator<CalEvent> TITLE = new Comparator<CalEvent>() {
            @Override
            public int compare(CalEvent calEvent, CalEvent t1) {
                return calEvent.mTitle.compareTo(t1.mTitle);
            }
        };
        public static Comparator<CalEvent> DATE = new Comparator<CalEvent>() {
            @Override
            public int compare(CalEvent calEvent, CalEvent t1) {
                if(calEvent.mDate1.compareTo(t1.mDate1) == 0)
                    return calEvent.mDate2.compareTo(t1.mDate2);
                return calEvent.mDate1.compareTo(t1.mDate1);
            }
        };
    }
}
