package com.github.m5rian.myra.utilities.permissions;

import com.github.m5rian.jdaCommandHandler.Permission;
import com.github.m5rian.myra.Config;

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
