package com.myra.dev.marian.database.allMethods;

import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class GetListenerManager {
    //variables
    private MongoDb mongoDb;
    private Guild guild;

    //constructor
    public GetListenerManager(MongoDb mongoDb, Guild guild) {
        this.mongoDb = mongoDb;
        this.guild = guild;
    }


    /**
     * Check if listener is enabled
     *
     * @param listener The command to check if its enabled or disabled.
     * @return Returns if the {@param listener} is enabled or disabled as a boolean value.
     * @throws Exception
     */
    public Boolean check(String listener) throws Exception {
        final Document listeners = (Document) mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first().get("listeners");  // Get listener object
        return listeners.getBoolean(listener);  // Return value of listener
    }

    /**
     * Toggle a listener on or off.
     * @param listener The listener to toggle.
     * @param listenerEmoji The emoji of the listener.
     * @param event The GuildMessageReceivedEvent.
     */
    public void toggle(String listener, String listenerEmoji, MessageReceivedEvent event) {
        final Document updatedDocument = mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(); // Get guild document
        final Document listeners = updatedDocument.get("listeners", Document.class); // Get listener document
        final boolean newValue = !listeners.getBoolean(listener); // Get new value of listener
        listeners.replace(listener, newValue); // Replace value
        mongoDb.getCollection("guilds").findOneAndReplace(mongoDb.getCollection("guilds").find(eq("guildId", guild.getId())).first(), updatedDocument); // Update guild document
        // Success information
        Success success = new Success(event)
                .setCommand(listener)
                .setEmoji(listenerEmoji)
                .setAvatar(event.getAuthor().getEffectiveAvatarUrl());
        if (newValue) success.setMessage("`" + listener + "` got toggled on").send();
        else success.setMessage("`" + listener + "` got toggled off").send();
    }
}