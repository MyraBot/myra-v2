package com.myra.dev.marian.utilities.permissions;

import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.List;

public class Moderator implements com.github.m5rian.jdaCommandHandler.Permission {
    @Override
    public String getName() {
        return "moderator";
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(Permission.VIEW_AUDIT_LOGS);
    }
}
