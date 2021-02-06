package com.myra.dev.marian.commands.administrator.reactionRoles;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "reaction roles",
        aliases = {"reaction role", "rr"},
        requires = Administrator.class
)
public class ReactionRolesHelp implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("reaction roles", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "reaction roles add <role>`", "\uD83D\uDD17 │ Bind a role to a reaction", false)
                    .addField("`" + ctx.getPrefix() + "reaction roles remove`", "\uD83D\uDDD1 │ Remove a reaction role", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
        }
    }
}
