package com.github.m5rian.myra.commands.moderation.mute;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import static com.github.m5rian.myra.utilities.language.Lang.*;
import com.github.m5rian.myra.utilities.permissions.Moderator;
import net.dv8tion.jda.api.entities.Role;

public class MuteRole implements CommandHandler {
    @CommandEvent(
            name = "mute role",
            aliases = {"muted role"},
            requires = Moderator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("mute role")
                    .addUsages(new Usage()
                            .setUsage("mute role <role>")
                            .setEmoji("\uD83D\uDD07")
                            .setDescription(lang(ctx).get("description.mod.muteRole")))
                    .send();
            return;
        }


        // Get provided role
        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "mute role", "\uD83D\uDD07");
        if (role == null) return;

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final String currentRole = db.getString("muteRole"); // Get current mute role id

        final Success success = new Success(ctx.getEvent())
                .setCommand("mute role")
                .setEmoji("\uD83D\uDD07");

        // Remove mute role
        if (currentRole.equals(role.getId())) {
            success.setMessage(lang(ctx).get("command.mod.muteRole.info.removed")).send(); // Success
            db.setString("muteRole", role.getId()); // Update database
        }
        // Mute role changed
        else {
            success.setMessage(lang(ctx).get("command.mod.muteRole.info.success")
                    .replace("{role}", role.getAsMention())) // New mute role
                    .send();
            db.setString("muteRole", role.getId()); // Update database
        }
    }
}
