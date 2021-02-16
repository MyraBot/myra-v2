package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@CommandSubscribe(
        name = "autorole",
        aliases = {"auto role", "defaultrole", "default role", "joinrole", "join role"},
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class AutoRole implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("autorole")
                    .addUsages(new Usage()
                            .setUsage("autorole <role>")
                            .setEmoji("\uD83D\uDCDD")
                            .setDescription("Give new joined members automatic certain roles")
                    ).send();
            return;
        }

        // Get autorole
        Role role = Utilities.getUtils().getRole(ctx.getEvent(), ctx.getArguments()[0], "autorole", "\uD83D\uDCDD");
        if (role == null) return;

        updateAutoRole(ctx.getEvent(), role);
    }

    public void updateAutoRole(MessageReceivedEvent e, Role role) {
        // Get database
        final Database db = new Database(e.getGuild());
        final List<String> autoRoles = db.getList("autoRole", String.class); // Get autoroles
        Success success = new Success(e)
                .setCommand("auto role")
                .setEmoji("\uD83D\uDCDD");

        //remove autorole
        if (autoRoles.contains(role.getId())) {
            autoRoles.remove(role.getId()); // Remove autorole
            db.setList("autoRole", autoRoles); // Update database
            success.setMessage("New members no longer get " + role.getAsMention()).send(); // Send success message
            // Remove role from every member
            e.getGuild().getMembers().forEach(member -> e.getGuild().removeRoleFromMember(member, role));
        }
        // Add autorole
        else {
            autoRoles.add(role.getId()); // Add autorole
            db.setList("autoRole", autoRoles); // Update database
            success.setMessage("New members get now  " + role.getAsMention()).send(); // Send success message
            // Add role to every member
            e.getGuild().getMembers().forEach(member -> e.getGuild().addRoleToMember(member, role));
        }
    }
}
