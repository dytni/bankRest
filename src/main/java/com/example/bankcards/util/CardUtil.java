package com.example.bankcards.util;

import java.util.Random;

public class CardUtil {

    private static final Random RANDOM = new Random();

    public static String generate() {
        StringBuilder builder = new StringBuilder("4444");

        for (int i = 0; i < 11; i++) {
            builder.append(RANDOM.nextInt(10));
        }

        int checkSum = calculateLuhnCheckSum(builder.toString());
        builder.append(checkSum);

        return builder.toString();
    }

    private static int calculateLuhnCheckSum(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
}