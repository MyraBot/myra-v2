package com.myra.dev.marian.utilities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Format {

    public static String asVariableName(String string) {
        final String[] words = string.split("\\s+"); // Split string in words

        StringBuilder formattedString = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            final char[] characters = words[i].toLowerCase().toCharArray(); // Convert word to lower case characters
            if (i != 0) {
                characters[0] = String.valueOf(characters[0]).toUpperCase().charAt(0); // Make the first character capital
            }
            formattedString.append(characters);
        }
        return formattedString.toString();
    }

    public static String asDate(long millis) {
        final Instant instant = java.time.Instant.ofEpochMilli(millis);
        final LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String toTime(long millis) {

        long seconds = (millis / 1000L) % 60;
        long minutes = (millis / (1000L * 60L)) % 60;
        long hours = (millis / (1000L * 60L * 60L)) % 24;
        long days = (millis / (1000L * 60L * 60L * 24L)) % 7;
        long weeks = (millis / (1000L * 60L * 60L * 24L * 7L)) % 4;
        long months = (millis / (1000L * 60L * 60L * 24L * 31L));

        if (minutes == 0L) {
            return String.format("%s seconds", seconds);
        } else if (hours == 0L) {
            return String.format("%sm and %ss", minutes, seconds);
        } else if (days == 0L) {
            return String.format("%sh and %sm", hours, minutes);
        } else if (weeks == 0L) {
            return String.format("%sd %sh %sm", days, hours, minutes);
        } else {
            return String.format("%s months", months);
        }

    }
}
