package com.example.test.testassigment;

/**
 * Created by antho on 12/4/2016.
 */

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Listens to user actions from the UI CalendarFragment, retrieves the data and updates the
 * UI as required.
 */
public class CalPresenter {
    private JsonHelp mJsonHelp;
    private CalendarFragment mCalendarFragment;
    private boolean mFirstLoad = true;
    private SortType mSortType = SortType.NONE;

    public CalPresenter(@NonNull JsonHelp jsonHelp, @NonNull CalendarFragment calendarFragment){
        mJsonHelp = checkNotNull(jsonHelp);
        mCalendarFragment = checkNotNull(calendarFragment);
        mCalendarFragment.setPresenter(this);
    }

    public void start(){
        loadCalendar(mFirstLoad);
    }

    public void loadCalendar(boolean forceUpdate) {
        if(forceUpdate)
            mJsonHelp.getListFromJson();

        // only for first time loading
        mFirstLoad = false;
        List<CalEvent> mList = mJsonHelp.getList();
        switch(mSortType){
            case DATE_FIRST:
                Collections.sort(mList, CalEvent.Comparators.DATE);
                break;
            case DATE_LAST:
                Collections.sort(mList, Collections.reverseOrder(CalEvent.Comparators.DATE));
                break;
            case TITLE_FIRST:
                Collections.sort(mList, CalEvent.Comparators.TITLE);
                break;
            case TITLE_LAST:
                Collections.sort(mList, Collections.reverseOrder(CalEvent.Comparators.TITLE));
                break;
            default:
                break;
        }

        processCalendar(mList);
    }

    private void processCalendar(List<CalEvent> list){
        mCalendarFragment.showCalendar(list);
    }

    public void setSortType(SortType requestType){
        mSortType = requestType;
    }

    public void toggleSortType(String sortType){
        switch(sortType) {
            case "date":
                if (mSortType == SortType.DATE_FIRST) mSortType = SortType.DATE_LAST;
                else mSortType = SortType.DATE_FIRST;
                break;
            case "title":
                if (mSortType == SortType.TITLE_FIRST) mSortType = SortType.TITLE_LAST;
                else mSortType = SortType.TITLE_FIRST;
                break;
        }
    }

    public SortType getSortType(){
        return mSortType;
    }


    public void deleteEvent(CalEvent calEvent){
        mJsonHelp.deleteEvent(calEvent);
        loadCalendar(false);
    }

    public void editEvent(String title, String date1, String date2, CalEvent event){
        if(!title.isEmpty())
            event.setTitle(title);
        if(!date1.isEmpty()){
            try{
                event.setDate1(date1);
            } catch (Exception e){
                mCalendarFragment.displayDateReadingError();
            }
        }
        if(!date2.isEmpty()){
            try{
                event.setDate2(date2);
            } catch (Exception e){
                mCalendarFragment.displayDateReadingError();
            }
        }
        loadCalendar(false);
    }

    public void addEvent(String title, String date1, String date2){
        CalEvent temp;
        if(CalEvent.isValidInput(date1) && CalEvent.isValidInput(date2)) {
            temp = new CalEvent(title, date1, date2);
            mJsonHelp.addEvent(temp);
            loadCalendar(false);
        } else {
            mCalendarFragment.displayDateReadingError();
        }
    }
}