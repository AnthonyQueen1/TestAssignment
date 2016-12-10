package com.example.test.testassigment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;


import com.example.test.testassigment.data.CalEvent;
import com.example.test.testassigment.utils.SortType;

import java.util.ArrayList;
import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by anthony on 12/2/2016.
 */

public class CalendarFragment extends Fragment  {

    private CalendarAdapter mCalendarAdapter;

    private LinearLayout mLinearLayout;
    private CalPresenter mCalPresenter;

    // empty constructor
    public CalendarFragment(){}

    // to create a new instance of CalendarFragment
    public static CalendarFragment newInstance(){
        return new CalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCalendarAdapter = new CalendarAdapter(new ArrayList<CalEvent>(0), mCalItemListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        mCalPresenter.start();
    }

    // sets the presenter for this fragment
    public void setPresenter(@NonNull CalPresenter presenter){
        mCalPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root =  inflater.inflate(R.layout.cal_frag, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.cal_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(mCalendarAdapter);
        mLinearLayout = (LinearLayout) root.findViewById(R.id.calendarLL);

        setHasOptionsMenu(true);

        // set up floating action button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_main_activity);
        fab.setImageResource(R.drawable.ic_plus);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCalPresenter.addNewEvent();
            }
        });

        return root;
    }

    // shows the calendar with updated list
    public void showCalendar(List<CalEvent> list){
        mCalendarAdapter.replaceData(list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_layout, menu);
    }

    // handles cases for picking each menu item. toggles or changes from none to chosen type. Then updates calendar
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.sort_date:
                if(mCalPresenter.getSortType() == SortType.NONE) mCalPresenter.setSortType(SortType.DATE_FIRST);
                else mCalPresenter.toggleSortType("date");
                break;
            case R.id.sort_title:
                if(mCalPresenter.getSortType() == SortType.NONE) mCalPresenter.setSortType(SortType.TITLE_FIRST);
                else mCalPresenter.toggleSortType("title");
                break;
        }
        mCalPresenter.loadCalendar(false);
        return true;
    }

    // displays toast to show user that there was an error
    public void displayDateReadingError(String error){
        Toast toast = Toast.makeText(getContext(), error, Toast.LENGTH_SHORT);
        toast.show();
    }

    // item listener for each Row in listview.
    CalendarAdapter.CalItemListener mCalItemListener = new CalendarAdapter.CalItemListener() {
        @Override
        public void onItemClick(final CalEvent item) {
            setUpDialog(item, 0);
        }

        @Override
        public void onItemLongPress(CalEvent calEvent) {
            mCalPresenter.deleteEvent(calEvent);
        }
    };

    public void setUpDialog(final CalEvent event, final int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit entry");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.add_dialog, (ViewGroup) getView(), false);
        final EditText titleET = (EditText) viewInflated.findViewById(R.id.title_edit_text);
        final EditText firstET = (EditText) viewInflated.findViewById(R.id.first_time_edit_text);
        final EditText secondET = (EditText) viewInflated.findViewById(R.id.second_time_edit_text);
        builder.setView(viewInflated)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 0 for edit
                        if(type == 0) mCalPresenter.editEvent(titleET.getText().toString(), firstET.getText().toString(), secondET.getText().toString(), event);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    // TODO: modify to put in its own class
    // TODO: change to open a datepicker rather than string
    public void setUpDialogForAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add entry");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.add_dialog, (ViewGroup) getView(), false);
        final EditText titleET = (EditText) viewInflated.findViewById(R.id.title_edit_text);
        final EditText firstET = (EditText) viewInflated.findViewById(R.id.first_time_edit_text);
        final EditText secondET = (EditText) viewInflated.findViewById(R.id.second_time_edit_text);
        builder.setView(viewInflated)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCalPresenter.addEvent(titleET.getText().toString(), firstET.getText().toString(), secondET.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }


    //custom adapter for recyclerview
    private static class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        private List<CalEvent> mList;
        CalItemListener mItemListener;

        public CalendarAdapter(List<CalEvent> list, CalItemListener itemListener){
            setList(list);
            mItemListener = itemListener;
        }

        // update view with current list
        public void replaceData(List<CalEvent> list){
            setList(list);
            notifyDataSetChanged();
        }

        // sets the inner list to the provided list.
        private void setList(List<CalEvent> list){
            mList = checkNotNull(list);
        }


        @Override
        public int getItemCount(){
            return mList.size();
        }

        private CalEvent getItem(int i){
            return mList.get(i);
        }

        @Override
        public long getItemId(int i){
            return i;
        }

        protected static class CalendarViewHolder extends RecyclerView.ViewHolder{
            private CalItemListener mListener;
            private View view;
            private CalEvent calEvent;
            private TextView mTitleTV;
            private TextView mDateTV;
            private TextView mDayOfWeekTV;

            private CalendarViewHolder(View v){
                super(v);
                view = v;
                mTitleTV = (TextView) view.findViewById(R.id.title_text_view);
                mDateTV = (TextView) view.findViewById(R.id.date_of_event_text_view);
                mDayOfWeekTV = (TextView) view.findViewById(R.id.day_of_week_text_view);
                // click listener, opens a dialog to edit
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        mListener.onItemClick(calEvent);
                    }
                });

                // long click listener, deletes an event
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mListener.onItemLongPress(calEvent);
                        return true;
                    }
                });
            }

            private void updateTexts(){
                mDayOfWeekTV.setText(calEvent.getDayOfWeek());
                mDateTV.setText(calEvent.getDates());
                mTitleTV.setText(calEvent.getTitle());
            }
        }

        @Override
        public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new CalendarViewHolder(view);
        }


        @Override
        public void onBindViewHolder(CalendarViewHolder holder, int position){
            holder.mListener = mItemListener;
            holder.calEvent = getItem(position);
            holder.updateTexts();
        }

        public interface CalItemListener {
            void onItemClick(CalEvent item);
            void onItemLongPress(CalEvent calEvent);
        }
    }
}
