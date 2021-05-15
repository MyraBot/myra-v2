package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.management.Listeners;
import com.myra.dev.marian.utilities.CustomEmoji;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.UserBadge;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class Config implements CommandHandler {

    @CommandEvent(
            name = "config clean",
            requires = Administrator.class
    )
    public void onMemberPurge(CommandContext ctx) {
        // Confirmation
        final EmbedBuilder confirmation = new Success(ctx.getEvent())
                .setCommand("config clean")
                .setEmoji("\uD83E\uDDFD")
                .setMessage("Remove left members from the database")
                .setFooter("Do not touch this command, if you don't know what it does!")
                .getEmbed();
        ctx.getChannel().sendMessage(confirmation.build()).queue(message -> {
            message.addReaction(CustomEmoji.GREEN_TICK.getAsEmoji()).queue(); // Add reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> e.getUserIdLong() == ctx.getAuthor().getIdLong() &&
                            e.getReactionEmote().toString().equals(CustomEmoji.GREEN_TICK.getAsReactionEmote()))
                    .setAction(e -> {
                        final int memberCount = e.getGuild().getMemberCount(); // Get guild member count
                        Listeners.unavailableGuilds.add(e.getGuild().getId()); // Add Guild to unavailable guilds

                        final AtomicLong total = new AtomicLong(0); // Amount of passed members
                        EmbedBuilder progress = new Success(ctx.getEvent())
                                .setCommand("config clean")
                                .setEmoji("\uD83E\uDDFD")
                                .setMessage("Now all members which left get removed from the database. This can take some time. Meanwhile the bot won't response on any actions of this server." +
                                        "\nPlease be patient." +
                                        "\n`0%`")
                                .addTimestamp()
                                .getEmbed();

                        // Database clearing thread
                        final Thread databaseAction = new Thread(() -> {
                            final MongoCursor<Document> users = MongoDb.getInstance().getCollection("users").find(exists(e.getGuild().getId())).iterator(); // Get all users who have a guild document
                            long cleared = 0;

                            while (users.hasNext()) {
                                final Document user = users.next(); // Get next user document
                                total.getAndAdd(1); // Add 1 to total users

                                final String userId = user.getString("userId"); // Get user id
                                try {
                                    e.getGuild().retrieveMemberById(userId).complete(); // Try to retrieve member
                                }
                                // Error occurred
                                catch (Exception exception) {
                                    exception.printStackTrace();

                                    // Member isn't in the server anymore
                                    if (exception.getMessage().equals("10007: Unknown Member")) {
                                        cleared += 1; // Add 1 to cleared users

                                        user.remove(e.getGuild().getId()); // Remove guild document
                                        MongoDb.getInstance().getCollection("users").findOneAndReplace(eq("userId", userId), user); // Update database
                                    }
                                }
                            }

                            new Success(ctx.getEvent())
                                    .setCommand("config clean")
                                    .setEmoji("\uD83E\uDDFD")
                                    .setMessage(String.format("Successfully removed `%s/%s` members!", cleared, total.get()))
                                    .addTimestamp()
                                    .send();
                            Listeners.unavailableGuilds.remove(e.getGuild().getId()); // Make guild available again
                        });
                        databaseAction.setName("Clean members " + ctx.getGuild().getId()); // Set thread name

                        e.getChannel().sendMessage(progress.build()).queue(progressMessage -> new Thread(() -> {
                            // While database is working
                            while (databaseAction.isAlive()) {
                                try {
                                    final long percentage = total.get() * 100 / memberCount;
                                    progress.setDescription("Now all members which left get removed from the database. This can take some time. Meanwhile the bot won't response on any actions of this server." +
                                            "\nPlease be patient." +
                                            "\n`" + percentage + "%`");
                                    progressMessage.editMessage(progress.build()).queue();

                                    Thread.sleep(5000);
                                } catch (InterruptedException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }).start());

                        databaseAction.start(); // Start member clearing
                    })
                    .setTimeout(30, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }

    @CommandEvent(
            name = "config update"
    )
    public void onMemberUpdate(CommandContext ctx) {
        // Confirmation
        final EmbedBuilder confirmation = new Success(ctx.getEvent())
                .setCommand("config clean")
                .setEmoji("\u2B50")
                .setMessage("Update users data")
                .setFooter("Do not touch this command, if you don't know what it does!")
                .getEmbed();
        ctx.getChannel().sendMessage(confirmation.build()).queue(message -> {
            message.addReaction(CustomEmoji.GREEN_TICK.getAsEmoji()).queue(); // Add reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> e.getUserIdLong() == ctx.getAuthor().getIdLong() &&
                            e.getReactionEmote().toString().equals(CustomEmoji.GREEN_TICK.getAsReactionEmote()))
                    .setAction(e -> {
                        Listeners.unavailableGuilds.add(e.getGuild().getId()); // Add Guild to unavailable guilds
                        final long total = MongoDb.getInstance().getCollection("users").countDocuments(exists(e.getGuild().getId())); // Get amount of documents to parse
                        final AtomicLong updated = new AtomicLong(0); // Amount of already passed documents

                        EmbedBuilder progress = new Success(ctx.getEvent())
                                .setCommand("config clean")
                                .setEmoji("\u2B50")
                                .setMessage("Now the data of all members get updated. This can take some time. Meanwhile the bot won't response on any actions of this server." +
                                        "\nPlease be patient." +
                                        "\n`0%`")
                                .addTimestamp()
                                .getEmbed();

                        // Database clearing thread
                        final Thread databaseAction = new Thread(() -> {
                            final MongoCursor<Document> users = MongoDb.getInstance().getCollection("users").find(exists(e.getGuild().getId())).iterator(); // Get all users who have a guild document

                            while (users.hasNext()) {
                                final Document document = users.next(); // Get next user document
                                updated.getAndAdd(1); // Add 1 to total users

                                final String userId = document.getString("userId"); // Get user id
                                try {
                                    final User user = e.getJDA().retrieveUserById(userId).complete(); // Try to retrieve member
                                    final List<UserBadge> badges = UserBadge.getUserBadges(user); // Get badges
                                    final List<String> badgesAsString = new ArrayList<>();
                                    badges.forEach(badge -> badgesAsString.add(badge.getName()));

                                    document.replace("name", user.getName());
                                    document.replace("discriminator", user.getDiscriminator());
                                    document.replace("avatar", user.getEffectiveAvatarUrl());
                                    document.replace("badges", badgesAsString);
                                    MongoDb.getInstance().getCollection("users").findOneAndReplace(eq("userId", userId), document); // Update database
                                }
                                // Error occurred
                                catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                            // Send success message
                            new Success(ctx.getEvent())
                                    .setCommand("config clean")
                                    .setEmoji("\u2B50")
                                    .setMessage(String.format("Successfully updated `%s` members!", updated))
                                    .addTimestamp()
                                    .send();
                            Listeners.unavailableGuilds.remove(e.getGuild().getId()); // Make guild available again
                        });
                        databaseAction.setName("Update members " + ctx.getGuild().getId()); // Set thread name

                        // Progress animation thread
                        e.getChannel().sendMessage(progress.build()).queue(progressMessage -> new Thread(() -> {
                            // While database is working
                            while (databaseAction.isAlive()) {
                                try {
                                    final long percentage = updated.get() * 100 / total;
                                    progress.setDescription("Now all members which left get removed from the database. This can take some time. Meanwhile the bot won't response on any actions of this server." +
                                            "\nPlease be patient." +
                                            "\n`" + percentage + "%`");
                                    progressMessage.editMessage(progress.build()).queue();

                                    Thread.sleep(5000);
                                } catch (InterruptedException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }).start());

                        databaseAction.start(); // Start member clearing
                    })
                    .setTimeout(30, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }
}
