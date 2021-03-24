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
}
