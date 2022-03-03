package com.phonecompany.billing;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    public static final int YEAR_SECONDS = 31536000;
    public static final int DAY_SECONDS = 86400;

    public static final int HOUR_SECONDS = 3600;
    public static final int MINUTE_SECONDS = 60;


    public record Time (int hour, int minute, int sec) {}

    public record Date (int day, int month, int year) {
        public String getFormat () {

            String dayString = String.format("%02d", day);
            String monthString = String.format("%02d", month);
            String yearString = String.format("%04d", year);

            return dayString + " " + monthString + " " + yearString;
        }
    }
    public record TimeStamp (Date date, Time time) { }
    public record PhoneLog (String phoneNumber, TimeStamp phoneStart, TimeStamp phoneEnd) {}

    public long getSecondsFromDates (Date first, Date second) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
        String inputString1 = first.getFormat();
        String inputString2 = second.getFormat();

        LocalDate firstDate = LocalDate.parse(inputString1, dateTimeFormat);
        LocalDate secondDate = LocalDate.parse(inputString2, dateTimeFormat);

        long result = ChronoUnit.DAYS.between(firstDate, secondDate);
        if (result <= 0)
            result = 0;

        return result * DAY_SECONDS;
    }

    public long getSecondsFromTimes (Time first, Time second) {

        long phoneStartSeconds = first.hour * HOUR_SECONDS + first.minute * MINUTE_SECONDS + first.sec;
        long phoneEndSeconds = second.hour * HOUR_SECONDS + second.minute * MINUTE_SECONDS + second.sec;

        long result = phoneEndSeconds - phoneStartSeconds;
        if (result <= 0)
            result = 0;

        return result;
    }



    public long getSecondsFromTimestamps (TimeStamp phoneStart, TimeStamp phoneEnd) {

        long dateDifference = getSecondsFromDates(phoneStart.date, phoneEnd.date);
        long timeDifference = getSecondsFromTimes(phoneStart.time, phoneEnd.time);

        long result = dateDifference + timeDifference;

        return result;
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


    @Override
    public BigDecimal calculate(String phoneLog) {

        List<PhoneLog> phoneLogs = new ArrayList<>();
        String [] phoneCalls = phoneLog.split("\\r?\\n");

        for (var phoneCall: phoneCalls) {
            phoneLogs.add(getPhoneLog(phoneCall));
        }

        long result = getSecondsFromTimestamps(phoneLogs.get(0).phoneStart, phoneLogs.get(0).phoneEnd );

        return new BigDecimal(0);
    }
}
