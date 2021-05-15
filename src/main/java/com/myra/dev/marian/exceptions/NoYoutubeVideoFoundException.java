package com.myra.dev.marian.exceptions;

public class NoYoutubeVideoFoundException extends Exception {

    // Parameterless Constructor
    public NoYoutubeVideoFoundException() {
        super("No video matches the provided video id");
    }

}
