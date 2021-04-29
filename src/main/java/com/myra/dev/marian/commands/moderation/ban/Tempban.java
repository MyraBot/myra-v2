package com.myra.dev.marian.commands.moderation.ban;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.mongodb.client.MongoCollection;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Tempban implements CommandHandler {
    private final MongoDb mongoDb = MongoDb.getInstance();

    @CommandEvent(
            name = "tempban",
            aliases = {"tempbean"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        final Utilities utilities = Utilities.getUtils(); // Get utilities
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("tempban", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "tempban <user> <duration><time unit> [reason]`", "\u23F1\uFE0F │ Ban a user for a certain amount of time", false)
                    .setFooter("Accepted time units: seconds, minutes, hours, days");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Tempban
        final String reason = ctx.getArgumentsRaw().split("\\s+", 3)[2]; // Get reason
        final String durationRaw = ctx.getArguments()[1]; // Get duration from the message
        //if the duration is not [NumberLetters]
        if (!durationRaw.matches("[0-9]+[a-zA-z]+")) {
            new Error(ctx.getEvent())
                    .setCommand("tempban")
                    .setEmoji("\u23F1\uFE0F")
                    .setMessage("Invalid time")
                    .setFooter("please note: `<time><time unit>`")
                    .send();
            return;
        }

        Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "tempban", "\u23F1\uFE0F"); // Get member
        if (member == null) return;

        // Return duration
        JSONObject durationList = utilities.getDuration(durationRaw);
        String duration = String.valueOf(durationList.getLong("duration")); // Get duration
        long durationInMilliseconds = durationList.getLong("durationInMilliseconds"); // Get duration in milliseconds
        TimeUnit timeUnit = TimeUnit.valueOf(durationList.getString("timeUnit")); // Get Time unit

        final User user = member.getUser(); // Get member as user
        // Guild message (ban)
        EmbedBuilder guildMessageBan = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got temporary banned", null, user.getEffectiveAvatarUrl())
                .setColor(utilities.red)
                .setDescription("\u23F1\uFE0F │ " + user.getAsMention() + " got banned for **" + duration + " " + timeUnit.toString().toLowerCase() + "**")
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message (ban)
        EmbedBuilder directMessageBan = new EmbedBuilder()
                .setAuthor("You got temporary banned", null, ctx.getGuild().getIconUrl())
                .setColor(utilities.red)
                .setDescription("\u23F1\uFE0F │ You got banned on `" + ctx.getGuild().getName() + "` for **" + duration + " " + timeUnit.toString().toLowerCase() + "**")
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        // With reason
        if (reason != null) {
            guildMessageBan.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            directMessageBan.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
        }
        // Without reason
        else {
            guildMessageBan.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            directMessageBan.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
        }
        // Send messages
        ctx.getChannel().sendMessage(guildMessageBan.build()).queue(); // Send guild messages
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(directMessageBan.build()).queue();
        });
        // With reason
        if (reason != null) member.ban(7, reason).queue();
            // Without reason
        else member.ban(7).queue();

// Unban
        Document document = createUnban(user.getId(), ctx.getGuild().getId(), durationInMilliseconds, ctx.getAuthor().getId()); // Create unban document
        //delay
        Utilities.TIMER.schedule(new Runnable() {

@CommandEvent(
        name = "tempban",
        aliases = {"temp ban", "tempbean", "temp bean"},
        requires = Moderator.class
)
            public void run() {
                ctx.getGuild().unban(user).queue(); // Unban
                unbanMessage(user, ctx.getGuild(), ctx.getAuthor()); // Send unban message
                mongoDb.getCollection("unbans").deleteOne(document); // Delete Document
            }
        }, durationInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public Document createUnban(String userId, String guildId, Long durationInMilliseconds, String moderatorId) {
        MongoCollection<Document> guilds = mongoDb.getCollection("unbans");
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
        for (Document doc : mongoDb.getCollection("unbans").find()) {
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
                        mongoDb.getCollection("unbans").deleteOne(doc); // Delete document
                    }
                }
// Unban time isn't reached yet
                else {
                    // Delay
                    Utilities.TIMER.schedule(() -> {
                        guild.unban(user).queue(); // Unban
                        unbanMessage(user, guild, event.getJDA().getUserById(doc.getString("moderatorId"))); // Send unban message
                        mongoDb.getCollection("unbans").deleteOne(doc); // Delete document
                    }, doc.getLong("unbanTime") - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
            });
        }
    }

    private void unbanMessage(User user, Guild guild, User author) {
        MongoGuild db = new MongoGuild(guild); // Get database
        // Direct message
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("│ You got unbanned", null, guild.getIconUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("\uD83D\uDD13 │ You got unbanned from " + guild.getName())
                .setFooter("requested by " + author.getAsTag(), author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Guild message
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setAuthor("│ " + user.getAsTag() + " got unbanned", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("\uD83D\uDD13 │ " + user.getAsMention() + " got unbanned from " + guild.getName())
                .setFooter("requested by " + author.getAsTag(), author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

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
        final TextChannel textChannel = guild.getTextChannelById(db.getString("logChannel")); // Get log channel

        // Send messages
        textChannel.sendMessage(guildMessage.build()).queue(); // Send guild message
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(directMessage.build()).queue();
        });
    }
}