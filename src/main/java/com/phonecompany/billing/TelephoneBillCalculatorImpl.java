package com.phonecompany.billing;

import com.phonecompany.billing.data.PhoneLog;
import java.util.*;
import java.math.BigDecimal;
import static com.phonecompany.billing.data.DataParser.getPhoneLog;
import static com.phonecompany.billing.util.BillConditions.durationCondition;
import static com.phonecompany.billing.util.CalculateSeconds.getSecondsFromTimestamps;
import static com.phonecompany.billing.util.SpecialPhoneNumber.removeSpecialPhoneNumber;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    @Override
    public BigDecimal calculate(String phoneLog) {

        List<PhoneLog> phoneLogs = new ArrayList<>();
        String [] phoneCalls = phoneLog.split("\\r?\\n");

        for (var phoneCall: phoneCalls) {
            phoneLogs.add(getPhoneLog(phoneCall));
        }

        phoneLogs = removeSpecialPhoneNumber(phoneLogs); // Remove logs with special number
        double result = 0;
        for (PhoneLog log: phoneLogs) // Return result prize based on the bill conditions.
            result += durationCondition(getSecondsFromTimestamps(log.phoneStart(), log.phoneEnd()), log.phoneStart());
        return new BigDecimal(result);
    }
}
