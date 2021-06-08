package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Language implements CommandHandler {
    @CommandEvent(
            name = "language",
            aliases = {"languages", "lang", "langs"},
            requires = Administrator.class,
            description = "Change the language for your server"
    )
    public void onLanguageCommand(CommandContext ctx) {
        final EmbedBuilder embed = info(ctx).getEmbed(); // Create embed
        Arrays.asList(Lang.Country.values()).forEach(lang -> embed.appendDescription(lang.getFlag() + " " + lang.getNativeName() + "\n"));

        ctx.getChannel().sendMessage(embed.build()).queue(message -> {
            Arrays.asList(Lang.Country.values()).forEach(lang -> {
                // Language has a custom emote
                if (lang.getCodepoints().startsWith("RE:")) {
                    final Emote emote = ctx.getEvent().getJDA().getEmoteById(lang.getCodepoints().replaceAll("\\D", ""));
                    message.addReaction(emote).queue();
                }
                // Language has a normal emoji
                else {
                    message.addReaction(lang.getFlag()).queue();
                }
            });

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getMessageIdLong() == message.getIdLong()
                            && e.getUserIdLong() == ctx.getAuthor().getIdLong())
                    //&& Lang.Country.getFlagsAsCodepoints().contains(e.getReactionEmote().toString()))
                    .setAction(e -> {

                        final String reaction;
                        if (e.getReactionEmote().isEmote()) reaction = e.getReactionEmote().getEmote().toString();
                        else reaction = e.getReactionEmote().getAsCodepoints();

                        Lang.Country language;
                        switch (reaction) {
                            case "U+1f1ebU+1f1f7" -> language = Lang.Country.FRENCH;
                            case "E:Catalan(851830307405299714)" -> language = Lang.Country.CATALAN;
                            case "U+1f1eeU+1f1f9" -> language = Lang.Country.ITALIAN;
                            case "U+1f1e9U+1f1ea" -> language = Lang.Country.GERMAN;

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
