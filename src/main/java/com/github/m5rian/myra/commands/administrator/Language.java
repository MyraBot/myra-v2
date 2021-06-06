package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Language implements CommandHandler {
    private final String[] reactions = {
            "\uD83C\uDDEC\uD83C\uDDE7", // England
            "\uD83C\uDDEB\uD83C\uDDF7", // France
            "\uD83C\uDDEA\uD83C\uDDF8" // Catalonia
    };

    @CommandEvent(
            name = "language",
            aliases = {"languages", "lang", "langs"},
            requires = Administrator.class,
            description = "Change the language for your server"
    )
    public void onLanguageCommand(CommandContext ctx) {
        final EmbedBuilder embed = info(ctx).setDescription("""
                \uD83C\uDDEC\uD83C\uDDE7 English
                \uD83C\uDDEB\uD83C\uDDF7 French
                \uD83C\uDDEA\uD83C\uDDF8 Catalonian""")
                .getEmbed();
        ctx.getChannel().sendMessage(embed.build()).queue(message -> {
            message.addReaction(reactions[0]).queue();
            message.addReaction(reactions[1]).queue();
            message.addReaction(reactions[2]).queue();

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getMessageIdLong() == message.getIdLong()
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong()
                            && Arrays.asList(reactions).contains(e.getReactionEmote().getEmoji()))
                    .setAction(e -> {
                        final String reaction = e.getReactionEmote().getEmoji();


                        Lang.Country language;
                        switch (reaction) {
                            case "\uD83C\uDDEB\uD83C\uDDF7" -> language = Lang.Country.FRENCH;
                            case "\uD83C\uDDEA\uD83C\uDDF8" -> language = Lang.Country.CATALAN;

                            default -> language = Lang.Country.ENGLISH;
                        }

                        new MongoGuild(ctx.getGuild()).setString("lang", language.getId()); // Update language
                        Lang.languages.put(ctx.getGuild().getId(), language); // Update language cache

                        info(ctx).setDescription("Changed language to " + language.getName()).send();
                    })
                    .setTimeout(30, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }
}
