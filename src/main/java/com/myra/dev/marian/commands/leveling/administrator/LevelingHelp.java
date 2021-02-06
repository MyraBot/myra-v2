package com.myra.dev.marian.commands.leveling.administrator;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "leveling",
        requires = Administrator.class
)
public class LevelingHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Check for no arguments
        if (ctx.getArguments().length != 0) return;
        // Send message
        EmbedBuilder help = new EmbedBuilder()
                .setAuthor("leveling", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "leveling set <user> <level>`", "\uD83C\uDFC6 │ Change the level of a user", false)
                .addField("`" + ctx.getPrefix() + "leveling roles`", "\uD83D\uDD17 │ Link a role to a level", false)
                .addField("`" + ctx.getPrefix() + "leveling channel <channel>`", "\uD83E\uDDFE │ Change the channel where level-up messages are sent", false);
        ctx.getChannel().sendMessage(help.build()).queue();
    }
}
