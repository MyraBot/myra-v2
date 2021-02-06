package com.myra.dev.marian.database.documents;

import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

public class MemberDocument {
    private String id;

    private int level;
    private int xp;
    private int balance;

    public MemberDocument(Document memberDocument, Guild guild) {
        this.id = memberDocument.getString("userId");

        final Document guildMemberDocument = (Document) memberDocument.get(guild.getId()); // Get document of guild
        this.level = guildMemberDocument.getInteger("level");
        this.xp = guildMemberDocument.getInteger("xp");
        this.balance = guildMemberDocument.getInteger("balance");
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getBalance() {
        return balance;
    }
}