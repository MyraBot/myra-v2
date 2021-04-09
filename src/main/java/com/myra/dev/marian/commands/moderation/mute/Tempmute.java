package com.myra.dev.marian.commands.moderation.mute;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "tempmute",
        requires = Moderator.class
)
public class Tempmute implements Command {
    //Get database
    private final MongoDb mongoDb = MongoDb.getInstance();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        Utilities utilities = Utilities.getUtils(); // Get utilities
        //command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("tempmute", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("\uD83D\uDD07 │ tempmute a specific member", "`" + ctx.getPrefix() + "tempmute <user> <duration><time unit> <reason>`", true)
                    .setFooter("Accepted time units: seconds, minutes, hours, days");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Tempmute
        final String reason = Arrays.toString(ctx.getArgumentsRaw().split("\\s+", 3)); // Get reason
        final Member member = utilities.getModifiedMember(ctx.getEvent(), ctx.getArguments()[0], "tempmute", "\uD83D\uDD07"); // Get member
        if (member == null) return;


        String muteRoleId = new MongoGuild(ctx.getGuild()).getString("muteRole"); //Get mute role id
        if (muteRoleId.equals("not set")) { // No mute role set
            new Error(ctx.getEvent())
                    .setCommand("tempmute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage("You didn't specify a mute role")
                    .setFooter("You can only delete an amount between 1 and 100 messages")
                    .send();
            return;
        }
        // String is not [NumberLetters]
        if (!ctx.getArguments()[1].matches("[0-9]+[a-zA-z]+")) {
            new Error(ctx.getEvent())
                    .setCommand("tempmute")
                    .setEmoji("\uD83D\uDD07")
                    .setMessage("Invalid time")
                    .setFooter("please note: `<time><time unit>`")
                    .send();
            return;
        }

        // Return duration
        JSONObject durationRaw = utilities.getDuration(ctx.getArguments()[1]); // Separate time unit from duration
        long duration = durationRaw.getLong("duration"); // Given duration
        long durationInMilliseconds = durationRaw.getLong("durationInMilliseconds"); // Duration in milliseconds
        TimeUnit timeUnit = TimeUnit.valueOf(durationRaw.getString("TimeUnit")); // Time unit

        final User user = member.getUser(); // Get member as user
        // Guild message mute
        final EmbedBuilder muteGuild = new EmbedBuilder()
                .setAuthor(user.getName() + " got tempmuted", null, user.getEffectiveAvatarUrl())
                .setColor(utilities.red)
                .setDescription("\u23F1\uFE0F │ " + user.getAsMention() + " got muted for **" + duration + " " + timeUnit.toString().toLowerCase() + "**")
                .setFooter("requested by " + ctx.getAuthor().getAsTag(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // Direct message mute
        final EmbedBuilder muteDirectMessage = new EmbedBuilder()
                .setAuthor("You got tempmuted on " + ctx.getGuild().getName(), null, ctx.getGuild().getIconUrl())
                .setColor(utilities.red)
                .setDescription("\u23F1\uFE0F │ You got muted on " + ctx.getGuild().getName() + " for **" + duration + " " + timeUnit.toString().toLowerCase() + "**")
                .setFooter("requested by " + ctx.getEvent().getMember().getUser().getName(), ctx.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());

        // With reason
        if (ctx.getArguments().length > 2) {
            muteGuild.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
            muteDirectMessage.addField("\uD83D\uDCC4 │ reason:", reason, false); // Add reason
        }
        // Without reason
        else {
            muteGuild.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
            muteDirectMessage.addField("\uD83D\uDCC4 │ no reason", "there was no reason given", false); // Set reason to none
        }

        // Send messages
        ctx.getChannel().sendMessage(muteGuild.build()).queue(); // Send guild message
        user.openPrivateChannel().queue((channel) -> { // Send direct message
            channel.sendMessage(muteDirectMessage.build()).queue();
        });

        ctx.getGuild().addRoleToMember(ctx.getGuild().getMember(user), ctx.getGuild().getRoleById(muteRoleId)).queue(); // Mute
        Document document = createUnmute(user.getId(), ctx.getGuild().getId(), durationInMilliseconds, ctx.getAuthor().getId()); // Create unmute Document
// Unmute
        // Delay
        Utilities.TIMER.schedule(new Runnable() {
            @Override
            public void run() {
                // Member left the server
                if (ctx.getGuild().getMemberById(document.getString("userId")) == null) {
                    mongoDb.getCollection("unmutes").deleteOne(document); // Delete document
                }
                ctx.getGuild().removeRoleFromMember(document.getString("userId"), ctx.getGuild().getRoleById(muteRoleId)).queue(); // Remove role
                unmuteMessage(user, ctx.getGuild(), ctx.getAuthor()); // Send unmute message
                mongoDb.getCollection("unmutes").deleteOne(document); // Delete unmute document
            }
        }, durationInMilliseconds, TimeUnit.MILLISECONDS);
    }


    // Create unmute document
    public Document createUnmute(String userId, String guildId, Long durationInMilliseconds, String moderatorId) {
        MongoCollection<Document> guilds = mongoDb.getCollection("unmutes");
        // Create Document
        Document docToInsert = new Document()
                .append("userId", userId)
                .append("guildId", guildId)
                .append("unmuteTime", System.currentTimeMillis() + durationInMilliseconds)
                .append("moderatorId", moderatorId);
        guilds.insertOne(docToInsert);

        return docToInsert;
    }

    //unmute message
    private void unmuteMessage(User user, Guild guild, User author) {
        MongoGuild db = new MongoGuild(guild);
        //direct message unmute
        EmbedBuilder directMessage = new EmbedBuilder()
                .setAuthor("You got unmuted from " + guild.getName(), null, guild.getIconUrl())
                .setColor(Utilities.getUtils().green)
                .setDescription("You got unmuted from " + guild.getName())
                .setFooter("requested by " + author.getAsTag(), author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(directMessage.build()).queue();
        });
        //if no channel is set
        if (db.getString("logChannel").equals("not set")) {
            new Error(null)
                    .setCommand("tempban")
                    .setEmoji("\u23F1\uFE0F")
                    .setAvatar(user.getEffectiveAvatarUrl())
                    .setMessage("No log channel specified")
                    .setChannel(guild.getDefaultChannel())
                    .send();
            return;
        }
        //get log channel
        TextChannel textChannel = guild.getTextChannelById(db.getString("logChannel"));
        //guild message unmute
        EmbedBuilder guildMessage = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " got unmuted", null, user.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription(user.getAsMention() + " got unmuted")
                .setFooter("requested by " + author.getAsTag(), author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        textChannel.sendMessage(guildMessage.build()).queue();
    }

    public void onReady(ReadyEvent event) throws Exception {
        //for each document
        for (Document doc : mongoDb.getCollection("unmutes").find()) {
            //get unmute time
            Long unmuteTime = doc.getLong("unmuteTime");
            //get guild
            Guild guild = event.getJDA().getGuildById(doc.getString("guildId"));
            /**
             * if unmute time is already reached
             */
            if (unmuteTime < System.currentTimeMillis()) {
                //if member left the server
                if (event.getJDA().getGuildById(doc.getString("guildId")).getMemberById(doc.getString("userId")) == null) {
                    mongoDb.getCollection("unmutes").deleteOne(doc); //delete document
                }
                // No mute role set
                if (new MongoGuild(guild).getString("muteRole").equals("not set")) {
                    // No mute role set
                    new Error(null)
                            .setCommand("tempban")
                            .setEmoji("\u23F1\uFE0F")
                            .setAvatar(guild.getIconUrl())
                            .setMessage("You didn't specify a mute rol")
                            .setChannel(guild.getDefaultChannel())
                            .send();
                    continue;
                }
                guild.removeRoleFromMember(doc.getString("userId"), guild.getRoleById(new MongoGuild(guild).getString("muteRole"))).queue(); // Remove role
                unmuteMessage(event.getJDA().getUserById(doc.getString("userId")), guild, event.getJDA().getUserById(doc.getString("moderatorId"))); // Send unmute message
                mongoDb.getCollection("unmutes").deleteOne(doc); // Delete document
                continue;
            }
            // User should already be unmuted
            else {
                // Delay
                Utilities.TIMER.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //if member left the server
                        if (event.getJDA().getGuildById(doc.getString("guildId")).getMemberById(doc.getString("userId")) == null) {
                            //delete document
                            mongoDb.getCollection("unmutes").deleteOne(doc);
                        }
                        //unmute
                        guild.removeRoleFromMember(doc.getString("userId"), guild.getRoleById(new MongoGuild(guild).getString("muteRole"))).queue();
                        //send unmute message
                        unmuteMessage(event.getJDA().getUserById(doc.getString("userId")), guild, event.getJDA().getUserById(doc.getString("moderatorId")));
                        //delete document
                        mongoDb.getCollection("unmutes").deleteOne(doc);
                    }
                }, doc.getLong("unmuteTime") - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
        }
    }
}