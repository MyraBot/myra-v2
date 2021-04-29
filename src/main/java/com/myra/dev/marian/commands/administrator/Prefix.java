package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class Prefix implements CommandHandler {

@CommandEvent(
        name = "prefix",
        requires = Administrator.class,
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("prefix", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "prefix <prefix>`", "\uD83D\uDCCC │ Change the prefix of the bot", false);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
// Change the prefix
        MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        db.setString("prefix", ctx.getArguments()[0]); // Change prefix
        // Success information
        Success success = new Success(ctx.getEvent())
                .setCommand( "prefix")
                .setEmoji("\uD83D\uDCCC")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Prefix changed to `" + ctx.getArguments()[0] + "`");
        success.send();
    }
}
