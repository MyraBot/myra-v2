package com.github.m5rian.myra.commands.developer;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.MongoDb;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Marian;
import com.mongodb.client.model.Filters;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Blacklist implements CommandHandler {

    @CommandEvent(
            name = "blacklist",
            requires = Marian.class
    )
    public void onCommandUsage(CommandContext ctx) {
        // No arguments given
        if (ctx.getArguments().length == 0) {
            // Show command usage
            usage(ctx).addUsages(Blacklist.class)
                    .allowCommands("onBlacklistShow", "onBlacklistUser")
                    .send();
        }
    }

    @CommandEvent(
            name = "blacklist list",
            emoji = "\uD83D\uDCDC",
            description = "Show blacklisted users",
            requires = Marian.class
    )
    public void onBlacklistShow(CommandContext ctx) {
        usage(ctx).addUsages(Blacklist.class)
                .allowCommands("onCommandUsage")
                .send();
    }

    @CommandEvent(
            name = "blacklist user",
            emoji = "\uD83D\uDC64",
            description = "Add or remove a user from the blacklist",
            requires = Marian.class
    )
    public void onBlacklistUser(CommandContext ctx) {
        // Get provided user
        final User user = Utilities.getUser(ctx.getEvent(), ctx.getArguments()[0], ctx.getMethodInfo().getCommand().name(), ctx.getMethodInfo().getCommand().emoji());
        if (user == null) return;

        final Document blacklist = MongoDb.getInstance().getCollection("config").find(Filters.eq("document", "blacklist")).first();
        final List<Document> usersBlacklist = blacklist.getList("users", Document.class); // Get blacklist with all users

        // Remove user from blacklist
        if (usersBlacklist.stream().anyMatch(blacklistedUser -> blacklistedUser.getString("userId").equals(user.getId()))) {
            final Document blacklistedUser = usersBlacklist.stream().filter(blacklistDocument -> blacklistDocument.getString("userId").equals(user.getId())).findFirst().get();
            usersBlacklist.remove(blacklistedUser); // Remove user from blacklist
            MongoDb.getInstance().getCollection("config").findOneAndReplace(Filters.eq("document", "blacklist"), blacklist); // Update database
            ctx.getBlacklist().remove(user.getId()); // Remove user from local blacklist

            info(ctx).setDescription(Lang.lang(ctx).get("command.developer.blacklist.user.add")
                    .replace("{$user.name&tag}", user.getAsTag()))
                    .send();
        }
        // Add user to blacklist
        else {
            final String reason = ctx.getArguments().length > 1 ? ctx.getArgumentsRaw().split("\\s+", 2)[1] : "none";
            // Created document
            final Document blacklistDocument = new Document()
                    .append("userId", user.getId())
                    .append("reason", reason)
                    .append("time", System.currentTimeMillis());
            usersBlacklist.add(blacklistDocument); // Add user to blacklist
            MongoDb.getInstance().getCollection("config").findOneAndReplace(Filters.eq("document", "blacklist"), blacklist); // Update database
            ctx.getBlacklist().add(user.getId()); // Add user to local blacklist

            info(ctx).setDescription(Lang.lang(ctx).get("command.developer.blacklist.user.remove")
                    .replace("{$user.name&tag}", user.getAsTag()))
                    .send();
        }
    }

}
