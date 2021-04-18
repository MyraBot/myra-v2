package com.myra.dev.marian.utilities.permissions;

import com.github.m5rian.jdaCommandHandler.Role;
import com.myra.dev.marian.Config;

public class Marian implements Role {
    @Override
    public String getName() {
        return "marian";
    }

    @Override
    public String getUserId() {
        return Config.marian;
    }
}
