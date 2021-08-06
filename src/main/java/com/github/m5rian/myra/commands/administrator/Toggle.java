package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandData;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.DiscordBot;
import com.github.m5rian.myra.commands.member.help.Help;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.Nested;
import com.github.m5rian.myra.utilities.Format;
import com.github.m5rian.myra.utilities.permissions.Administrator;

import java.util.Arrays;
import java.util.Optional;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Toggle implements CommandHandler {

    @CommandEvent(
            name = "toggle",
            args = {"<command/category>"},
            emoji = "\uD83D\uDD11",
            description = "description.toggle",
            requires = Administrator.class,
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            usage(ctx).setFooter(lang(ctx).get("command.toggle.info.info")).send();
            return;
        }

        final MongoGuild db = MongoGuild.get(ctx.getGuild()); // Get database
        // User wants to toggle a whole category
        if (ctx.getArgumentsRaw().equalsIgnoreCase("leveling") || ctx.getArgumentsRaw().equalsIgnoreCase("moderation") || ctx.getArgumentsRaw().equalsIgnoreCase("music")) {
            boolean newValue;
            String category;

            // Category leveling
            switch (ctx.getArgumentsRaw().toLowerCase()) {
                case "leveling" -> {
                    newValue = toggleLeveling(db);
                    category = "leveling";
                }
                case "moderation" -> {
                    newValue = toggleModeration(db);
                    category = "moderation";
                }
                case "music" -> {
                    newValue = toggleMusic(db);
                    category = "music";
                }
                default -> throw new IllegalStateException("Unexpected value: " + ctx.getArgumentsRaw().toLowerCase());
            }

            // Category got enabled
            if (newValue) info(ctx).setDescription(lang(ctx).get("command.toggle.info.category.on")
                    .replace("{$category}", category))
                    .send();
                // Category is disabled
            else info(ctx).setDescription(lang(ctx).get("command.toggle.info.category.off")
                    .replace("{$category}", category))
                    .send();
        }

        // User wants to disable only a command
        else {
            // Get command without prefix
            String command = ctx.getArgumentsRaw();
            if (ctx.getArguments()[0].startsWith(ctx.getPrefix())) { // Argument starts with prefix
                command = ctx.getArgumentsRaw().substring(ctx.getPrefix().length()); // Remove prefix
            }

            final String query = command; // Copy command variable to final one
            // Try to finding a command matching the search query
            final Optional<CommandData> optionalCommand = DiscordBot.COMMAND_SERVICE.getCommands().stream().filter(commandData -> {
                final CommandEvent commandEvent = commandData.getCommand(); // Get command event annotation
                // Command names are the same
                if (commandEvent.name().equalsIgnoreCase(query)) {
                    return true;
                }
                // An alias matches the command query
                if (Arrays.stream(commandEvent.aliases()).anyMatch(alias -> alias.equalsIgnoreCase(query))) {
                    return true;
                }
                return false;
            }).findFirst();

            // No command found
            if (optionalCommand.isEmpty()) {
                error(ctx).setDescription(lang(ctx).get("command.toggle.error.notFound")).send();
                return;
            }
            // Command exists
            else {
                // Command is a help command
                if (optionalCommand.get().getMethod().getDeclaringClass().getPackageName().equals(Help.class.getPackageName())) {
                    error(ctx).setDescription(lang(ctx).get("command.toggle.error.helpCommands")).send();
                    return;
                }

                final String camelCase = Format.asVariableName(optionalCommand.get().getCommand().name()); // Get command name in camel case
                System.out.println(camelCase);
                final boolean newValue = !db.getNested("commands").getBoolean(camelCase); // Get new value of command
                db.getNested("commands").setBoolean(camelCase, newValue); // Update database

                // Command got toggled on
                if (newValue) info(ctx).setDescription(lang(ctx).get("command.toggle.info.command.on")
                        .replace("{$command}", optionalCommand.get().getCommand().name()))
                        .send();
                // Command got toggled off
                else info(ctx).setDescription(lang(ctx).get("command.toggle.info.command.off")
                        .replace("{$command}", optionalCommand.get().getCommand().name()))
                        .send();
            }
        }

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
