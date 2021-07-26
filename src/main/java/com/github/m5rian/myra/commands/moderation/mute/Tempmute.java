package com.github.m5rian.myra.commands.moderation.mute;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.defaultLang;
import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Tempmute implements CommandHandler {
    @CommandEvent(
            name = "tempmute",
            args = {"<member>", "<duration>", "<timeunit>", "(reason)"},
            emoji = "\uD83D\uDD07",
            description = "description.mod.tempmute",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length < 3) {
            usage(ctx).send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], ctx.getCommand().name(), ctx.getCommand().emoji()); // Get member
        if (member == null) return;

        String muteRoleId = MongoGuild.get(ctx.getGuild()).getString("muteRole"); //Get mute role id
        if (muteRoleId.equals("not set")) { // No mute role set
            error(ctx).setDescription(lang(ctx).get("error.mute.role.none")).send();
            return;
        }

        final String reason = ctx.getArguments().length == 3 ? "none" : ctx.getArgumentsRaw().split("\\s+", 4)[3]; // Get reason
        final Utilities.Duration duration = Utilities.getDuration(ctx, ctx.getArguments()[1], ctx.getArguments()[2]); // Get duration
        if (duration == null) return;

        // Prepare message
        final Success success = new Success(ctx.getEvent())
                .setCommand("tempmute")
                .setEmoji("\u23F1")
                .setFooter(lang(ctx).get("command.mod.info.requestBy").replace("{$member}", ctx.getAuthor().getAsTag()), ctx.getAuthor().getEffectiveAvatarUrl())
                .addField("\uD83D\uDCC4 │ " + lang(ctx).get("word.reason"), reason) // Add reason
                .addTimestamp();

        // Guild message
        success.setMessage(lang(ctx).get("command.mod.tempmute.info.guild")
                .replace("{$member}", member.getAsMention())
                .replace("{$duration}", String.valueOf(duration.getDuration()))
                .replace("{$timeunit}", duration.getTimeUnitAsName(ctx.getGuild())))
                .send();
        // Direct message
        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.tempmute.info.dm")
                    .replace("{$guild}", ctx.getGuild().getName())
                    .replace("{$duration}", String.valueOf(duration.getDuration()))
                    .replace("{$timeunit}", duration.getTimeUnitAsName(ctx.getGuild())))
                    .setChannel(channel)
                    .send();
        });

        ctx.getGuild().addRoleToMember(member, ctx.getGuild().getRoleById(muteRoleId)).queue(); // Mute
        final Document document = createUnmute(member.getId(), ctx.getGuild().getId(), duration.getMillis(), ctx.getAuthor().getId()); // Create unmute Document


        // Delay
        Utilities.TIMER.schedule(() -> {
            // Member left the server
            if (ctx.getGuild().getMemberById(document.getString("userId")) == null) {
                MongoDb.getInstance().getCollection("unmutes").deleteOne(document); // Delete document
            }
            ctx.getGuild().removeRoleFromMember(document.getString("userId"), ctx.getGuild().getRoleById(muteRoleId)).queue(); // Remove role
            unmuteMessage(member.getUser(), ctx.getGuild(), ctx.getAuthor()); // Send unmute message
            MongoDb.getInstance().getCollection("unmutes").deleteOne(document); // Delete unmute document
        }, duration.getMillis(), TimeUnit.MILLISECONDS);
    }


    // Create unmute document
    public Document createUnmute(String userId, String guildId, Long durationInMilliseconds, String moderatorId) {
        MongoCollection<Document> guilds = MongoDb.getInstance().getCollection("unmutes");
        // Create Document
        final Document docToInsert = new Document()
                .append("userId", userId)
                .append("guildId", guildId)
                .append("unmuteTime", System.currentTimeMillis() + durationInMilliseconds)
                .append("moderatorId", moderatorId);
        guilds.insertOne(docToInsert);
        return docToInsert;
    }

    // Unmute message
    private void unmuteMessage(User user, Guild guild, User author) {
        final MongoGuild db = MongoGuild.get(guild); // Get database

        // Prepare unmute message
        final Success success = new Success(null)
                .setCommand("tempmute")
                .setEmoji("\u23F1")
                .setFooter(defaultLang().get("command.mod.tempmute.info.requesterInfo")
                        .replace("{$guild}", guild.getName()) // Guild name
                        .replace("{$member}", author.getAsTag())) // Tempmute requester
                .addTimestamp();

        // Direct message
        user.openPrivateChannel().queue(channel -> {
            success.setMessage(defaultLang().get("command.mod.unmute.info.dm")
                    .replace("{$guild}", guild.getName())) // Guild name
                    .setAvatar(guild.getIconUrl())
                    .setChannel(channel)
                    .send();
        });

        if (db.getString("logChannel").equals("not set")) return; // No log channel set
        final TextChannel logChannel = guild.getTextChannelById(db.getString("logChannel")); // Get log channel
        // Guild unmute message
        success.setMessage(lang(guild).get("command.mod.unmute.info.guild")
                .replace("{$member.mention}", user.getAsMention()) // Muted member
                .replace("{$member}", author.getAsTag())) // Tempmute requester
                .setAvatar(user.getEffectiveAvatarUrl())
                .setChannel(logChannel)
                .send();
    }

    public void onReady(ReadyEvent event) throws Exception {
        // Go through each document
        final MongoCursor<Document> unmutes = MongoDb.getInstance().getCollection("unmutes").find().iterator();
        while (unmutes.hasNext()) {
            final Document document = unmutes.next(); // Get next document

            final Long unmuteTime = document.getLong("unmuteTime"); // Get unmute time
            final Guild guild = event.getJDA().getGuildById(document.getString("guildId")); // Get guild
            final User user = event.getJDA().retrieveUserById(document.getString("userId")).complete(); // Get user

            // Unmute time is already reached
            if (unmuteTime < System.currentTimeMillis()) {
                // Member left server
                if (user == null) {
                    MongoDb.getInstance().getCollection("unmutes").deleteOne(document); //delete document
                }

                // No mute role set
                if (MongoGuild.get(guild).getString("muteRole").equals("not set")) {
                    // No mute role set
                    new Error(null)
                            .setCommand("tempban")
                            .setEmoji("\u23F1")
                            .setAvatar(guild.getIconUrl())
                            .setMessage("You didn't specify a mute rol")
                            .setChannel(guild.getDefaultChannel())
                            .send();
                    continue;
                }
                guild.removeRoleFromMember(document.getString("userId"), guild.getRoleById(MongoGuild.get(guild).getString("muteRole"))).queue(); // Remove role
                unmuteMessage(user, guild, event.getJDA().getUserById(document.getString("moderatorId"))); // Send unmute message
                MongoDb.getInstance().getCollection("unmutes").deleteOne(document); // Delete document
            }
            // User should already be unmuted
            else {
                // Delay
                Utilities.TIMER.schedule(() -> {
                    //if member left the server
                    if (guild.getMemberById(document.getString("userId")) == null) {
                        MongoDb.getInstance().getCollection("unmutes").deleteOne(document); //delete document
                    }
                    //unmute
                    guild.removeRoleFromMember(document.getString("userId"), guild.getRoleById(MongoGuild.get(guild).getString("muteRole"))).queue();
                    unmuteMessage(user, guild, event.getJDA().getUserById(document.getString("moderatorId"))); /// Send unmute message
                    MongoDb.getInstance().getCollection("unmutes").deleteOne(document); // Delete document
                }, document.getLong("unmuteTime") - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
        }
    }
}