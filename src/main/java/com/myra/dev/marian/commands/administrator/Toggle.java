package com.myra.dev.marian.commands.administrator;

import com.github.m5rian.jdaCommandHandler.*;
import com.myra.dev.marian.DiscordBot;
import com.myra.dev.marian.commands.help.Help;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.Nested;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Format;
import static com.myra.dev.marian.utilities.language.Lang.*;
import com.myra.dev.marian.utilities.permissions.Administrator;

import java.util.Arrays;
import java.util.Map;

public class Toggle implements CommandHandler {

    @CommandEvent(
            name = "toggle",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("toggle")
                    .addUsages(new Usage()
                            .setUsage("toggle <command/category>")
                            .setEmoji("\uD83D\uDD11")
                            .setDescription(lang(ctx).get("description.toggle")))
                    .addInformation(lang(ctx).get("command.toggle.info.info"))
                    .send();
            return;
        }

        final MongoGuild db = new MongoGuild(ctx.getGuild()); // Get database
        final Success success = new Success(ctx.getEvent())
                .setCommand("toggle")
                .setEmoji("\uD83D\uDD11");

        // Category leveling
        if (ctx.getArgumentsRaw().equalsIgnoreCase("leveling")) {
            final boolean newValue = toggleLeveling(db);
            // Category is enabled
            if (newValue)
                success.setMessage(lang(ctx).get("command.toggle.info.category.on")
                        .replace("{$category}", "leveling"));
                // Category is disabled
            else success.setMessage(lang(ctx).get("command.toggle.info.category.off")
                    .replace("{$category}", "leveling"));

            success.send(); // Send success message
            return;
        }
        // Category Moderation
        if (ctx.getArgumentsRaw().equalsIgnoreCase("moderation")) {
            final boolean newValue = toggleModeration(db);
            // Category is enabled
            if (newValue)
                success.setMessage(lang(ctx).get("command.toggle.info.category.on")
                        .replace("{$category}", "moderation"));
                // Category is disabled
            else success.setMessage(lang(ctx).get("command.toggle.info.category.off")
                    .replace("{$category}", "moderation"));

            success.send(); // Send success message
            return;
        }
        // Category music
        if (ctx.getArgumentsRaw().equalsIgnoreCase("music")) {
            final boolean newValue = toggleMusic(db);
            // Category is enabled
            if (newValue)
                success.setMessage(lang(ctx).get("command.toggle.info.category.on")
                        .replace("{$category}", "music"));
                // Category is disabled
            else success.setMessage(lang(ctx).get("command.toggle.info.category.off")
                    .replace("{$category}", "music"));

            success.send(); // Send success message
            return;
        }


        // Get command without prefix
        String command;
        if (ctx.getArguments()[0].startsWith(ctx.getPrefix())) {
            command = ctx.getArguments()[0].substring(ctx.getPrefix().length());
        } else command = ctx.getArguments()[0];

        // Go throw every command
        for (Map.Entry<MethodInfo, CommandEvent> entry : DiscordBot.COMMAND_SERVICE.getCommands().entrySet()) {

            // If a alias or name matches the given command
            if (Arrays.stream(entry.getValue().aliases()).anyMatch(command::equalsIgnoreCase) || command.equalsIgnoreCase(entry.getValue().name())) {
                // Command is a help command
                if (entry.getKey().getClass().getPackage().equals(Help.class.getPackage())) {
                    new Error(ctx.getEvent())
                            .setCommand("toggle")
                            .setEmoji("\uD83D\uDD11")
                            .setMessage(lang(ctx).get("command.toggle.error.helpCommands"))
                            .send();
                    return;
                }
                command = Format.asVariableName(entry.getValue().name()); // Get command name
                boolean newValue = !db.getNested("commands").get(command, Boolean.class); // Get new value of command
                db.getNested("commands").setBoolean(command, newValue); // Update database

                // Success information
                if (newValue) success.setMessage(lang(ctx).get("command.toggle.info.command.on")
                        .replace("{$command}", command)); // Command which got toggled on
                else success.setMessage(lang(ctx).get("command.toggle.info.command.off")
                        .replace("{$command}", command)); // Command which got toggled off

                success.send();
                return;
            }
        }
        // Command doesn't exist
        new Error(ctx.getEvent())
                .setCommand("toggle")
                .setEmoji("\uD83D\uDD11")
                .setMessage(lang(ctx).get("command.toggle.error.notFound"))
                .send();
    }

    private boolean toggleLeveling(MongoGuild db) {
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

    private boolean toggleModeration(MongoGuild db) {
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

    private boolean toggleMusic(MongoGuild db) {
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
