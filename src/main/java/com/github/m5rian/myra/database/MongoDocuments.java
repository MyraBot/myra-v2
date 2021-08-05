package com.github.m5rian.myra.database;

import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class MongoDocuments {

    public static void guild(Guild guild) throws Exception {
        MongoDb mongoDb = MongoDb.getInstance();

        // In database is already a guild document
        if (mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first() != null) return;

        // Economy
        Document economy = new Document()
                .append("currency", CustomEmoji.COIN.getAsMention())
                .append("shop", new Document());
        // Leveling
        Document levelingDocument = new Document()
                .append("boost", 1)
                .append("uniqueRoles", false)
                .append("roles", new Document())
                .append("channel", "not set");
        // Notification
        Document notificationsDocument = new Document()
                .append("channel", "not set")
                .append("twitchMessage", "not set")
                .append("twitch", new ArrayList<String>())
                .append("youtubeMessage", "not set")
                .append("youtube", new ArrayList<String>());

        // Commands
        final Document commands = new Document();
        DiscordBot.COMMAND_SERVICE.getCommands().forEach(command -> {
            final Class<?> commandClass = command.getMethod().getDeclaringClass();
            // Command isn't a command to escape
            if (Arrays.stream(Config.ESCAPED_COMMAND_PACKAGES).noneMatch(dir -> commandClass.getPackageName().startsWith(dir))) {
                commands.put(Format.asVariableName(command.getCommand().name()), true);
            }
        });
        //listeners
        Document listeners = new Document()
                // Welcome
                .append("welcomeImage", false)
                .append("welcomeEmbed", false)
                .append("welcomeDirectMessage", false)
                // Suggestions
                .append("suggestions", false)
                // Leveling
                .append("leveling", true);
        //welcome
        Document welcome = new Document()
                .append("welcomeChannel", "not set")
                .append("welcomeColour", String.valueOf(Utilities.blue))
                .append("welcomeImageBackground", "not set")
                .append("welcomeImageFont", "default")
                .append("welcomeEmbedMessage", "Welcome {member} to {server}! Enjoy your stay")
                .append("welcomeDirectMessage", "Welcome {member} to {server}! Enjoy your stay");
        // Create document
        Document guildDoc = new Document("guildId", guild.getId())
                .append("guildName", guild.getName())
                .append("prefix", Config.DEFAULT_PREFIX)
                .append("lang", Lang.Country.ENGLISH.getIsoCode())
                .append("premium", false)
                .append("unicorn", null)

                .append("economy", economy)
                .append("leveling", levelingDocument)
                .append("notifications", notificationsDocument)

                .append("suggestionsChannel", "not set")
                .append("logChannel", "not set")
                .append("globalChat", null)
                .append("reactionRoles", new ArrayList<>())
                .append("autoRole", new ArrayList<>())
                .append("muteRole", "not set")
                .append("musicVoting", false)

                .append("welcome", welcome)
                .append("commands", commands)
                .append("listeners", listeners);
        mongoDb.getCollection("guilds").insertOne(guildDoc);
    }

    public static Document createUserDocument(User user) {
        return new Document()
                .append("userId", user.getId()) // Id
                .append("name", user.getName()) // Username
                .append("discriminator", user.getDiscriminator()) // User tag
                .append("badges", new ArrayList<String>()) // badges
                .append("xp", 0)
                .append("messages", 0)
                .append("avatar", user.getEffectiveAvatarUrl())
                .append("birthday", "not set")
                .append("achievements", new Document());
    }

    public static Document createGuildMemberDocument() {
        return new Document()
                .append("level", 0)
                .append("xp", 0)
                .append("messages", 0)
                .append("voiceCallTime", Long.valueOf(0))
                .append("balance", 0)
                .append("dailyStreak", 0)
                .append("lastClaim", System.currentTimeMillis())
                .append("rankBackground", "default");
    }
}