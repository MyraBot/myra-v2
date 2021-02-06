package com.myra.dev.marian.listeners.autorole;

import com.myra.dev.marian.database.allMethods.Database;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "autorole",
        aliases = {"auto role", "defaultrole", "default role", "joinrole", "join role"},
        requires = Administrator.class
)
public class AutoRoleSet implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        //command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("auto role", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(utilities.gray)
                    .addField("`" + ctx.getPrefix() + "autorole <role>`", "\uD83D\uDCDD │ Give a new joined member automatic a certain role", true);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Autorole
        // Get autorole
        Role role = utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "autorole", "\uD83D\uDCDD");
        if (role == null) return;
        // Get database
        Database db = new Database(ctx.getGuild());
        Success success = new Success(ctx.getEvent())
                .setCommand("auto role")
                .setEmoji("\uD83D\uDCDD")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove autorole
        if (db.getString("autoRole").equals(role.getId())) {
            success.setMessage("New members no longer get " + ctx.getGuild().getRoleById(db.getString("autoRole")).getAsMention()).send(); // Send success message
            db.set("autoRole", "not set"); // Update database
        } else {
            success.setMessage("New members get now  " + role.getAsMention() + " role").send(); // Send success message
            db.set("autoRole", role.getId()); // Update database
        }
    }
}
