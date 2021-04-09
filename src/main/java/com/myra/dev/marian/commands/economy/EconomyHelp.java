package com.myra.dev.marian.commands.economy;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "economy",
        requires = Administrator.class
)
public class EconomyHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Usage
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("economy", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "economy set <user> <balance>`", "\uD83D\uDC5B │ Change a users balance", false)
                .addField("`" + ctx.getPrefix() + "economy currency <currency>`", new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency") + " │ Set a custom currency", false);
        ctx.getChannel().sendMessage(usage.build()).queue();
        return;
    }
}
