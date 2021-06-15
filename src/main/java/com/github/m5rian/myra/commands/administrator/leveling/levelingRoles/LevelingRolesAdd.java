package com.github.m5rian.myra.commands.administrator.leveling.levelingRoles;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.commands.administrator.AutoRole;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Role;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class LevelingRolesAdd implements CommandHandler {
    private final AutoRole autoRole = new AutoRole();

    @CommandEvent(
            name = "leveling roles add",
            aliases = {"leveling role add"},
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0 || ctx.getArguments().length > 2) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("leveling roles add")
                    .addUsages(new Usage()
                            .setUsage("leveling roles add <level> <role>")
                            .setEmoji("\uD83D\uDD17")
                            .setDescription(lang(ctx).get("description.leveling.roles.Add")))
                    .send();
            return;
        }

        // If level is not a digit
        if (!ctx.getArguments()[0].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("leveling roles add")
                    .setEmoji("\uD83C\uDFC5")
                    .setMessage(lang(ctx).get("command.leveling.roles.add.info.InvalidLevel"))
                    .send();
            return;
        }

        // Get role
        Role role = Utilities.getRole(ctx.getEvent(), ctx.getArguments()[1], "leveling roles add", "\uD83C\uDFC5");
        if (role == null) return;
        // Get level
        final int level = Integer.parseInt(ctx.getArguments()[0]);

        // Role is a autorole
        if (level == 0) {
            autoRole.updateAutoRole(ctx.getEvent(), role);
            return;
        }

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        db.getLeveling().addLevelingRole(level, role); // Update database
        // Update every member
        ctx.getGuild().loadMembers().onSuccess(members -> members.forEach(member -> {
            if (!member.getUser().isBot()) { // Ignore bots

                // If members level is at least the level of the leveling roles
                if (db.getMembers().getMember(member).getLevel() >= Integer.parseInt(ctx.getArguments()[0])) {
                    ctx.getGuild().addRoleToMember(member, role).queue(); // Add role
                }
            }
        }));

        // Success message
        new Success(ctx.getEvent())
                .setCommand("leveling roles add")
                .setEmoji("\uD83C\uDFC5")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl())
                .setMessage(lang(ctx).get("command.leveling.roles.add.info.levelingRoleAdded")
                        .replace("{$role}", role.getAsMention()) // Mention role
                        .replace("{$level}", String.valueOf(level))) // Level
                .send();
    }
}
