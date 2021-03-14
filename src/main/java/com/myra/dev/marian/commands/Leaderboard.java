package com.myra.dev.marian.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.LeaderboardType;
import com.myra.dev.marian.database.documents.MemberDocument;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.Utilities;
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

@CommandSubscribe(
        name = "leaderboard",
        aliases = {"top"}
)
public class Leaderboard implements Command {

    @Override
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

            waiter(Myra.WAITER, message, ctx.getMember());
        });
    }

    private void waiter(EventWaiter waiter, Message message, Member member) {
        // I needed to use this weird format on the normal emoji, because otherwise I wouldn't be able to use the Arrays.stream thing below in the conditions of the event waiter
        final String[] emojis = {
                "R" + Utilities.getUtils().getEmote("coin").toString(), // Reaction emotes starts with 'RE' and emote with 'E' so I remove the 'R'
                "RE:U+1f3c6" // 🏆
        };


        waiter.waitForEvent(
                GuildMessageReactionAddEvent.class,
                e -> !e.getUser().isBot()
                        && e.getUserIdLong() == member.getIdLong()
                        && e.getMessageIdLong() == message.getIdLong()
                        && Arrays.stream(emojis).anyMatch(e.getReactionEmote().toString()::equals),
                e -> { // Code on event


                    // Create loading embed
                    EmbedBuilder loading = new EmbedBuilder()
                            .setAuthor(e.getGuild().getName() + "'s leaderboard", null, e.getGuild().getIconUrl())
                            .setColor(Utilities.getUtils().blue)
                            .setDescription("*loading*");
                    // Edit message
                    message.editMessage(loading.build()).queue(); // Edit message to show the balance leaderboard


                    // Reaction is emote
                    if (e.getReactionEmote().isEmote()) { // Reaction is emote
                        getLeaderboard(message, type.BALANCE); // Send balance leaderboard
                    }
                    // Reaction is emoji
                    else {
                        if (e.getReactionEmote().toString().equals(emojis[1])) {
                            getLeaderboard(message, type.LEVEL); // Send level leaderboard
                        }
                    }

                    e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                    waiter(waiter, message, member); // Loop this method until it runs the timeout
                },
                30L, TimeUnit.SECONDS, // Timeout
                () -> message.clearReactions().queue() // Run on timeout
        );
    }

    private enum type {
        LEVEL,
        BALANCE
    }

    private void getLeaderboard(Message message, type type) {
        final Guild guild = message.getGuild(); // Get guild

        // Get leaderboard with all members
        List<MemberDocument> leaderboardRaw;
        if (type == Leaderboard.type.LEVEL) // Get level leaderboard
            leaderboardRaw = new Database(guild).getMembers().getLeaderboard(LeaderboardType.LEVEL);
        else // Get balance leaderboard
            leaderboardRaw = new Database(guild).getMembers().getLeaderboard(LeaderboardType.BALANCE);

        final List<String> ids = new ArrayList<>(); // Create a list to store top 10 member ids
        for (int i = 0; i < 10; i++) {
            if (i == leaderboardRaw.size()) break;
            final MemberDocument member = leaderboardRaw.get(i); // Get current document
            ids.add(member.getId());
        }
        String[] topIds = ids.toArray(new String[0]); // Convert List to Array

        guild.retrieveMembersByIds(topIds) // Retrieve top 10 members
                .onSuccess(members -> { // Member was successfully retrieved
                    StringBuilder leaderboard = new StringBuilder(); // Create leaderboard message
                    AtomicInteger place = new AtomicInteger();

                    for (final String id : ids) { // Add all members to leaderboard
                        Optional<Member> memberResult = members.stream() // Find member object
                                .filter(user -> user.getId().equals(id)) // Member needs same id as id from the leaderboard of the database
                                .findFirst();
                        if (memberResult.isEmpty()) continue; // No member found
                        final Member member = memberResult.get();

                        final MemberDocument memberDocument = leaderboardRaw.stream() // Find member document to get the balance
                                .filter(doc -> doc.getId().equals(id))
                                .findFirst()
                                .get();

                        // Store value to display
                        String value;
                        if (type == Leaderboard.type.LEVEL)
                            value = String.valueOf(memberDocument.getLevel()); // Get level
                        else
                            value = Utilities.getUtils().formatNumber(memberDocument.getBalance()); // Format balance

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
                .onError(error -> {
                    error.printStackTrace();
                });
        /*        String top10 =
                        "1 \uD83D\uDC51 `" + leaderboardList.get(0).getLevel() + "` **" + leaderboardList.get(0).getName() + "**\n" +
                                "2 \uD83D\uDD31 `" + leaderboardList.get(1).getLevel() + "` **" + leaderboardList.get(1).getName() + "**\n" +
                                "3 \uD83C\uDFC6 `" + leaderboardList.get(2).getLevel() + "` **" + leaderboardList.get(2).getName() + "**\n" +
                                "4 \uD83C\uDF96 `" + leaderboardList.get(3).getLevel() + "` **" + leaderboardList.get(3).getName() + "**\n" +
                                "5 \uD83C\uDFC5 `" + leaderboardList.get(4).getLevel() + "` **" + leaderboardList.get(4).getName() + "**\n" +
                                "6 \u26A1 `" + leaderboardList.get(5).getLevel() + "` **" + leaderboardList.get(5).getName() + "**\n" +
                                "7 \uD83C\uDF97 `" + leaderboardList.get(6).getLevel() + "` **" + leaderboardList.get(6).getName() + "**\n" +
                                "8 \uD83C\uDF97 `" + leaderboardList.get(7).getLevel() + "` **" + leaderboardList.get(7).getName() + "**\n" +
                                "9 \uD83C\uDF97 `" + leaderboardList.get(8).getLevel() + "` **" + leaderboardList.get(8).getName() + "**\n" +
                                "10 \uD83C\uDF97 `" + leaderboardList.get(9).getLevel() + "` **" + leaderboardList.get(9).getName() + "**\n";*/
    }
}
