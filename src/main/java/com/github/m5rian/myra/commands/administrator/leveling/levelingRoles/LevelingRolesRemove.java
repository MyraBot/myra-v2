package com.github.m5rian.myra.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class LevelingRolesRemove implements CommandHandler {

    @CommandEvent(
            name = "leveling roles remove",
            aliases = {"leveling role remove"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling roles remove")
                    .addUsages(new Usage()
                            .setUsage("leveling roles remove <role>")
                            .setEmoji("\uD83D\uDDD1")
                            .setDescription(lang(ctx).get("description.leveling.roles.Remove")))
                    .send();
            return;
        }

        // Get role
        Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "leveling roles remove", "\uD83C\uDFC5");
        if (role == null) return;

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        db.getLeveling().removeLevelingRole(role); // Remove role from database
        final Document roleDocument = db.getLeveling().getLevelingRole(role.getId()); // Get leveling role from the database

        // Update every member
        for (Member member : ctx.getGuild().getMembers()) {
            if (member.getUser().isBot()) continue; // Ignore bots

            // If members level is at least the level of the leveling roles
            if (GuildMember.get(member).getLevel() >= roleDocument.getInteger("level")) {
                ctx.getGuild().removeRoleFromMember(member, role).queue(); // Remove leveling role
            }
        }
        // Success message
        new Success(ctx.getEvent())
                .setCommand("leveling roles remove")
                .setEmoji("\uD83C\uDFC5")
                .setMessage(
                        lang(ctx).get("command.leveling.roles.remove.info.success")
                                .replace("{$role}", role.getAsMention()) // Old leveling role as mention
                                .replace("{$level}", String.valueOf(roleDocument.getInteger("level")))) // Required level
                .send();
    }
}
