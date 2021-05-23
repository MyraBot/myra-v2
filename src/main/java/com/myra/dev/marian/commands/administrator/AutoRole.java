package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class AutoRole implements CommandHandler {

    @CommandEvent(
            name = "autorole",
            aliases = {"auto role", "defaultrole", "default role", "joinrole", "join role"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("autorole")
                    .addUsages(new Usage()
                            .setUsage("autorole <role>")
                            .setEmoji("\uD83D\uDCDD")
                            .setDescription(lang(ctx).get("description.autorole")))
                    .send();
            return;
        }

        // Get autorole
        Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "autorole", "\uD83D\uDCDD");
        if (role == null) return;

        updateAutoRole(ctx.getEvent(), role);
    }

    public void updateAutoRole(MessageReceivedEvent e, Role role) {
        final MongoGuild db = new MongoGuild(e.getGuild()); // Get database
        final List<String> autoRoles = db.getList("autoRole", String.class); // Get autoroles
        Success success = new Success(e)
                .setCommand("auto role")
                .setEmoji("\uD83D\uDCDD");

        // Remove autorole
        if (autoRoles.contains(role.getId())) {
            autoRoles.remove(role.getId()); // Remove autorole
            db.setList("autoRole", autoRoles); // Update database
            success.setMessage(lang(e).get("command.autorole.info.removed")
                    .replace("{$role}", role.getAsMention())) // Old auto role
                    .send(); // Send success message
            e.getGuild().getMembers().forEach(member -> e.getGuild().removeRoleFromMember(member, role).queue()); // Remove role from every member
        }
        // Add autorole
        else {
            autoRoles.add(role.getId()); // Add autorole
            db.setList("autoRole", autoRoles); // Update database
            success.setMessage(lang(e).get("command.autorole.info.added")
                    .replace("{$role}", role.getAsMention())) // Old auto role
                    .send(); // Send success message
            // Add role to every member
            e.getGuild().getMembers().forEach(member -> e.getGuild().addRoleToMember(member, role).queue());
        }
    }
}
