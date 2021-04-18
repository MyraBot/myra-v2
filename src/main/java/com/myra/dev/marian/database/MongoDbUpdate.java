package com.myra.dev.marian.database;

import com.mongodb.client.MongoCursor;
import com.myra.dev.marian.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbUpdate {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MongoDbUpdate.class); // Logger

    public static void update(Runnable runnable) {
        final MongoDb mongoDb = MongoDb.getInstance();

        if (Config.updateGuilds) {
            for (Document guildDocument : mongoDb.getCollection("guilds").find()) {
                final Document updatedGuildDocument = updateGuild(guildDocument);
                mongoDb.getCollection("guilds").replaceOne(guildDocument, updatedGuildDocument);
            }
        }


        if (Config.updateUsers) {
            final MongoCursor<Document> iterator = mongoDb.getCollection("users").find().iterator();
            final long totalUserDocuments = mongoDb.getCollection("users").countDocuments(); // Get total amount of documents
            int currentUserDocument = 0;

            while (iterator.hasNext()) {
                currentUserDocument += 1;
                final Document userDocument = iterator.next(); // Skip to the current document
                final Document updatedUserDocument = updateUser(userDocument); // Get updates user document
                mongoDb.getCollection("users").replaceOne(userDocument, updatedUserDocument); // Update document

                long percentage = currentUserDocument * 100 / totalUserDocuments;
                LOGGER.info(percentage + "%     (" + currentUserDocument + "/" + totalUserDocuments + ")");
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
                .append("welcomeColour", welcomeRaw.getString("welcomeColour"))
                .append("welcomeImageBackground", welcomeRaw.getString("welcomeImageBackground"))
                .append("welcomeImageFont", welcomeRaw.getString("welcomeImageFont"))
                .append("welcomeEmbedMessage", welcomeRaw.getString("welcomeEmbedMessage"))
                .append("welcomeDirectMessage", welcomeRaw.getString("welcomeDirectMessage"));
        Document commands = new Document()
                .append("calculate", commandsRaw.getBoolean("calculate"))
                .append("avatar", commandsRaw.getBoolean("avatar"))
                .append("information", commandsRaw.getBoolean("information"))
                .append("reminder", commandsRaw.getBoolean("reminder"))

                .append("rank", commandsRaw.getBoolean("rank"))
                .append("leaderboard", commandsRaw.getBoolean("leaderboard"))
                .append("editRank", commandsRaw.getBoolean("editRank"))

                .append("meme", commandsRaw.getBoolean("meme"))
                .append("textFormatter", commandsRaw.getBoolean("textFormatter"))

                .append("music", commandsRaw.getBoolean("music"))
                .append("join", commandsRaw.getBoolean("join"))
                .append("leave", commandsRaw.getBoolean("leave"))
                .append("play", commandsRaw.getBoolean("play"))
                .append("skip", commandsRaw.getBoolean("skip"))
                .append("clearQueue", commandsRaw.getBoolean("clearQueue"))
                .append("shuffle", commandsRaw.getBoolean("shuffle"))
                .append("musicInformation", commandsRaw.getBoolean("musicInformation"))
                .append("queue", commandsRaw.getBoolean("queue"))

                .append("moderation", commandsRaw.getBoolean("moderation"))
                .append("clear", commandsRaw.getBoolean("clear"))
                .append("nick", commandsRaw.getBoolean("nick"))
                .append("kick", commandsRaw.getBoolean("kick"))
                .append("mute", commandsRaw.getBoolean("mute"))
                .append("ban", commandsRaw.getBoolean("ban"))
                .append("unban", commandsRaw.getBoolean("unban"));
        Document listeners = new Document()
                .append("welcomeImage", listenersRaw.getBoolean("welcomeImage"))
                .append("welcomeEmbed", listenersRaw.getBoolean("welcomeEmbed"))
                .append("welcomeDirectMessage", listenersRaw.getBoolean("welcomeDirectMessage"))

                .append("suggestions", listenersRaw.getBoolean("suggestions"))
                .append("leveling", listenersRaw.getBoolean("leveling"));

        //create Document
        Document updatedDocument = new Document()
                .append("guildId", doc.getString("guildId"))
                .append("guildName", doc.getString("guildName"))
                .append("prefix", doc.getString("prefix"))
                .append("premium", doc.getBoolean("premium"))
                .append("unicorn", doc.getLong("unicorn"))

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

        return updatedDocument;
    }

    private static Document updateUser(Document userDocument) {
        Document updatedUserDocument = new Document() // Create a new user document
                .append("userId", userDocument.getString("userId"))
                .append("name", "not set") // Username
                .append("discriminator", "not set") // User tag
                .append("avatar", "not set") // Avatar url
                .append("birthday", userDocument.getString("birthday"))
                .append("achievements", userDocument.get("achievements", Document.class));

        for (String key : userDocument.keySet()) {
            if (!key.matches("[0-9]+")) continue; // Value isn't a guild document

            final Document guildDocument = userDocument.get(key, Document.class); // Get guild document
            final Document updatedGuildDocument = new Document() // Create a new guild document
                    .append("level", guildDocument.getInteger("level"))
                    .append("xp", guildDocument.getInteger("xp"))
                    .append("messages", guildDocument.getInteger("messages"))
                    .append("voiceCallTime", guildDocument.getLong("voiceCallTime"))
                    .append("balance", guildDocument.getInteger("balance"))
                    .append("dailyStreak", guildDocument.getInteger("dailyStreak"))
                    .append("lastClaim", guildDocument.getLong("lastClaim"))
                    .append("rankBackground", guildDocument.getString("rankBackground"));

            updatedUserDocument.append(key, updatedGuildDocument);
        }

        return updatedUserDocument;
    }

    /**
     * Add Guild Document on guild join.
     *
     * @param event
     * @throws Exception
     */
    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        MongoDocuments.guild(event.getGuild());
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

    }

    /**
     * Change guild name.
     *
     * @param event The GuildUpdateNameEvent event.
     */
    public void onGuildNameUpdate(GuildUpdateNameEvent event) {
        final MongoDb mongoDb = MongoDb.getInstance();
        Document guildDoc = mongoDb.getCollection("guilds").find(eq("guildId", event.getGuild().getId())).first();
        Bson updateGuildDoc = new Document("$set", new Document("guildName", event.getNewValue()));
        mongoDb.getCollection("guilds").findOneAndUpdate(guildDoc, updateGuildDoc);
    }

    //delete document on guild leave
    public void onGuildLeave(GuildLeaveEvent event) {
        final MongoDb mongoDb = MongoDb.getInstance();
        mongoDb.getCollection("guilds").deleteOne(eq("guildId", event.getGuild().getId()));
    }
}
