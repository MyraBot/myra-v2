package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessage;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.database.guild.member.GuildMembers;
import com.github.m5rian.myra.listeners.leveling.Leveling;
import com.github.m5rian.myra.listeners.leveling.VoiceCall;
import com.github.m5rian.myra.management.Listeners;
import com.github.m5rian.myra.utilities.APIs.mee6.Mee6;
import com.github.m5rian.myra.utilities.APIs.mee6.Mee6User;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.UserBadge;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.utilities.permissions.Marian;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.m5rian.myra.utilities.language.Lang.lang;
import static com.mongodb.client.model.Filters.*;

public class Config implements CommandHandler {

    @CommandEvent(
            name = "config",
            emoji = "\u2699",
            description = "description.config",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void onConfigHelp(CommandContext ctx) {
        // No other command meant
        if (ctx.getArguments().length != 1) {
            // Command usages
            new CommandUsage(ctx.getEvent())
                    .setCommand("config")
                    .addUsages(
                            new Usage().setUsage("config clean")
                                    .setEmoji("\uD83E\uDDFD")
                                    .setDescription(lang(ctx).get("description.config.clean")),
                            new Usage().setUsage("config update")
                                    .setEmoji("\u2B50")
                                    .setDescription(lang(ctx).get("description.config.update")))
                    .send();
        }
    }

    @CommandEvent(
            name = "config clean",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void onMemberPurge(CommandContext ctx) {
        // Confirmation
        final Success confirmation = new Success(ctx.getEvent())
                .setCommand("config clean")
                .setEmoji("\uD83E\uDDFD")
                .setMessage(lang(ctx).get("description.config.clean"))
                .setFooter(lang(ctx).get("info.securityWarning"));
        ctx.getChannel().sendMessage(confirmation.getEmbed().build()).queue(message -> {
            message.addReaction(CustomEmoji.GREEN_TICK.getEmote()).queue(); // Add reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> e.getUserIdLong() == ctx.getAuthor().getIdLong() &&
                            e.getReactionEmote().toString().equals(CustomEmoji.GREEN_TICK.getCodepoints()))
                    .setAction(e -> {
                        final int memberCount = e.getGuild().getMemberCount(); // Get guild member count
                        Listeners.unavailableGuilds.add(e.getGuild().getId()); // Add Guild to unavailable guilds

                        final AtomicLong total = new AtomicLong(0); // Amount of passed members
                        final String progressTemplate = lang(ctx).get("command.config.clean.info.progress");
                        final EmbedBuilder progress = new Success(ctx.getEvent())
                                .setCommand("config clean")
                                .setEmoji("\uD83E\uDDFD")
                                .setMessage(progressTemplate.replace("{$percent}", "0"))
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
                                    .setMessage(lang(ctx).get("command.config.clean.info.success")
                                            .replace("{$clearedMembers}", String.valueOf(cleared)) // Amount of removed members
                                            .replace("{$totalMembers}", String.valueOf(total.get()))) // Total membesr
                                    .addTimestamp()
                                    .send();
                            Listeners.unavailableGuilds.remove(e.getGuild().getId()); // Make guild available again
                        });
                        databaseAction.setName("Clean members " + ctx.getGuild().getId()); // Set thread name

                        e.getChannel().sendMessage(progress.build()).queue(progressMessage -> new Thread(() -> {
                            // While database is working
                            while (databaseAction.isAlive()) {
                                try {
                                    final long percentage = total.get() * 100 / memberCount; // calculate percentage
                                    progress.setDescription(progressTemplate.replace("{$percent}", String.valueOf(percentage))); // Update percentage
                                    progressMessage.editMessage(progress.build()).queue(); // Edit message

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
            name = "config update",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void onMemberUpdate(CommandContext ctx) {
        // Confirmation
        final EmbedBuilder confirmation = new Success(ctx.getEvent())
                .setCommand("config update")
                .setEmoji("\u2B50")
                .setMessage(lang(ctx).get("description.config.update"))
                .setFooter(lang(ctx).get("info.securityWarning"))
                .getEmbed();
        ctx.getChannel().sendMessage(confirmation.build()).queue(message -> {
            message.addReaction(CustomEmoji.GREEN_TICK.getEmote()).queue(); // Add reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> e.getUserIdLong() == ctx.getAuthor().getIdLong() &&
                            e.getReactionEmote().toString().equals(CustomEmoji.GREEN_TICK.getCodepoints()))
                    .setAction(e -> {
                        Listeners.unavailableGuilds.add(e.getGuild().getId()); // Add Guild to unavailable guilds

                        final long total = MongoDb.getInstance().getCollection("users").countDocuments(exists(e.getGuild().getId())); // Get amount of documents to parse
                        final AtomicLong updated = new AtomicLong(0); // Amount of already passed documents
                        final String progressTemplate = lang(ctx).get("command.config.update.progress");
                        final Success progress = new Success(ctx.getEvent())
                                .setCommand("config update")
                                .setEmoji("\u2B50")
                                .setMessage(progressTemplate.replace("{$percent}", "0"))
                                .addTimestamp();

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
                                    .setMessage(lang(ctx).get("command.config.update.success")
                                            .replace("{$updatedMembers}", String.valueOf(updated)))
                                    .addTimestamp()
                                    .send();
                            Listeners.unavailableGuilds.remove(e.getGuild().getId()); // Make guild available again
                        });
                        databaseAction.setName("Update members " + ctx.getGuild().getId()); // Set thread name

                        // Progress animation thread
                        e.getChannel().sendMessage(progress.getEmbed().build()).queue(progressMessage -> new Thread(() -> {
                            // While database is working
                            while (databaseAction.isAlive()) {
                                try {
                                    final long percentage = updated.get() * 100 / total; // Calculate percentage
                                    progress.setMessage(progressTemplate.replace("{$percent}", String.valueOf(percentage))); // Update percentage
                                    progressMessage.editMessage(progress.getEmbed().build()).queue(); // Edit message

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
            name = "config mee6",
            emoji = "\uD83C\uDFC6",
            description = "description.config.mee6",
            requires = Marian.class,
            channel = Channel.GUILD
    )
    public void onMee6LevelingTransfer(CommandContext ctx) {
        // Confirmation
        final EmbedBuilder confirmation = info(ctx)
                .setDescription(lang(ctx).get("description.config.mee6"))
                .setFooter(lang(ctx).get("info.securityWarning"))
                .getEmbed();
        ctx.getChannel().sendMessage(confirmation.build()).queue(message -> {
            message.addReaction(CustomEmoji.GREEN_TICK.getEmote()).queue(); // Add reaction

            ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                    .setCondition(e -> e.getUserIdLong() == ctx.getAuthor().getIdLong() &&
                            e.getReactionEmote().toString().equals("RE:" + CustomEmoji.GREEN_TICK.getCodepoints()))
                    .setAction(e -> {
                        Listeners.unavailableGuilds.add(e.getGuild().getId()); // Add Guild to unavailable guilds

                        final GuildMembers guildMembers = new MongoGuild(ctx.getGuild()).getMembers();
                        final List<Mee6User> mee6Members = new Mee6(ctx.getGuild()).getUsers();

                        final AtomicLong updated = new AtomicLong(0); // Amount of already passed documents

                        final String progressTemplate = lang(ctx).get("command.config.update.progress");
                        final CommandMessage progress = info(ctx)
                                .setDescription(progressTemplate.replace("{$percent}", "0"))
                                .addTimestamp();

                        // Database clearing thread
                        final Thread transferAction = new Thread(() -> {

                            mee6Members.forEach(member -> {
                                final GuildMember guildMember = guildMembers.getMember(member.getUserId()); // Get guild from member
                                if (!guildMember.isBot()) {
                                    final Integer mee6Xp = member.getXp();
                                    final int voiceXp = VoiceCall.getXp(guildMember.getVoiceTime());

                                    final long xp = mee6Xp + voiceXp;
                                    guildMember.setXp(xp); // Update xp
                                    guildMember.setLevel(Leveling.getLevelFromXp(xp));
                                    guildMember.setMessageCount(member.getMessageCount());

                                    updated.getAndAdd(1);
                                }
                            });

                            // Send success message
                            info(ctx)
                                    .setDescription(lang(ctx).get("command.config.update.success")
                                            .replace("{$updatedMembers}", String.valueOf(updated)))
                                    .addTimestamp()
                                    .send();
                            Listeners.unavailableGuilds.remove(e.getGuild().getId()); // Make guild available again
                        }, "Member updater " + ctx.getGuild().getId());


                        final Thread thread = new Thread(() -> {
                            ctx.getChannel().sendMessage(progress.getEmbed().build()).queue(progressMessage -> {
                                final Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        // Transferring action is still going
                                        if (transferAction.isAlive()) {
                                            final long percentage = updated.get() * 100 / mee6Members.size(); // Calculate percentage
                                            final String description = progressTemplate.replace("{$percent}", String.valueOf(percentage));
                                            progressMessage.editMessage(progress.setDescription(description).getEmbed().build()).queue(); // Edit message
                                        }
                                        // Transferring is done
                                        else {
                                            timer.cancel();
                                            timer.purge();
                                        }
                                    }
                                }, 0, 5000);
                            });
                        }, "Message updater " + ctx.getGuild().getId());

                        transferAction.start();
                        thread.start();
                    })
                    .setTimeout(30, TimeUnit.SECONDS)
                    .setTimeoutAction(() -> message.clearReactions().queue())
                    .load();
        });
    }
}
