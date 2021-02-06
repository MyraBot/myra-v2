package com.myra.dev.marian.listeners.welcome.welcomeEmbed;

import com.myra.dev.marian.database.allMethods.Database;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandSubscribe(
        name = "welcome embed message",
        requires = Administrator.class
)
public class WelcomeEmbedMessage implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Utilities
        Utilities utilities = Utilities.getUtils();
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder welcome = new EmbedBuilder()
                    .setAuthor("welcome embed message", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "welcome embed message <message>`", "\uD83D\uDCAC │ Set the text of the embed message", false)
                    .setFooter("{user} = mention the user │ {server} = server name │ {count} = user count");
            ctx.getChannel().sendMessage(welcome.build()).queue();
            return;
        }
        // Get message
        String message = utilities.getString(ctx.getArguments());
        // Get database
        Database db = new Database(ctx.getGuild());
        // Update database
        db.getNested("welcome").setString("welcome", message);
        // Success
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome embed message")
                .setEmoji("\uD83D\uDCAC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Welcome text changed to" +
                        "\n" + message
                        .replace("{user}", ctx.getAuthor().getAsMention())
                        .replace("{server}", ctx.getGuild().getName())
                        .replace("{count}", Integer.toString(ctx.getGuild().getMemberCount()))
                );
        success.send();
    }
}