package com.phonecompany.billing.util;

import com.phonecompany.billing.data.Date;
import com.phonecompany.billing.data.Time;
import com.phonecompany.billing.data.TimeStamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CalculateSeconds {

    public static final int DAY_SECONDS = 86400;
    public static final int HOUR_SECONDS = 3600;
    public static final int MINUTE_SECONDS = 60;

    public static long getSecondsFromDates (Date first, Date second) {
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

    public static long getSecondsFromTimes (Time first, Time second) {
        long phoneStartSeconds = first.hour() * HOUR_SECONDS + first.minute() * MINUTE_SECONDS + first.sec();
        long phoneEndSeconds = second.hour() * HOUR_SECONDS + second.minute() * MINUTE_SECONDS + second.sec();

        long result = phoneEndSeconds - phoneStartSeconds;
        if (result <= 0)
            result = 0;

        return result;
    }


    public static long getSecondsFromTimestamps (TimeStamp phoneStart, TimeStamp phoneEnd) {
        long dateDifference = getSecondsFromDates(phoneStart.date(), phoneEnd.date());
        long timeDifference = getSecondsFromTimes(phoneStart.time(), phoneEnd.time());

        long result = dateDifference + timeDifference;

        return result;
    }
}
