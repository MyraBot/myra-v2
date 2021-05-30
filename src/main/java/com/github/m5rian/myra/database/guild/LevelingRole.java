package com.github.m5rian.myra.database.guild;

import org.bson.Document;

public class LevelingRole {
    // Variables
    private int level;
    private String role;

    // Constructor
    public LevelingRole(Document levelingRole) {
        this.level = levelingRole.getInteger("level");
        this.role = levelingRole.getString("role");
    }

    public Integer getLevel() {
        return level;
    }

    public String getRole() {
        return role;
    }
}
