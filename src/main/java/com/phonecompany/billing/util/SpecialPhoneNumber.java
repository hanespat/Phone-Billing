package com.phonecompany.billing.util;

import com.phonecompany.billing.data.PhoneLog;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Numbers that have been called the most can be ignored when calculating the total prize of phone calls.
 * In case there are more numbers with the same numbers of occurrences, the number is determined
 * by arithmetic value.
 */
public class SpecialPhoneNumber {

    public static List<PhoneLog> removeSpecialPhoneNumber(List<PhoneLog> phoneLogs) {
        String specialPhoneNumber = getSpecialPhoneNumber(phoneLogs);
        return phoneLogs.stream().filter(phoneLog -> !phoneLog.phoneNumber().equals(specialPhoneNumber)).toList();
    }

    public static String getSpecialPhoneNumber ( List<PhoneLog> phoneLogs ) {

        // Create an array that will hold all the records of the phone numbers called
        List<String>phoneNumberRecords = new ArrayList<>();
        phoneLogs.forEach(phoneLog -> phoneNumberRecords.add(phoneLog.phoneNumber()));

        // Create an array with only the unique phone numbers (remove duplicates from records)
        List<String> phoneNumbersDistinct = phoneNumberRecords.stream().distinct().collect(Collectors.toList());

        // Create a hash map with the key being the phone number, and the value being the occurrence count
        HashMap<String, Integer> phoneNumberCount = new HashMap<>();
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

    public static int getPhoneNumberCount(List<String> phoneNumbers, String phoneNumber) {
        int count = 0;
        for (var currentPhoneNumber: phoneNumbers)
            if (currentPhoneNumber.equals(phoneNumber))
                count ++;
        return count;
    }

    private static Integer getArithmeticValueOfPhoneNumber(String phoneNumber) {
        int length = phoneNumber.length();
        int sum = 0;
        for (int i = 0; i < length; i++)
            sum += Character.getNumericValue(phoneNumber.charAt(i));
        return sum;
    }
}
