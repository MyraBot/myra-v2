package com.myra.dev.marian.commands.economy.administrator;

import com.myra.dev.marian.database.guild.MongoGuild;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class Currency implements CommandHandler {

@CommandEvent(
        name = "economy currency",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Get database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("leveling currency", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "economy currency <emoji>`", db.getNested("economy").getString("currency") + " │ Set a custom currency", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
        /**
         * Change currency
         */
        // Get new currency
        String currency = "";
        for (String argument : ctx.getArguments()) {
            currency += argument + " ";
        }
        //remove last space
        currency = currency.substring(0, currency.length() - 1);
        // Update database
        db.getNested("economy").setString("currency", currency);
        // Send success message
        Success success = new Success(ctx.getEvent())
                .setCommand("economy currency")
                .setEmoji(currency)
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage("Changed currency to " + currency);
        success.send();
    }
}
