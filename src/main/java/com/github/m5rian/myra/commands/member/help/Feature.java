package com.github.m5rian.myra.commands.member.help;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.commands.member.general.Suggest;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.entities.TextChannel;

public class Feature implements CommandHandler {
    @CommandEvent(
            name = "feature",
            aliases = {"submit"},
            emoji = "\uD83D\uDCCC",
            description = "description.help.feature",
            args = {"<feature description>"}
    )
    public void execute(final CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("format")
                    .addUsages(new Usage()
                            .setUsage("feature <feature description>")
                            .setEmoji("\uD83D\uDCCC")
                            .setDescription(Lang.lang(ctx).get("description.general.feature")))
                    .addInformation(Lang.lang(ctx).get("command.help.feature.info"))
                    .send();
            return;
        }

        final TextChannel channel = ctx.getBot().getGuildById(Config.MARIAN_SERVER_ID).getTextChannelById(Config.CHANNEL_SUGGESTIONS);
        Suggest.sendSuggestion(ctx, channel);
        info(ctx).setDescription(Lang.lang(ctx).get("command.help.feature.success")).send();
    }
}
