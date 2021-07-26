package com.github.m5rian.myra.commands.moderation.ban;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Tempban implements CommandHandler {
    @CommandEvent(
            name = "tempban",
            aliases = {"tempbean"},
            args = {"<member>", "<duration>", "<time unit>", "(reason)"},
            emoji = "\u23F1\uFE0F",
            description = "description.mod.tempban",
            requires = Moderator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("tempban")
                    .addUsages(new Usage()
                            .setUsage("tempban <user> <duration><time unit> (reason)")
                            .setEmoji("\u23F1\uFE0F")
                            .setDescription(lang(ctx).get("description.mod.tempban")))
                    .addInformation(lang(ctx).get("command.mod.tempban.info.timeUnits"))
                    .send();
            return;
        }

        // Get provided member
        final Member member = Utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "tempban", "\u23F1\uFE0F"); // Get member
        if (member == null) return;


        final String reason = ctx.getArguments().length == 1 ? "none" : ctx.getArgumentsRaw().split("\\s+", 4)[3]; // Get reason
        final String durationRaw = ctx.getArguments()[1]; // Get duration from the message
        // Duration doesn't math [NumbersLetters]
        if (!durationRaw.matches("[0-9]+[a-zA-z]+")) {
            new Error(ctx.getEvent())
                    .setCommand("tempban")
                    .setEmoji("\u23F1\uFE0F")
                    .setMessage(lang(ctx).get("error.invalidTime"))
                    .send();
            return;
        }

        final Utilities.Duration duration = Utilities.getDuration(ctx, ctx.getArguments()[1], ctx.getArguments()[2]); // Get duration
        if (duration == null) return;

        // Prepare message
        final Success success = new Success(ctx.getEvent())
                .setCommand("tempban")
                // Member who executed the ban
                .setFooter(lang(ctx).get("command.mod.info.requestBy")
                                .replace("{$member}", ctx.getAuthor().getAsTag()),
                        ctx.getAuthor().getEffectiveAvatarUrl())
                // Add reason
                .addField("\uD83D\uDCC4 │ " + lang(ctx).get("word.reason"), reason)
                .addTimestamp();

        // Send guild message
        success.setMessage(lang(ctx).get("command.mod.tempban.info.guild")
                .replace("{$member}", member.getAsMention()) // Member who got banned
                .replace("{$duration}", String.valueOf(duration.getDuration())) // Ban duration
                .replace("{$timeunit}", duration.getTimeUnitAsName(ctx.getGuild())))
                .send(); // Send in current channel
        // Send direct message
        member.getUser().openPrivateChannel().queue(channel -> {
            success.setMessage(lang(ctx).get("command.mod.tempban.info.dm")
                    .replace("{$guild}", ctx.getGuild().getName()) // Guild name
                    .replace("{$duration}", String.valueOf(duration.getDuration())) // Ban duration
                    .replace("{$timeunit}", duration.getTimeUnitAsName(ctx.getGuild())))
                    .setChannel(channel) // Set channel to direct message
                    .send();
        });

        member.ban(7, reason).queue(); // Ban member


        final Document document = createUnban(member.getId(), ctx.getGuild().getId(), duration.getMillis(), ctx.getAuthor().getId()); // Create unban document
        //delay
        Utilities.TIMER.schedule(() -> {
            ctx.getGuild().unban(member.getUser()).queue(); // Unban
            unbanMessage(member.getUser(), ctx.getGuild(), ctx.getAuthor()); // Send unban message
            MongoDb.getInstance().getCollection("unbans").deleteOne(document); // Delete Document
        }, duration.getMillis(), TimeUnit.MILLISECONDS);
    }

    public Document createUnban(String userId, String guildId, Long durationInMilliseconds, String moderatorId) {
        MongoCollection<Document> guilds = MongoDb.getInstance().getCollection("unbans");
        // Create document
        Document docToInsert = new Document()
                .append("userId", userId)
                .append("guildId", guildId)
                .append("unbanTime", System.currentTimeMillis() + durationInMilliseconds)
                .append("moderatorId", moderatorId);
        guilds.insertOne(docToInsert);

        return docToInsert;
    }

    public void loadUnbans(ReadyEvent event) throws Exception {
        //for each document
        for (Document doc : MongoDb.getInstance().getCollection("unbans").find()) {
            Long unbanTime = doc.getLong("unbanTime"); // Get unban time
            Guild guild = event.getJDA().getGuildById(doc.getString("guildId")); // Get guild

            // Retrieve bans
            guild.retrieveBanList().queue(bans -> {
                final String userId = doc.getString("userId"); // Get user id
                final User user = event.getJDA().getUserById(userId); // Get user
// Unban time is already reached
                if (unbanTime < System.currentTimeMillis()) {
                    // User is still banned
                    if (bans.stream().anyMatch(ban -> ban.getUser().equals(event.getJDA().getUserById(userId)))) {
                        guild.unban(user).queue(); // Unban
                        unbanMessage(user, guild, event.getJDA().getUserById(doc.getString("moderatorId"))); // Send unban message
                        MongoDb.getInstance().getCollection("unbans").deleteOne(doc); // Delete document
                    }
                }
// Unban time isn't reached yet
                else {
                    // Delay
                    Utilities.TIMER.schedule(() -> {
                        guild.unban(user).queue(); // Unban
                        unbanMessage(user, guild, event.getJDA().getUserById(doc.getString("moderatorId"))); // Send unban message
                        MongoDb.getInstance().getCollection("unbans").deleteOne(doc); // Delete document
                    }, doc.getLong("unbanTime") - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
            });
        }
    }

    private void unbanMessage(User user, Guild guild, User author) {
        MongoGuild db = MongoGuild.get(guild); // Get database

        // Prepare message
        final Success success = new Success(null)
                .setCommand("tempban")
                .setFooter(lang(guild).get("command.mod.tempban.info.requesterInfo").replace("{$member}",
                        author.getAsTag()), author.getEffectiveAvatarUrl())
                .addTimestamp();

        // Send direct message
        user.openPrivateChannel().queue(channel -> {
            success.setMessage(lang(guild).get("command.mod.unban.info.dm")
                    .replace("{$guild}", guild.getName()))
                    .setAvatar(guild.getIconUrl())
                    .setChannel(channel)
                    .send();
        });

        // No log channel set
        if (db.getString("logChannel").equals("not set")) {
            new Error(null)
                    .setCommand("tempban")
                    .setEmoji("\u23F1\uFE0F")
                    .setAvatar(user.getEffectiveAvatarUrl())
                    .setMessage("No log channel specified")
                    .send();
            return;
        }
        final TextChannel logChannel = guild.getTextChannelById(db.getString("logChannel")); // Get log channel
        // Send in log channel
        success.setMessage(lang(guild).get("command.mod.unban.info.guild")
                .replace("{$user}", user.getAsTag()))
                .setAvatar(user.getEffectiveAvatarUrl())
                .setChannel(logChannel)
                .send();
    }
}