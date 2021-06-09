package com.github.m5rian.myra.utilities;

import net.dv8tion.jda.api.entities.Guild;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

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

    public static String toTimeExact(long millis) {
        final long seconds = (millis / 1000) % 60;
        final long minutes = (millis / (1000 * 60)) % 60;
        final long hours = (millis / (1000 * 60 * 60)) % 24;
        final long days = (millis / (1000 * 60 * 60 * 24));

        return String.format("%02dd %02dh %02dmin %02ds", days, hours, minutes, seconds);
    }

    public static String toTime(long millis, Guild guild) {
        List<Long> times = new ArrayList<>() {{
            long seconds = millis / 1000;

            final long months = seconds / 2629743;
            seconds -= months * 2629743;

            final long weeks = seconds / 604800;
            seconds -= weeks * 604800;

            final long days = seconds / 86400;
            seconds -= days * 86400;

            final long hours = seconds / 3600;
            seconds -= hours * 3600;

            final long minutes = seconds / 60;
            seconds -= minutes * 60;

            add(months);
            add(weeks);
            add(days);
            add(hours);
            add(minutes);
            add(seconds);
        }};

        StringBuilder output = new StringBuilder();
        final String wordAnd = lang(guild).get("word.and");
        for (int i = 0; i < times.size(); i++) {
            final long duration = times.get(i);

            if (duration == 0) continue;
            if (output.toString().contains(wordAnd)) break;
            if (output.length() > 0) output.append(" and ");

            output.append(duration).append(" ");  // Append duration

            String suffix = "s";
            if (duration == 1) suffix = "";

            // Append time unit
            switch (i) {
                case 0 -> output.append(lang(guild).get("word.timeunit.month" + suffix));
                case 1 -> output.append(lang(guild).get("word.timeunit.week" + suffix));
                case 2 -> output.append(lang(guild).get("word.timeunit.day" + suffix));
                case 3 -> output.append(lang(guild).get("word.timeunit.hour" + suffix));
                case 4 -> output.append(lang(guild).get("word.timeunit.minute" + suffix));
                case 5 -> output.append(lang(guild).get("word.timeunit.second" + suffix));
            }

        }

        if (output.length() == 0) output.append(lang(guild).get("word.none"));
        return output.toString();

        /*List<Time> times = new ArrayList<>() {{
            Time seconds = new Time("seconds", millis / 1000);

            Time months = new Time("months", seconds.duration / 2629743);
            seconds.subtract(months.duration * 2629743);

            Time weeks = new Time("weeks", seconds.duration / 604800);
            seconds.subtract(weeks.duration * 604800);

            Time days = new Time("days", seconds.duration / 86400);
            seconds.subtract(days.duration * 86400);

            Time hours = new Time("hours", seconds.duration / 3600);
            seconds.subtract(hours.duration * 3600);

            Time minutes = new Time("minutes", seconds.duration / 60);
            seconds.subtract(minutes.duration * 60);

            add(months);
            add(weeks);
            add(days);
            add(hours);
            add(minutes);
            add(seconds);
        }};

        final StringBuilder output = new StringBuilder();
        for (Time time : times) {
            if (time.duration == 0) continue; // Duration is 0
            if (output.toString().contains("and")) continue; // There are already at least 2 values

            if (!output.toString().equals("")) output += duration + " and ");
            if (time.duration != 1) output += duration + time.duration).append(" ").append(time.timeUnit);
            else output += duration + time.duration).append(" ").append(time.timeUnit).substring(0, output.length() - 1);
        }
        if (output.isEmpty()) output += duration + "none");

        return output.toString();*/
    }

    public static class Time {
        public final String timeUnit;
        public long duration;

        public Time(String timeUnit, long duration) {
            this.timeUnit = timeUnit;
            this.duration = duration;
        }

        public void subtract(long amount) {
            this.duration -= amount;
        }
    }

    /**
     * Add '.' separators to show the number more nicely.
     *
     * @param number The number to format.
     * @return Returns the formatted number as a String.
     */
    public static String number(int number) {
        return NumberFormat.getInstance().format(number);
    }
}
