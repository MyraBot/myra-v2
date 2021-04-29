package com.myra.dev.marian.listeners.welcome.welcomeDirectMessage;

import com.myra.dev.marian.database.guild.MongoGuild;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

public class WelcomeDirectMessageMessage implements CommandHandler {

@CommandEvent(
        name = "welcome direct message message",
        aliases = {"welcome dm message"},
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder welcomeDirectMessageMessage = new EmbedBuilder()
                    .setAuthor("welcome direct message", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .addField("`" + ctx.getPrefix() + "welcome direct message message <message>`", "\uD83D\uDCAC │ change the text of the direct messages", false)
                    .setFooter("{user} = mention the user │ {server} = server name │ {count} = user count");
            ctx.getChannel().sendMessage(welcomeDirectMessageMessage.build()).queue();
            return;
        }
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Get message
        String message = "";
        for (int i = 0; i < ctx.getArguments().length; i++) {
            message += ctx.getArguments()[i] + " ";
        }
        //remove last space
        message = message.substring(0, message.length() - 1);
        //change value in database
        db.getNested("welcome").setString("welcomeDirectMessage", message);
        //success
        String welcomeMessage = db.getNested("welcome").getString("welcomeDirectMessage");
        Success success = new Success(ctx.getEvent())
                .setCommand("welcome direct message")
                .setEmoji("\u2709\uFE0F")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Welcome message changed to" +
                        "\n" + welcomeMessage
                        .replace("{user}", ctx.getAuthor().getAsMention())
                        .replace("{server}", ctx.getGuild().getName())
                        .replace("{count}", Integer.toString(ctx.getGuild().getMemberCount()))
                );
        success.send();
    }
}
