package com.codepath.apps.mysimpletweets.helpers;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by kavitha on 10/1/15.
 */
public class Utils {
    public static String cleanupTheTimeStr(String timeStamp) {
        SimpleDateFormat sdf = null;

        long createdTime = 0;
        try {
            sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
            createdTime = sdf.parse(timeStamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String createdTimeStr = (String) DateUtils.getRelativeTimeSpanString(createdTime, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        Log.v("KAVITHA", "time b4 " + timeStamp + " time value: " + createdTimeStr);
        if (createdTimeStr.contains("Yesterday")) {
            createdTimeStr = "1 day ago";
        }
        String[] value = createdTimeStr.split(" ");
        return value[0] + value[1].charAt(0);
    }
}
