package com.myra.dev.marian.utilities.permissions;

import com.github.m5rian.jdaCommandHandler.Permission;
import com.myra.dev.marian.Config;

public class Marian implements Permission {
    @Override
    public String getName() {
        return "marian";
    }

    @Override
    public String getUserId() {
        return Config.MARIAN_ID;
    }
}
