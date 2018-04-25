package com.codedotman.drugreminder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.codedotman.drugreminder.Data.ReminderContract;

/**
 * Created by USER on 19/04/2018.
 */

public class CustomCursorAdapter extends CursorAdapter {

    private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;

    public CustomCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reminder_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mTitleText = (TextView) view.findViewById(R.id.list_title);
        mDateAndTimeText = (TextView) view.findViewById(R.id.list_date_time);
        mRepeatInfoText = (TextView) view.findViewById(R.id.list_repeat_info);


        int titleColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_REPEAT_NO);
        int repeatTypeColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE);
        int activeColumnIndex = cursor.getColumnIndex(ReminderContract.AlarmReminderEntry.KEY_ACTIVE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String active = cursor.getString(activeColumnIndex);

        String dateTime = date + " " + time;


        setReminderTitle(title);
        setReminderDateTime(dateTime);
        setReminderRepeatInfo(repeat, repeatNo, repeatType);





    }

    // Set reminder title view
    public void setReminderTitle(String title) {
        mTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

    }

    // Set date and time views
    public void setReminderDateTime(String datetime) {
        mDateAndTimeText.setText(datetime);
    }

    // Set repeat views
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Repeat Off");
        }
    }



}
