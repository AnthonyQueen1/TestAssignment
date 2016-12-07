package com.example.test.testassigment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;


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

        ListView listView = (ListView) root.findViewById(R.id.cal_list);
        listView.setAdapter(mCalendarAdapter);
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
                        // 1 for add
                        if(type == 1) mCalPresenter.addEvent(titleET.getText().toString(), firstET.getText().toString(), secondET.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

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



    private static class CalendarAdapter extends BaseAdapter {
        private List<CalEvent> mList;
        private CalItemListener mItemListener;

        public CalendarAdapter(List<CalEvent> list, CalItemListener itemListener){
            setList(list);
            mItemListener = itemListener;
        }

        public void replaceData(List<CalEvent> list){
            setList(list);
            notifyDataSetChanged();
        }

        private void setList(List<CalEvent> list){
            mList = checkNotNull(list);
        }


        @Override
        public int getCount(){
            return mList.size();
        }

        @Override
        public CalEvent getItem(int i){
            return mList.get(i);
        }

        @Override
        public long getItemId(int i){
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup){
            View rowView = view;
            if(rowView == null){
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.item, viewGroup, false);
            }

            final CalEvent calEvent = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(calEvent.getTitle());
            TextView daysTV = (TextView) rowView.findViewById(R.id.date_of_event);
            daysTV.setText(calEvent.getDates());
            TextView weekdayTV = (TextView) rowView.findViewById(R.id.day_of_week);
            weekdayTV.setText(calEvent.getDayOfWeek());
            rowView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mItemListener.onItemClick(calEvent);
                }
            });
            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mItemListener.onItemLongPress(calEvent);
                    return true;
                }
            });


            return rowView;
        }

        public interface CalItemListener {
            void onItemClick(CalEvent item);
            void onItemLongPress(CalEvent calEvent);
        }
    }
}
