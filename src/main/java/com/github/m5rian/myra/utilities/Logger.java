package com.github.m5rian.myra.utilities;

import org.slf4j.LoggerFactory;

public class Logger {

    public static void log(Class clazz, String info) {
        LoggerFactory.getLogger(clazz).info(info);
    }

}
