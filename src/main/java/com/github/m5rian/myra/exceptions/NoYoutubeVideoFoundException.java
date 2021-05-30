package com.github.m5rian.myra.exceptions;

public class NoYoutubeVideoFoundException extends Exception {

    // Parameterless Constructor
    public NoYoutubeVideoFoundException() {
        super("No video matches the provided video id");
    }

}
