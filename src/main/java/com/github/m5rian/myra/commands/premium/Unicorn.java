package com.github.m5rian.myra.commands.premium;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.EmbedMessage.Success;
import net.dv8tion.jda.api.entities.Role;

public class Unicorn implements CommandHandler {

@CommandEvent(
        name = "unicorn",
        requires = Administrator.class
)
    public void execute(CommandContext ctx) throws Exception {
        final MongoGuild db = new MongoGuild(ctx.getGuild());
        if (!db.getBoolean("premium")) return; // Check for premium

        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("unicorn")
                    .addUsages(new Usage()
                            .setUsage("unicorn <role>")
                            .setEmoji("\uD83E\uDD84")
                            .setDescription("Let a role change their colour")
                            .isPremium()
                    ).send();
            return;
        }

        final Role role = Utilities.getRole(ctx.getEvent(), ctx.getArgumentsRaw(), "unicorn", "\uD83E\uDD84");
        if (role == null) return;

        final Long unicornRole = db.getLong("unicorn"); // Get current unicorn role
        // Remove role
        if (unicornRole == 0) {
            db.setLong("unicorn", role.getIdLong());
            new Success(ctx.getEvent())
                    .setCommand("unicorn")
                    .setEmoji("\uD83E\uDD84")
                    .setMessage(String.format("Added **%s** as unicorn role", role.getAsMention()))
                    .send();
        }
        // Change role
        else {
            db.setLong("unicorn", 0L); // Set unicorn role to null
            new Success(ctx.getEvent())
                    .setCommand("unicorn")
                    .setEmoji("\uD83E\uDD84")
                    .setMessage("Removed unicorn role")
                    .send();
        }
    }
}
