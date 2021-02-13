package com.myra.dev.marian.database;

import com.myra.dev.marian.management.Listeners;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbUpdate {
    //database
    private final MongoDb mongoDb = MongoDb.getInstance();

    //update Database
    public void update(ReadyEvent event) throws Exception {
        //updateGuilds();
        //updateUsers();
    }

    private void updateGuilds() {
        // Guild update
        for (Document doc : mongoDb.getCollection("guilds").find()) {
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
                    .append("musicController", commandsRaw.getBoolean("musicController"))

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

                    .append("suggestions", listenersRaw.getBoolean("suggestions"));

            //create Document
            Document updatedDocument = new Document()
                    .append("guildId", doc.getString("guildId"))
                    .append("guildName", doc.getString("guildName"))
                    .append("prefix", doc.getString("prefix"))
                    .append("premium", doc.getBoolean("premium"))

                    .append("economy", economy)
                    .append("leveling", leveling)
                    .append("notifications", notifications)
                    .append("suggestionsChannel", doc.getString("suggestionsChannel"))
                    .append("reactionRoles", doc.getList("reactionRoles", Document.class))

                    .append("logChannel", doc.getString("logChannel"))
                    .append("globalChat", doc.get("globalChat"))
                    .append("autoRole", doc.getString("autoRole"))
                    .append("muteRole", doc.getString("muteRole"))
                    .append("musicVoting", doc.getBoolean("musicVoting"))
                    .append("welcome", welcome)
                    .append("commands", commands)
                    .append("listeners", listeners);

            // Update document
            mongoDb.getCollection("guilds").findOneAndReplace(doc, updatedDocument);
        }
    }

    private void updateUsers() {
        for (Document userDocument : mongoDb.getCollection("users").find()) {

            Document updatedUserDocument = new Document() // Create a new user document
                    .append("userId", userDocument.getString("userId"))
                    .append("birthday", userDocument.getString("birthday"))
                    .append("achievements", userDocument.get("achievements", Document.class));

            for (String key : userDocument.keySet()) {
                if (key.equals("_id")) continue;
                if (key.equals("userId")) continue;
                if (key.equals("birthday")) continue;
                if (key.equals("achievements")) continue;

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
            // Update document
            mongoDb.getCollection("users").replaceOne(userDocument, updatedUserDocument);
        }
    }

    //add guild document
    public void guildJoinEvent(GuildJoinEvent event) throws Exception {
        MongoDbDocuments.guild(event.getGuild());
    }

    /**
     * Add missing guild documents to database
     *
     * @param event The ReadyEvent.
     * @throws Exception
     */
    public void updateDatabase(ReadyEvent event) throws Exception {
        // Add missing guilds to the database
        for (Guild guild : event.getJDA().getGuilds()) {
            // Guild isn't in database yet
            if (mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first() == null) {
                MongoDbDocuments.guild(guild); // Create new guild document
            }
        }
        // Update the database itself (if necessary)
        update(event);
        Listeners.ready = !Listeners.ready; // Change next to true
    }

    /**
     * Change guild name.
     *
     * @param event The GuildUpdateNameEvent event.
     */
    public void guildNameUpdated(GuildUpdateNameEvent event) {
        Document guildDoc = mongoDb.getCollection("guilds").find(eq("guildId", event.getGuild().getId())).first();
        Bson updateGuildDoc = new Document("$set", new Document("guildName", event.getNewValue()));
        mongoDb.getCollection("guilds").findOneAndUpdate(guildDoc, updateGuildDoc);
    }

    //delete document on guild leave
    public void guildLeaveEvent(GuildLeaveEvent event) {
        mongoDb.getCollection("guilds").deleteOne(eq("guildId", event.getGuild().getId()));
    }
}
