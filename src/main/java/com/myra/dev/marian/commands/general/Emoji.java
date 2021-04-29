package com.myra.dev.marian.commands.general;

import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;

public class Emoji implements CommandHandler {

@CommandEvent(
        name = "character",
        aliases = {"char", "emoji"}
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("character")
                    .addUsages(new Usage()
                    .setUsage("character <character/emoji>")
                    .setEmoji("\u2049")
                    .setDescription("Get the Unicode of a character or emoji")
                    ).send();
        }
        if (ctx.getArguments()[0].codePoints().count() > 10) {
            // Invalid
            return;
        }

        Success success = new Success(ctx.getEvent())
                .setCommand("character")
                .setMessage("**Character information of " + ctx.getArguments()[0] + "**");
        ctx.getArguments()[0].codePoints().forEachOrdered(code -> { // Loop through all code point variants
            final char[] chars = Character.toChars(code); // Convert codepoints to characters

            String hex = Integer.toHexString(code).toUpperCase(); // Convert codepoints to UTF hex
            while (hex.length() < 4) hex = "0" + hex; // Make hex at least 4 characters long

            final String name = Character.getName(code).toLowerCase(); // Get name of character
            success.appendMessage("\n• " + (hex.length() == 4 ? "UTF-16" : "UTF-32") + ": `\\u" + hex + "` - " + name); // Add character information

            // Character was displayed in UTF-32
            if (chars.length > 1) {
                String hex0 = Integer.toHexString(chars[0]).toUpperCase(); // Get first part as UTF-16 hex
                String hex1 = Integer.toHexString(chars[1]).toUpperCase(); // Get second part UTF-16 hex
                while (hex0.length() < 4) hex0 = "0" + hex0; // Make hex at least 4 characters long
                while (hex1.length() < 4) hex1 = "0" + hex1; // Make hex at least 4 characters long

                success.appendMessage("\n• UTF-16: `\\u" + hex0 + "\\u" + hex1 + "` - " + name); // Add UTF-16 character information
            }

            //success.appendMessage(" " + String.valueOf(chars)); // Append preview of character
        });
        success.send();
    }
}
