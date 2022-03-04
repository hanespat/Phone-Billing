package com.phonecompany;

import com.phonecompany.billing.TelephoneBillCalculatorImpl;

import java.math.BigDecimal;

public class Application {
    public static void main ( String [] args ) {
        TelephoneBillCalculatorImpl billCalculator = new TelephoneBillCalculatorImpl();
        BigDecimal result = billCalculator.calculate(
                "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00");
    }
}
