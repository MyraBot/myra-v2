package com.github.m5rian.myra.utilities.permissions;

import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.List;

public class Administrator implements com.github.m5rian.jdaCommandHandler.Permission {
    @Override
    public String getName() {
        return "administrator";
    }

    @Override
    public List<Permission> getPermissions() {
        return Collections.singletonList(Permission.MANAGE_SERVER);
    }
}
