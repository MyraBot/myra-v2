package com.myra.dev.marian.utilities.permissions;

import com.github.m5rian.jdaCommandHandler.Role;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Administrator implements Role {
    @Override
    public String getName() {
        return "administrator";
    }

    @Override
    public List<Permission> getPermissions() {
        return Collections.singletonList(Permission.ADMINISTRATOR);
    }
}
