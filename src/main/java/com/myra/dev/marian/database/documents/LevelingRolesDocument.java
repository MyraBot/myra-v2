package com.myra.dev.marian.database.documents;

import org.bson.Document;

public class LevelingRolesDocument {
    // Variables
    private int level;
    private String role;

    // Constructor
    public LevelingRolesDocument(Document levelingRole) {
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
