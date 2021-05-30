package com.github.m5rian.myra.commands.developer;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.permissions.Marian;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class SetGuildPremium implements CommandHandler {
    @CommandEvent(
        name = "set premium",
        requires = Marian.class
)
    public void execute(CommandContext ctx) throws Exception {
        final String guildId = ctx.getArguments()[0]; // Get guild
        // No server found
        if (ctx.getEvent().getJDA().getGuildById(guildId) == null) {
            new Error(ctx.getEvent())
                    .setCommand("set premium")
                    .setMessage("Couldn't find this server")
                    .send();
            return;
        }
        final Guild guild = ctx.getEvent().getJDA().getGuildById(guildId); // Get guild

        final Document document = MongoDb.getInstance().getCollection("guilds").find(eq("guildId", guildId)).first(); // Get guild document
        final boolean newValue = !document.getBoolean("premium"); // Get opposite value of the current value
        document.replace("premium", newValue); // Update premium status

        MongoDb.getInstance().getCollection("guilds").findOneAndReplace(eq("guildId", guildId), document); // Update guild document
        new Success(ctx.getEvent())
                .setCommand("set premium")
                .setAvatar(guild.getIconUrl())
                .setMessage(guild.getName() + " has now premium")
                .setChannel(ctx.getChannel())
                .send();
    }
}
