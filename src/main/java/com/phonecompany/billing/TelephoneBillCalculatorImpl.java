package com.phonecompany.billing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    public static final int DAY_SECONDS = 86400;
    public static final int HOUR_SECONDS = 3600;
    public static final int MINUTE_SECONDS = 60;


    public record Time (int hour, int minute, int sec) {
        public String getFormat () {

            String hourString = String.format("%02d", hour);
            String minuteString = String.format("%02d", minute);
            String secString = String.format("%02d", sec);

            return hourString + " " + minuteString + " " + secString;
        }
    }

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


    public String getSpecialPhoneNumber ( List<PhoneLog> phoneLogs ) {

        // Create an array that will hold all the records of the phone numbers called
        List<String>phoneNumberRecords = new ArrayList<>();
        phoneLogs.forEach(phoneLog -> phoneNumberRecords.add(phoneLog.phoneNumber));

        // Create an array with only the unique phone numbers (remove duplicates from records)
        List<String> phoneNumbersDistinct = phoneNumberRecords.stream().distinct().collect(Collectors.toList());

        // Create a hash map with the key being the phone number, and the value being the occurrence count
        HashMap<String, Integer>phoneNumberCount = new HashMap<>();
        for (var phoneNumber: phoneNumbersDistinct)
            phoneNumberCount.put(phoneNumber, getPhoneNumberCount(phoneNumberRecords, phoneNumber));

        // Based on the number of occurrences, filter the array to only the phone numbers with the most occurrences (max)
        int maxCount = phoneNumberCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
        Map<String, Integer> filteredPhoneNumberCount = phoneNumberCount.entrySet().stream().filter(entry -> entry.getValue() == maxCount).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        if (phoneNumberCount.size() == 1) // Only one max value
            return phoneNumberCount.keySet().stream().toList().get(0);

        else { // Multiple max values

            // Replace occurrences with arithmetic values
            filteredPhoneNumberCount = filteredPhoneNumberCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> getArithmeticValueOfPhoneNumber(entry.getKey())));

            // Get the result phone number based on the max arithmetic value
            String resultPhoneNumber = filteredPhoneNumberCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
            return resultPhoneNumber;
        }
    }

    private Integer getArithmeticValueOfPhoneNumber(String phoneNumber) {
        int length = phoneNumber.length();
        int sum = 0;
        for (int i = 0; i < length; i++)
            sum += Character.getNumericValue(phoneNumber.charAt(i));
        return sum;
    }


    public double firstCondition ( long seconds, TimeStamp timeStamp ) throws ParseException {
        String leftBound = "07 55 00";
        String rightBound = "15 59 59";

        String leftEdge = "08 00 00";
        String rightEdge = "15 54 59";

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");

        java.util.Date startDate = dateFormat.parse(timeStamp.time.getFormat());
        java.util.Date leftBoundDate = dateFormat.parse(leftBound);
        java.util.Date rightBoundDate = dateFormat.parse(rightBound);

        long leftBoundDifMS = startDate.getTime() - leftBoundDate.getTime();
        long rightBoundDifMS = startDate.getTime() - rightBoundDate.getTime();

        if ( (leftBoundDifMS <= 0) || (rightBoundDifMS >= 0) ) // Outside interval (07:55:00 || 15:59:59)
            return Math.ceil(seconds / 60) * 0.50;


        java.util.Date leftEdgeDate = dateFormat.parse(leftEdge);
        java.util.Date rightEdgeDate = dateFormat.parse(rightEdge);
        long leftEdgeDifMS = startDate.getTime() - leftEdgeDate.getTime();
        long rightEdgeDifMS = startDate.getTime() - rightEdgeDate.getTime();

        if (leftEdgeDifMS < 0) { // To the left of 08:00:00
            long differenceInSeconds = (Math.abs(leftEdgeDifMS) / 1000) % 60;
            if (differenceInSeconds > seconds)
                return (differenceInSeconds * 0.50);
            else
                return (differenceInSeconds * 0.50) +  ((seconds - differenceInSeconds) * 0.20);
        }
        else if (rightEdgeDifMS > 0) { // To the right of 15:54:59
            long differenceInSeconds = (Math.abs(rightEdgeDifMS) / 1000) % 60;
            if (differenceInSeconds > seconds)
                return (differenceInSeconds * 0.20);
            else
                return (differenceInSeconds * 0.20) +  ((seconds - differenceInSeconds) * 0.50);
        }
        else { // Inside the interval
            return Math.ceil(seconds / 60) * 0.20;
        }
    }

    public double secondCondition ( long seconds, TimeStamp timeStamp ) throws ParseException {
        if (seconds - (5 * 60) <= 0)
            return firstCondition(seconds, timeStamp);
        else {
            double result = Math.ceil((seconds - (5 * 60)) / 60);
            return result + firstCondition(5 * 60, timeStamp);
        }
    }

    public List<PhoneLog> removeSpecialPhoneNumber(List<PhoneLog> phoneLogs) {
        String specialPhoneNumber = getSpecialPhoneNumber(phoneLogs);
        return phoneLogs.stream().filter(phoneLog -> !phoneLog.phoneNumber.equals(specialPhoneNumber)).toList();
    }

    @Override
    public BigDecimal calculate(String phoneLog) {

        List<PhoneLog> phoneLogs = new ArrayList<>();
        String [] phoneCalls = phoneLog.split("\\r?\\n");

        for (var phoneCall: phoneCalls) {
            phoneLogs.add(getPhoneLog(phoneCall));
        }

        // Remove special number from calls
        // return the result of calculation of the first and second condition
        phoneLogs = removeSpecialPhoneNumber(phoneLogs);
        double result = 0;
        for (PhoneLog log: phoneLogs) {
            try {
                result += secondCondition(getSecondsFromTimestamps(log.phoneStart, log.phoneEnd), log.phoneStart());
            } catch (ParseException ignored) {
            }

        }
        return new BigDecimal(0);
    }
}
