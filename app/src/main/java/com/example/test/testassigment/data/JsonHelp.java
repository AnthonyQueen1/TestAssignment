package com.example.test.testassigment.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.test.testassigment.R;
import com.example.test.testassigment.data.CalEvent;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by antho on 12/4/2016.
 */

public class JsonHelp {
    private Context mContext;
    private JSONArray jsonArray;
    private List<CalEvent> mList;

    public JsonHelp(Context context){
        mContext = context;
    }

    // gets a JSON string from the JSON file in resources.
    // returns null if there is an error
    @Nullable
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = mContext.getResources().openRawResource(R.raw.academiccalendar);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // gets a list of CalEvents from the JSON string
    public void getListFromJson(){
        mList = new ArrayList<>();
        try{
            jsonArray = new JSONArray(loadJSONFromAsset());
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mList.add(new CalEvent(jsonObject.get("summary").toString(), jsonObject.get("dtstart").toString(), jsonObject.get("dtend").toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // returns the List of Events
    public List<CalEvent> getList(){
        return mList;
    }

    public void deleteEvent(CalEvent calEvent){
        mList.remove(calEvent);
    }

    public void addEvent(CalEvent calEvent){
        mList.add(calEvent);
    }
}
