package com.myra.dev.marian.management;

import com.github.m5rian.jdaCommandHandler.VariablePrefix;
import com.myra.dev.marian.database.guild.MongoGuild;
import net.dv8tion.jda.api.entities.Guild;

public class Prefix implements VariablePrefix {
    @Override
    public String getVariablePrefix(Guild guild) {
        return new MongoGuild(guild).getString("prefix");
    }
}
