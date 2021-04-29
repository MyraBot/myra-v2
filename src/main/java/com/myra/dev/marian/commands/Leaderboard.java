package com.myra.dev.marian.commands;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.guild.LeaderboardType;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.LeaderboardMember;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Leaderboard implements CommandHandler {
    // I needed to use this weird format on the normal emoji, because otherwise I wouldn't be able to use the Arrays.stream thing below in the conditions of the event waiter
    final String[] emojis = {
            "RE:U+1F3C6", // Leveling (🏆)
            "R" + Utilities.getUtils().getEmote("coin").toString().toUpperCase(), // Reaction emotes starts with 'RE' and emote with 'E' so I remove the 'R'
            "RE:U+1F4DE" // Voice call
    };

    @CommandEvent(
            name = "leaderboard",
            aliases = {"lb", "top"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Create loading embed
        EmbedBuilder loading = new EmbedBuilder()
                .setAuthor(ctx.getGuild().getName() + "'s leaderboard", null, ctx.getGuild().getIconUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("*loading*");
        // Send message
        ctx.getChannel().sendMessage(loading.build()).queue(message -> {
            // Create leaderboard embed
            getLeaderboard(message, type.LEVEL);
            // Add reactions
            message.addReaction("\uD83C\uDFC6").queue(); // Add level emoji
            message.addReaction(Utilities.getUtils().getEmote("coin")).queue(); // Add balance emote
            message.addReaction("\uD83D\uDCDE").queue(); // Voice call emoji

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getMember().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.asList(emojis).contains(e.getReactionEmote().toString().toUpperCase()))
                    .setAction(e -> {
                        message.editMessage(loading.build()).queue(); // Edit to loading message

                        // Reaction is emote
                        if (e.getReactionEmote().isEmote()) { // Reaction is emote
                            getLeaderboard(message, type.BALANCE); // Send balance leaderboard
                        }
                        // Reaction is emoji
                        else {
                            // Leveling leaderboard
                            if (e.getReactionEmote().toString().toUpperCase().equals(emojis[0])) {
                                getLeaderboard(message, type.LEVEL); // Send level leaderboard
                            }
                            // Voice call leaderboard
                            else if (e.getReactionEmote().toString().toUpperCase().equals(emojis[2])) {
                                getLeaderboard(message, type.VOICE); // Send voice call leaderboard
                            }
                        }

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

    private void getLeaderboard(Message message, type type) {
        final Guild guild = message.getGuild(); // Get guild

        // Get leaderboard with all members
        List<LeaderboardMember> leaderboardRaw;
        if (type == Leaderboard.type.LEVEL) // Get level leaderboard
            leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.LEVEL);
        else if (type == Leaderboard.type.BALANCE) // Get balance leaderboard
            leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.BALANCE);
        else // Get voice leaderboard
            leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.VOICE);

        final List<String> ids = new ArrayList<>(); // Create a list to store top 10 member ids
        for (int i = 0; i < 10; i++) {
            if (i == leaderboardRaw.size()) break;
            final LeaderboardMember member = leaderboardRaw.get(i); // Get current document
            ids.add(member.getId());
        }
        String[] topIds = ids.toArray(new String[0]); // Convert List to Array

        guild.retrieveMembersByIds(topIds) // Retrieve top 10 members
                .onSuccess(members -> { // Members were successfully retrieved
                    StringBuilder leaderboard = new StringBuilder(); // Create leaderboard message
                    AtomicInteger place = new AtomicInteger();

                    for (final String id : ids) { // Add all members to leaderboard
                        Optional<Member> memberResult = members.stream() // Find member object
                                .filter(m -> m.getId().equals(id)) // Member needs same id as id from the leaderboard of the database
                                .findFirst();
                        if (memberResult.isEmpty()) continue; // No member found
                        final Member member = memberResult.get();

                        final LeaderboardMember memberDocument = leaderboardRaw.stream() // Find member document to get the balance
                                .filter(doc -> doc.getId().equals(id))
                                .findFirst()
                                .get();

                        // Store value to display
                        String value;
                        if (type == Leaderboard.type.LEVEL)
                            value = String.valueOf(memberDocument.getLevel()); // Get level
                        else if (type == Leaderboard.type.BALANCE)
                            value = Utilities.getUtils().formatNumber(memberDocument.getBalance()); // Format balance
                        else
                            value = Format.toTime(memberDocument.getVoiceCallTime()); // Format time

                        leaderboard.append(String.format("%d \uD83C\uDF97 `%s` **%s**%n", place.get() + 1, value, member.getEffectiveName())); // Add member to leaderboard
                        place.getAndAdd(1); // Increase place
                    }

                    // Create embed
                    String header;
                    if (type == Leaderboard.type.LEVEL) // Show level leaderboard
                        header = String.format("%s's level leaderboard%n", guild.getName());
                    else // Show balance leaderboard
                        header = String.format("%s's balance leaderboard%n", guild.getName());

                    EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor(guild.getName() + "'s leaderboard", null, guild.getIconUrl())
                            .setColor(Utilities.getUtils().blue)
                            .setDescription(header)
                            .appendDescription(leaderboard);
                    // Send message
                    message.editMessage(embed.build()).queue(); // Edit message to show the balance leaderboard
                })
                .onError(Throwable::printStackTrace);
    }
}
