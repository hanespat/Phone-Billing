package com.phonecompany.billing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    public record Time (int hour, int minute, int sec) {}
    public record Date (int day, int month, int year) {}
    public record TimeStamp (Date date, Time time) {}
    public record PhoneLog (String phoneNumber, TimeStamp phoneStart, TimeStamp phoneEnd) {}

    public static final int YEAR_SECONDS = 31536000;
    public static final int MONTH_SECONDS = ;
    public static final int DAY_SECONDS = 86400;

    public static final int HOUR_SECONDS = 3600;
    public static final int MINUTE_SECONDS = 60;

    public BigInteger getSeconds (TimeStamp phoneStart, TimeStamp phoneEnd) {
        long years = phoneEnd.date.year - phoneStart.date.year;
        long months = phoneEnd.date.month - phoneStart.date.month;
        long days = phoneEnd.date.day - phoneStart.date.day;

        long hours= phoneEnd.time.hour - phoneStart.time.hour;
        long minutes = phoneEnd.time.minute - phoneStart.time.minute;
        long seconds = phoneEnd.time.sec - phoneStart.time.sec;

        long result = years *

        return new BigInteger(0);
    }


    public Time getTime (String time) {
        String [] timeParts = time.split(":");

        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int sec = Integer.parseInt(timeParts[2]);

        return new Time(hour, minute, sec);
    }

    public Date getDate (String date) {
        String [] dateParts = date.split("-");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        return new Date(day, month, year);
    }


    public PhoneLog getPhoneLog (String phoneLog) {
        String [] logParts = phoneLog.split(",");
        return new PhoneLog(logParts[0], getTimeStamp(logParts[1]), getTimeStamp(logParts[2]));
    }

    public TimeStamp getTimeStamp (String timeStamp) {
        String [] parts = timeStamp.split(" ");

        String date = parts[0];
        String time = parts[1];

        return new TimeStamp(getDate(date), getTime(time));
    }



    public int getPhoneNumberCount(List<String> phoneNumbers, String phoneNumber) {
        int count = 0;
        for (var currentPhoneNumber: phoneNumbers)
            if (currentPhoneNumber.equals(phoneNumber))
                count ++;
        return count;
    }

    public String getMaxCall ( List<PhoneLog> phoneLogs ) {
        List<String>phoneNumbers = new ArrayList<>();
        phoneLogs.forEach(phoneLog -> phoneNumbers.add(phoneLog.phoneNumber));

        HashMap<String, Integer>phoneNumberCount = new HashMap<>();
        List<String> phoneNumbersDistinct = phoneNumbers.stream().distinct().collect(Collectors.toList());

        for (var phoneNumber: phoneNumbersDistinct)
            phoneNumberCount.put(phoneNumber, getPhoneNumberCount(phoneNumbers, phoneNumber));



        return "";
    }

    /**
     * Get array of phoneNumbers
     *
     */

    @Override
    public BigDecimal calculate(String phoneLog) {

        List<PhoneLog> phoneLogs = new ArrayList<>();
        String [] phoneCalls = phoneLog.split("\\r?\\n");

        for (var phoneCall: phoneCalls) {
            phoneLogs.add(getPhoneLog(phoneCall));
        }

        return new BigDecimal(0);
    }
}
