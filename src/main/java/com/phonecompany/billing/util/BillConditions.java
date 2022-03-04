package com.phonecompany.billing.util;

import com.phonecompany.billing.data.TimeStamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class BillConditions {

    private static final String leftOuterEdge = "07 55 00";
    private static final String rightOuterEdge = "15 59 59";

    private static final String leftInnerEdge = "08 00 00";
    private static final String rightInnerEdge = "15 59 59";

    public static double intervalCondition ( long seconds, TimeStamp timeStamp ) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
        java.util.Date startDate, leftOuterDate, rightOuterDate, leftInnerDate, rightInnerDate;

        try {
            startDate = dateFormat.parse(timeStamp.time().getFormat());
            leftOuterDate = dateFormat.parse(leftOuterEdge);
            rightOuterDate = dateFormat.parse(rightOuterEdge);
        }
        catch (ParseException exception) {
            System.err.println("Error parsing date strings during interval duration calculation.");
            return 0;
        }

        long leftBoundDifMS = startDate.getTime() - leftOuterDate.getTime();
        long rightBoundDifMS = startDate.getTime() - rightOuterDate.getTime();

        if ( (leftBoundDifMS <= 0) || (rightBoundDifMS >= 0) ) // Outside interval (07:55:00 || 15:59:59)
            return Math.ceil(seconds / 60F) * 0.50;

        try {
            leftInnerDate = dateFormat.parse(leftInnerEdge);
            rightInnerDate = dateFormat.parse(rightInnerEdge);
        }
        catch (ParseException exception) {
            System.err.println("Error parsing date strings during interval duration calculation.");
            return 0;
        }

        long leftEdgeDifMS = startDate.getTime() - leftInnerDate.getTime();
        long rightEdgeDifMS = startDate.getTime() - rightInnerDate.getTime();

        if (leftEdgeDifMS < 0) { // To the left of 08:00:00
            long differenceInSeconds = (Math.abs(leftEdgeDifMS) / 1000) % 60;
            if (differenceInSeconds > seconds)
                return (Math.ceil(differenceInSeconds / 60F) * 0.50);
            else
                return (Math.ceil(differenceInSeconds / 60F) * 0.50) +  (Math.ceil((seconds - differenceInSeconds)/60F) * 1);
        }
        else if (rightEdgeDifMS < 0) { // To the left of 15:59:59
            long differenceInSeconds = (Math.abs(rightEdgeDifMS) / 1000) % 60;
            if (differenceInSeconds > seconds)
                return (Math.ceil(differenceInSeconds / 60F) * 1);
            else
                return (Math.ceil(differenceInSeconds / 60F) * 1) +  (Math.ceil((seconds - differenceInSeconds)/60F) * 0.50);
        }
        else { // Inside the interval
            return Math.ceil(seconds / 60F) * 1;
        }
    }

    public static double durationCondition ( long seconds, TimeStamp timeStamp ) {
        if (seconds - (5 * 60) <= 0)
            return intervalCondition(seconds, timeStamp);
        else {
            double result = Math.ceil((seconds - (5 * 60)) / 60F) * 0.20;
            return result + intervalCondition(5 * 60, timeStamp);
        }
    }
}
