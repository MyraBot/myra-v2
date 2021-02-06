package com.myra.dev.marian.commands.general.information;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "information",
        aliases = {"info"}
)
public class InformationHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //check for no arguments
        if (ctx.getArguments().length != 0) return;
        EmbedBuilder usage = new EmbedBuilder()
                .setAuthor("information", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().gray)
                .addField("`" + ctx.getPrefix() + "information user <user>`", "\uD83D\uDC64 │ Gives you information about a specific user", false)
                .addField("`" + ctx.getPrefix() + "information server`", "\uD83D\uDDFA │ Gives you information about the server", false)
                .addField("`" + ctx.getPrefix() + "information bot`", "\uD83D\uDD0C │ Gives you information about the bot", false);
        ctx.getChannel().sendMessage(usage.build()).queue();
    }
}
