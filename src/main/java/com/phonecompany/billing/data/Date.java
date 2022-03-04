package com.phonecompany.billing.data;

public record Date (int day, int month, int year) {
    public String getFormat () {

        String dayString = String.format("%02d", day);
        String monthString = String.format("%02d", month);
        String yearString = String.format("%04d", year);

        return dayString + " " + monthString + " " + yearString;
    }
}
