package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.commands.help.Help;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.Nested;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.permissions.Administrator;

import java.util.Arrays;
import java.util.Map;

@CommandSubscribe(
        name = "toggle",
        requires = Administrator.class,
        channel = Channel.GUILD
)
public class Toggle implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("toggle")
                    .addUsages(new Usage()
                            .setUsage("toggle <command/category>")
                            .setEmoji("\uD83D\uDD11")
                            .setDescription("Toggle commands on and off. You can also toggle entire categories"))
                    .addInformation("You can disable the following categories:" +
                            "\nleveling" +
                            "\nmoderation" +
                            "\nmusic"
                    )
                    .send();
            return;
        }

        Database db = new Database(ctx.getGuild()); // Get database
        // Disable category 'Leveling'
        if (ctx.getArgumentsRaw().equalsIgnoreCase("leveling")) {
            final boolean newValue = toggleLeveling(db);
            // Success information
            Success success = new Success(ctx.getEvent())
                    .setCommand("toggle")
                    .setEmoji("\uD83D\uDD11")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
            if (newValue) success.setMessage("Members can now use `leveling` commands again");
            else success.setMessage("From now on members can no longer use any `leveling` commands");
            success.send(); // Send success message
            return;
        }
        // Disable category 'Moderation'
        if (ctx.getArgumentsRaw().equalsIgnoreCase("moderation")) {
            final boolean newValue = toggleModeration(db);
            // Success information
            Success success = new Success(ctx.getEvent())
                    .setCommand("toggle")
                    .setEmoji("\uD83D\uDD11")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
            if (newValue) success.setMessage("Members can now use `moderation` commands again");
            else success.setMessage("From now on members can no longer use any `moderation` commands");
            success.send(); // Send success message
            return;
        }
        // Disable category 'Music'
        if (ctx.getArgumentsRaw().equalsIgnoreCase("music")) {
            final boolean newValue = toggleMusic(db);
            // Success information
            Success success = new Success(ctx.getEvent())
                    .setCommand("toggle")
                    .setEmoji("\uD83D\uDD11")
                    .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
            if (newValue) success.setMessage("Members can now use `music` commands again");
            else success.setMessage("From now on members can no longer use any `music` commands");
            success.send(); // Send success message
            return;
        }


        // Get command without prefix
        String command;
        if (ctx.getArguments()[0].startsWith(ctx.getPrefix())) {
            command = ctx.getArguments()[0].substring(ctx.getPrefix().length());
        } else command = ctx.getArguments()[0];

        // Go throw every command
        for (Map.Entry<Command, CommandSubscribe> entry : Myra.COMMAND_SERVICE.getCommands().entrySet()) {
            // If a alias or name matches the given command
            if (Arrays.stream(entry.getValue().aliases()).anyMatch(command::equalsIgnoreCase) || command.equalsIgnoreCase(entry.getValue().name())) {
                // Command is a help command
                if (entry.getKey().getClass().getPackage().equals(Help.class.getPackage())) {
                    new Error(ctx.getEvent())
                            .setCommand("toggle")
                            .setEmoji("\uD83D\uDD11")
                            .setMessage("You can't toggle `help` commands")
                            .send();
                    return;
                }
                command = Format.asVariableName(entry.getValue().name()); // Get command name
                boolean newValue = !db.getNested("commands").get(command, Boolean.class); // Get new value of command
                db.getNested("commands").setBoolean(command, newValue); // Update database
                // Success information
                Success success = new Success(ctx.getEvent())
                        .setCommand("toggle")
                        .setEmoji("\uD83D\uDD11")
                        .setAvatar(ctx.getAuthor().getEffectiveAvatarUrl());
                if (newValue) success.setMessage("Members can now use the command `" + command + "` again");
                else success.setMessage("From now on members can no longer use the command `" + command + "`");
                success.send();
                return;
            }
        }
        // Command doesn't exist
        new Error(ctx.getEvent())
                .setCommand("toggle")
                .setEmoji("\uD83D\uDD11")
                .setMessage("The command doesn't exist")
                .send();
    }

    private boolean toggleLeveling(Database db) {
        final Nested commands = db.getNested("commands");

        final Boolean[] musicCommands = {
                commands.getBoolean("rank"),
                commands.getBoolean("leaderboard"),
                commands.getBoolean("editRank"),
        };

        int trueCount = 0;
        for (int i = 0; i < musicCommands.length; i++) {
            if (musicCommands[i]) trueCount++;
        }
        final int percent = trueCount / musicCommands.length;
        boolean newValue = true;
        if (percent > 0.5) newValue = false;

        commands.setBoolean("rank", newValue);
        commands.setBoolean("leaderboard", newValue);
        commands.setBoolean("edit rank", newValue);

        return newValue;
    }

    private boolean toggleModeration(Database db) {
        final Nested commands = db.getNested("commands");

        final Boolean[] musicCommands = {
                commands.getBoolean("clear"),
                commands.getBoolean("nick"),
                commands.getBoolean("kick"),
                commands.getBoolean("mute"),
                commands.getBoolean("ban"),
                commands.getBoolean("unban")
        };

        int trueCount = 0;
        for (int i = 0; i < musicCommands.length; i++) {
            if (musicCommands[i]) trueCount++;
        }
        final int percent = trueCount / musicCommands.length;
        boolean newValue = true;
        if (percent > 0.5) newValue = false;

        commands.setBoolean("clear", newValue);
        commands.setBoolean("nick", newValue);
        commands.setBoolean("kick", newValue);
        commands.setBoolean("mute", newValue);
        commands.setBoolean("ban", newValue);
        commands.setBoolean("unban", newValue);

        return newValue;
    }

    private boolean toggleMusic(Database db) {
        final Nested commands = db.getNested("commands");

        final Boolean[] musicCommands = {
                commands.getBoolean("join"),
                commands.getBoolean("leave"),
                commands.getBoolean("play"),
                commands.getBoolean("skip"),
                commands.getBoolean("clearQueue"),
                commands.getBoolean("shuffle"),
                commands.getBoolean("musicInformation"),
                commands.getBoolean("queue"),
        };

        int trueCount = 0;
        for (int i = 0; i < musicCommands.length; i++) {
            if (musicCommands[i]) trueCount++;
        }
        final int percent = trueCount / musicCommands.length;
        boolean newValue = true;
        if (percent > 0.5) newValue = false;

        commands.setBoolean("join", newValue);
        commands.setBoolean("leave", newValue);
        commands.setBoolean("play", newValue);
        commands.setBoolean("skip", newValue);
        commands.setBoolean("clearQueue", newValue);
        commands.setBoolean("shuffle", newValue);
        commands.setBoolean("musicInformation", newValue);
        commands.setBoolean("queue", newValue);

        return newValue;
    }
}
