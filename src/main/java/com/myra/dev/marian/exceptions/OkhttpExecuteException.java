package com.myra.dev.marian.exceptions;

public class OkhttpExecuteException extends Exception {

    public OkhttpExecuteException(Exception exception) {
        super("Something went wrong while executing the call");
        exception.printStackTrace();
    }

}
