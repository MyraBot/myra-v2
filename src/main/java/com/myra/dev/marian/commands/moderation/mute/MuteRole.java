package com.myra.dev.marian.commands.moderation.mute;

import com.myra.dev.marian.database.guild.MongoGuild;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Moderator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

@CommandSubscribe(
        name = "mute role",
        aliases = {"muted role"},
        requires = Moderator.class
)
public class MuteRole implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length != 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("mute role", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "mute role <role>`", "\uD83D\uDD07 │ Change the mute role", true);
            ctx.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        /**
         * Change mute role
         */
        // Get database
        MongoGuild db = new MongoGuild(ctx.getGuild());
        // Get utilities
        Utilities utilities = Utilities.getUtils();
        // Get role
        Role role = utilities.getRole(ctx.getEvent(), ctx.getArguments()[0], "mute role", "\uD83D\uDD07");
        if (role == null) return;
        //get mute role id
        String muteRoleId = db.getString("muteRole");

        Success success = new Success(ctx.getEvent())
                .setCommand("mute role")
                .setEmoji("\uD83D\uDD07")
                .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
        //remove mute role
        if (role.getId().equals(muteRoleId)) {
            //success
            success.setMessage("The mute role will no longer be " + ctx.getGuild().getRoleById(muteRoleId).getAsMention()).send();
            //database
            db.setString("muteRole", role.getId());
            return;
        }
        //change mute role
        db.setString("muteRole", role.getId());
        //role changed
        success.setMessage("Mute role set to " + role.getAsMention()).send();
    }
}
