package com.github.m5rian.myra.commands.member;

import ch.qos.logback.core.db.dialect.MsSQLDialect;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.LeaderboardType;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.LeaderboardMember;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Leaderboard implements CommandHandler {
    // I needed to use this weird format on the normal emoji, because otherwise I wouldn't be able to use the Arrays.stream thing below in the conditions of the event waiter
    final String[] emojis = {
            "RE:U+1F3C6", // Leveling (🏆)
            CustomEmoji.COIN.getAsReactionEmote(), // Balance
            "RE:U+1F4DE" // Voice call
    };

    @CommandEvent(
            name = "leaderboard",
            aliases = {"lb", "top"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Create loading embed
        Success loading = new Success(ctx.getEvent())
                .setCommand("leaderboard")
                .setEmoji("\uD83E\uDD47")
                .setHyperLink(Config.SERVER_ADDRESS + "/leaderboard/" + ctx.getGuild().getId()) // Online leaderboard
                .setMessage(Lang.lang(ctx).get("word.loading"));
        // Send message
        ctx.getChannel().sendMessage(loading.getEmbed().build()).queue(message -> {
            // Add reactions
            message.addReaction("\uD83C\uDFC6").queue(); // Add level emoji
            message.addReaction(CustomEmoji.COIN.getAsEmote()).queue(); // Add balance emote
            message.addReaction("\uD83D\uDCDE").queue(); // Voice call emoji

            final MessageEmbed levelLeaderboard = renderLeaderboard(type.LEVEL, ctx.getGuild()).build(); // Get level leaderboard
            final MessageEmbed balanceLeaderboard = renderLeaderboard(type.BALANCE, ctx.getGuild()).build(); // Get balance leaderboard
            final MessageEmbed voiceLeaderboard = renderLeaderboard(type.VOICE, ctx.getGuild()).build(); // Get voice leaderboard

            message.editMessage(levelLeaderboard).queue(); // Display by default level leaderboard

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getMember().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.asList(emojis).stream().anyMatch(emoji -> emoji.equalsIgnoreCase(e.getReactionEmote().toString())))
                    .setAction(e -> {
                        message.editMessage(loading.getEmbed().build()).queue(); // Edit to loading message
                        final String reaction = e.getReactionEmote().toString().toUpperCase(); // Get reacted reaction emote

                        // Balance leaderboard
                        if (e.getReactionEmote().isEmote()) message.editMessage(balanceLeaderboard).queue();
                            // Level leaderboard
                        else if (reaction.equals(emojis[0])) message.editMessage(levelLeaderboard).queue();
                            // Voice leaderboard
                        else if (reaction.equals(emojis[2])) message.editMessage(voiceLeaderboard).queue();

                        e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                    })
                    .setTimeout(30L, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .remainsOnAction()
                    .load();
        });
    }

    private enum type {
        LEVEL,
        BALANCE,
        VOICE
    }

    private EmbedBuilder renderLeaderboard(type type, Guild guild) {
        final EmbedBuilder leaderboard = new EmbedBuilder()// Create embed builder for leaderboard
                .setAuthor("leaderboard", Config.SERVER_ADDRESS + "/leaderboard/" + guild.getId(), guild.getIconUrl())
                .setColor(Utilities.blue);

        // Get leaderboard with all members
        final List<LeaderboardMember> leaderboardMembers;
        switch (type) {
            // Balance
            case BALANCE -> {
                leaderboardMembers = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.BALANCE);
                leaderboard.setDescription(lang(guild).get("command.leaderboard.balance") + "\n");
            }
            // Voice call time
            case VOICE -> {
                leaderboardMembers = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.VOICE);
                leaderboard.setDescription(lang(guild).get("command.leaderboard.voice") + "\n");
            }
            // Level
            default -> {
                leaderboardMembers = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.LEVEL);
                leaderboard.setDescription(lang(guild).get("command.leaderboard.level") + "\n");
            }
        }

        for (int i = 0; i < 10; i++) {
            if (i == leaderboardMembers.size()) break; // There are no more members
            final LeaderboardMember member = leaderboardMembers.get(i); // Get current member

            final String value; // Store value to display
            switch (type) {
                case BALANCE -> value = Format.number(member.getBalance()); // Balance
                case VOICE -> value = Format.toTime(member.getVoiceCallTime()); // Voice call time

                default -> value = String.valueOf(member.getLevel()); // Level
            }

            final int rank = i + 1; // Get users rank
            // Add member to leaderboard
            leaderboard.appendDescription(String.format("%d \uD83C\uDF97 `%s` **%s**%n",
                    rank, value, member.getName()));
        }

        return leaderboard;
    }

}
