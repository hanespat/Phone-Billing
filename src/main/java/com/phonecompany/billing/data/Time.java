package com.phonecompany.billing.data;

public record Time (int hour, int minute, int sec) {
    public String getFormat () {

        String hourString = String.format("%02d", hour);
        String minuteString = String.format("%02d", minute);
        String secString = String.format("%02d", sec);

        return hourString + " " + minuteString + " " + secString;
    }
}
