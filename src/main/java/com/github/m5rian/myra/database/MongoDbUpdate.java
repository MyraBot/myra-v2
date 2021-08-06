package com.github.m5rian.myra.database;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Logger;
import com.github.m5rian.myra.utilities.Utilities;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.bson.Document;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbUpdate {
    public static void update(Runnable runnable) {
        final MongoDb mongoDb = MongoDb.getInstance();

        if (Config.UPDATE_GUILDS) {
            for (Document guildDocument : mongoDb.getCollection("guilds").find()) {
                final Document updatedGuildDocument = updateGuild(guildDocument);
                mongoDb.getCollection("guilds").replaceOne(guildDocument, updatedGuildDocument);
            }
        }


        if (Config.UPDATE_USERS) {
            final MongoCursor<Document> iterator = mongoDb.getCollection("users").find().iterator();
            final long totalUserDocuments = mongoDb.getCollection("users").countDocuments(); // Get total amount of documents
            int currentUserDocument = 0;

            while (iterator.hasNext()) {
                currentUserDocument += 1;
                final Document userDocument = iterator.next(); // Skip to the current document
                final Document updatedUserDocument = updateUser(userDocument); // Get updates user document
                mongoDb.getCollection("users").replaceOne(userDocument, updatedUserDocument); // Update document

                long percentage = currentUserDocument * 100L / totalUserDocuments;
                Logger.log(MongoDbUpdate.class, percentage + "%     (" + currentUserDocument + "/" + totalUserDocuments + ")");
            }
        }

        runnable.run();
    }

    private static Document updateGuild(Document doc) {
        // Get variables
        final Document economyRaw = (Document) doc.get("economy");
        final Document levelingRaw = (Document) doc.get("leveling");
        final Document notificationsRaw = (Document) doc.get("notifications");
        final Document welcomeRaw = (Document) doc.get("welcome");
        final Document commandsRaw = (Document) doc.get("commands");
        final Document listenersRaw = (Document) doc.get("listeners");

        Document economy = new Document()
                .append("currency", economyRaw.getString("currency"))
                .append("shop", economyRaw.get("shop", Document.class));
        Document leveling = new Document()
                .append("boost", levelingRaw.getInteger("boost"))
                .append("uniqueRoles", levelingRaw.getBoolean("uniqueRoles"))
                .append("roles", levelingRaw.get("roles", Document.class))
                .append("channel", levelingRaw.getString("channel"));
        Document notifications = new Document()
                .append("channel", notificationsRaw.getString("channel"))
                .append("twitchMessage", notificationsRaw.getString("twitchMessage"))
                .append("twitch", notificationsRaw.getList("twitch", String.class))
                .append("youtubeMessage", notificationsRaw.getString("youtubeMessage"))
                .append("youtube", notificationsRaw.getList("youtube", String.class));
        Document welcome = new Document()
                .append("welcomeChannel", welcomeRaw.getString("welcomeChannel"))
                .append("welcomeColour", Utilities.getBsonString(welcomeRaw, "welcomeColour"))
                .append("welcomeImageBackground", welcomeRaw.getString("welcomeImageBackground"))
                .append("welcomeImageFont", welcomeRaw.getString("welcomeImageFont"))
                .append("welcomeEmbedMessage", welcomeRaw.getString("welcomeEmbedMessage"))
                .append("welcomeDirectMessage", welcomeRaw.getString("welcomeDirectMessage"));

        // Commands
        final Document commands = new Document();
        DiscordBot.COMMAND_SERVICE.getCommands().forEach(command -> {
            final Class<?> commandClass = command.getMethod().getDeclaringClass();
            // Command isn't a command to escape
            if (Arrays.stream(Config.ESCAPED_COMMAND_PACKAGES).noneMatch(dir -> commandClass.getPackageName().startsWith(dir))) {
                final String commandName = Format.asVariableName(command.getCommand().name()); // Get camel case of command name
                // Command was already saved
                if (commandsRaw.containsKey(commandName)) {
                    commands.put(commandName, commandsRaw.getBoolean(commandName));
                }
                // Command wasn't in the guild document yet
                else {
                    commands.put(Format.asVariableName(command.getCommand().name()), true);
                }
            }
        });

        Document listeners = new Document()
                .append("welcomeImage", listenersRaw.getBoolean("welcomeImage"))
                .append("welcomeEmbed", listenersRaw.getBoolean("welcomeEmbed"))
                .append("welcomeDirectMessage", listenersRaw.getBoolean("welcomeDirectMessage"))

                .append("suggestions", listenersRaw.getBoolean("suggestions"))
                .append("leveling", listenersRaw.getBoolean("leveling"));

        //create Document
        return new Document()
                .append("guildId", doc.getString("guildId"))
                .append("guildName", doc.getString("guildName"))
                .append("prefix", doc.getString("prefix"))
                .append("lang", doc.getString("lang"))
                .append("premium", doc.getBoolean("premium"))
                .append("unicorn", Utilities.getBsonLong(doc, "unicorn"))

                .append("economy", economy)
                .append("leveling", leveling)
                .append("notifications", notifications)
                .append("suggestionsChannel", doc.getString("suggestionsChannel"))
                .append("reactionRoles", doc.getList("reactionRoles", Document.class))

                .append("logChannel", doc.getString("logChannel"))
                .append("globalChat", doc.get("globalChat"))
                .append("autoRole", doc.getList("autoRole", String.class))
                .append("muteRole", doc.getString("muteRole"))
                .append("musicVoting", doc.getBoolean("musicVoting"))

                .append("welcome", welcome)
                .append("commands", commands)
                .append("listeners", listeners);
    }

    private static Document updateUser(Document userDocument) {
        Document updatedUserDocument = new Document() // Create a new user document
                .append("userId", userDocument.getString("userId"))
                .append("name", userDocument.getString("name")) // Username
                .append("discriminator", userDocument.getString("discriminator")) // User tag
                .append("avatar", userDocument.getString("avatar")) // Avatar url
                .append("badges", userDocument.getList("badges", String.class))
                .append("xp", Utilities.getBsonLong(userDocument, "xp")) // Global xp
                .append("messages", userDocument.getInteger("messages")) // Global messages
                .append("birthday", userDocument.getString("birthday"))
                .append("socials", userDocument.get("socials", Document.class))
                .append("achievements", userDocument.get("achievements", Document.class));

        for (String key : userDocument.keySet()) {
            if (!key.matches("[0-9]+")) continue; // Value isn't a guild document

            final Document guildDocument = userDocument.get(key, Document.class); // Get guild document
            final Document updatedGuildDocument = new Document() // Create a new guild document
                    .append("level", guildDocument.getInteger("level"))
                    .append("xp", Utilities.getBsonLong(guildDocument, "xp"))
                    .append("messages", Utilities.getBsonLong(guildDocument, "messages"))
                    .append("voiceCallTime", Utilities.getBsonLong(guildDocument, "voiceCallTime"))
                    .append("balance", guildDocument.getInteger("balance"))
                    .append("dailyStreak", guildDocument.getInteger("dailyStreak"))
                    .append("lastClaim", guildDocument.getLong("lastClaim"))
                    .append("rankBackground", guildDocument.getString("rankBackground"));

            updatedUserDocument.append(key, updatedGuildDocument);
        }

        return updatedUserDocument;
    }

    /**
     * Update users stat and
     * add Guild Document on guild join.
     *
     * @param event A {@link GuildJoinEvent}.
     * @throws Exception
     */
    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        MongoDocuments.guild(event.getGuild()); // Add guild to database
    }

    /**
     * Add missing guild documents to database
     *
     * @param event The ReadyEvent.
     * @throws Exception
     */
    public void updateGuilds(ReadyEvent event) throws Exception {
        final MongoDb mongoDb = MongoDb.getInstance();

        // Add missing guilds to the database
        for (Guild guild : event.getJDA().getGuilds()) {
            // Guild isn't in database yet
            if (mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first() == null) {
                MongoDocuments.guild(guild); // Create new guild document
            }
        }

        MongoCursor<Document> iterator = mongoDb.getCollection("guilds").find().iterator();
        while (iterator.hasNext()) {
            Document next = iterator.next();

            String guildId = next.getString("guildId");
            boolean b = event.getJDA().getGuilds().stream().noneMatch(guild -> guild.getId().equals(guildId));
            if (b) {
                mongoDb.getCollection("guilds").findOneAndDelete(eq("guildId", guildId));
            }
        }

    }

    //delete document on guild leave
    public void onGuildLeave(GuildLeaveEvent event) {
        final MongoDb mongoDb = MongoDb.getInstance();
        mongoDb.getCollection("guilds").deleteOne(eq("guildId", event.getGuild().getId()));
    }
}
