package com.phonecompany.billing.data;

/**
 * Helper class for parsing date/time information from string
 */
public class DataParser {

    public static Time getTime (String time) {
        String [] timeParts = time.split(":");

        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int sec = Integer.parseInt(timeParts[2]);

        return new Time(hour, minute, sec);
    }

    public static Date getDate (String date) {
        String [] dateParts = date.split("-");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        return new Date(day, month, year);
    }


    public static PhoneLog getPhoneLog (String phoneLog) {
        String [] logParts = phoneLog.split(",");
        return new PhoneLog(logParts[0], getTimeStamp(logParts[1]), getTimeStamp(logParts[2]));
    }

    public static TimeStamp getTimeStamp (String timeStamp) {
        String [] parts = timeStamp.split(" ");

        String date = parts[0];
        String time = parts[1];

        return new TimeStamp(getDate(date), getTime(time));
    }
}
