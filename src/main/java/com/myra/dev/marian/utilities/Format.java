package com.myra.dev.marian.utilities;

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
}
