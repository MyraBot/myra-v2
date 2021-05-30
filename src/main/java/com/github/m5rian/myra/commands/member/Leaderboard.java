package com.github.m5rian.myra.commands.member;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.LeaderboardType;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.LeaderboardMember;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Format;
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
            getLeaderboard(message, type.LEVEL); // Create leaderboard embed
            // Add reactions
            message.addReaction("\uD83C\uDFC6").queue(); // Add level emoji
            message.addReaction(CustomEmoji.COIN.getAsEmote()).queue(); // Add balance emote
            message.addReaction("\uD83D\uDCDE").queue(); // Voice call emoji

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> !e.getUser().isBot()
                            && e.getUserIdLong() == ctx.getMember().getIdLong()
                            && e.getMessageIdLong() == message.getIdLong()
                            && Arrays.asList(emojis).stream().anyMatch(emoji -> emoji.equalsIgnoreCase(e.getReactionEmote().toString())))
                    .setAction(e -> {
                        message.editMessage(loading.getEmbed().build()).queue(); // Edit to loading message
                        final String reaction = e.getReactionEmote().toString().toUpperCase(); // Get reacted reaction emote

                        // Balance leaderboard
                        if (e.getReactionEmote().isEmote()) getLeaderboard(message, type.BALANCE);
                            // Level leaderboard
                        else if (reaction.equals(emojis[0])) getLeaderboard(message, type.LEVEL);
                            // Voice leaderboard
                        else if (reaction.equals(emojis[2])) getLeaderboard(message, type.VOICE);

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
        List<LeaderboardMember> leaderboardRaw = null;
        switch (type) {
            case LEVEL -> leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.LEVEL);
            case BALANCE -> leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.BALANCE);
            case VOICE -> leaderboardRaw = new MongoGuild(guild).getMembers().getLeaderboard(LeaderboardType.VOICE);
        }

        final List<String> ids = new ArrayList<>(); // Create a list to store top 10 member ids
        for (int i = 0; i < 10; i++) {
            if (i == leaderboardRaw.size()) break;
            final LeaderboardMember member = leaderboardRaw.get(i); // Get current document
            ids.add(member.getId());
        }
        String[] topIds = ids.toArray(new String[0]); // Convert List to Array

        List<LeaderboardMember> finalLeaderboardRaw = leaderboardRaw;
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

                        final LeaderboardMember memberDocument = finalLeaderboardRaw.stream() // Find member document to get the balance
                                .filter(doc -> doc.getId().equals(id))
                                .findFirst()
                                .get();

                        // Store value to display
                        String value;
                        if (type == Leaderboard.type.LEVEL)
                            value = String.valueOf(memberDocument.getLevel()); // Get level
                        else if (type == Leaderboard.type.BALANCE)
                            value = Format.number(memberDocument.getBalance()); // Format balance
                        else
                            value = Format.toTime(memberDocument.getVoiceCallTime()); // Format time

                        leaderboard.append(String.format("%d \uD83C\uDF97 `%s` **%s**%n", place.get() + 1, value, member.getEffectiveName())); // Add member to leaderboard
                        place.getAndAdd(1); // Increase place
                    }

                    // Create embed
                    String header;
                    if (type == Leaderboard.type.LEVEL) // Show level leaderboard
                        header = Lang.lang(guild).get("command.leaderboard.level").replace("{$guild.name}", guild.getName());
                    else if (type == Leaderboard.type.BALANCE) // Show balance leaderboard
                        header = Lang.lang(guild).get("command.leaderboard.balance").replace("{$guild.name}", guild.getName());
                    else // Show voice call time leaderboard
                        header = Lang.lang(guild).get("command.leaderboard.voice").replace("{$guild.name}", guild.getName());

                    final EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor(guild.getName() + "'s leaderboard", null, guild.getIconUrl())
                            .setColor(Utilities.blue)
                            .setDescription(header)
                            .appendDescription(leaderboard);
                    // Send message
                    message.editMessage(embed.build()).queue(); // Edit message to show the balance leaderboard
                })
                .onError(Throwable::printStackTrace);
    }
}
