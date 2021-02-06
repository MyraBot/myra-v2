package com.myra.dev.marian.commands.fun;


import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;
import com.myra.dev.marian.Myra;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "format",
        aliases = {"font"}
)
public class TextFormatter implements Command {

    //old german
    private String oldGerman(String text) {
        text = text
                .replace("A", "\uD835\uDD04").replace("a", "\uD835\uDD1E")
                .replace("B", "\uD835\uDD05").replace("b", "\uD835\uDD1F")
                .replace("C", "\u212D").replace("c", "\uD835\uDD20")
                .replace("D", "\uD835\uDD07").replace("d", "\uD835\uDD21")
                .replace("E", "\uD835\uDD08").replace("e", "\uD835\uDD22")
                .replace("F", "\uD835\uDD09").replace("f", "\uD835\uDD23")
                .replace("G", "\uD835\uDD0A").replace("g", "\uD835\uDD24")
                .replace("H", "\u210C").replace("h", "\uD835\uDD8D")
                .replace("I", "ℑ").replace("i", "\uD835\uDD8E")
                .replace("J", "\uD835\uDD0D").replace("j", "\uD835\uDD8F")
                .replace("K", "\uD835\uDD0E").replace("k", "\uD835\uDD28")
                .replace("L", "\uD835\uDD0F").replace("l", "\uD835\uDD29")
                .replace("M", "\uD835\uDD10").replace("m", "\uD835\uDD2A")
                .replace("N", "\uD835\uDD11").replace("n", "\uD835\uDD2B")
                .replace("O", "\uD835\uDD12").replace("o", "\uD835\uDD2C")
                .replace("P", "\uD835\uDD13").replace("p", "\uD835\uDD2D")
                .replace("Q", "\uD835\uDD14").replace("q", "\uD835\uDD2E")
                .replace("R", "ℜ").replace("r", "\uD835\uDD2F")
                .replace("S", "\uD835\uDD16").replace("s", "\uD835\uDD30")
                .replace("T", "\uD835\uDD17").replace("t", "\uD835\uDD31")
                .replace("U", "\uD835\uDD18").replace("u", "\uD835\uDD32")
                .replace("V", "\uD835\uDD19").replace("v", "\uD835\uDD33")
                .replace("W", "\uD835\uDD1A").replace("w", "\uD835\uDD34")
                .replace("X", "\uD835\uDD1B").replace("x", "\uD835\uDD35")
                .replace("Y", "\uD835\uDD1C").replace("y", "\uD835\uDD36")
                .replace("Z", "ℨ").replace("z", "\uD835\uDD37");
        return text;
    }

    //handwritten
    private String handwritten(String text) {
        text = text
                .replace("A", "\uD835\uDC9C").replace("a", "\uD835\uDCB6")
                .replace("B", "\uD835\uDC35").replace("b", "\uD835\uDCB7")
                .replace("C", "\uD835\uDC9E").replace("c", "\uD835\uDCB8")
                .replace("D", "\uD835\uDC9F").replace("d", "\uD835\uDCB9")
                .replace("E", "\uD835\uDC38").replace("e", "\uD835\uDC52")
                .replace("F", "\uD835\uDC39").replace("f", "\uD835\uDCBB")
                .replace("G", "\uD835\uDCA2").replace("g", "\uD835\uDC54")
                .replace("H", "\uD835\uDC3B").replace("h", "\uD835\uDCBD")
                .replace("I", "\uD835\uDC3C").replace("i", "\uD835\uDCBE")
                .replace("J", "\uD835\uDCA5").replace("j", "\uD835\uDCBF")
                .replace("K", "\uD835\uDCA6").replace("k", "\uD835\uDCC0")
                .replace("L", "\uD835\uDC3F").replace("l", "\uD835\uDCC1")
                .replace("M", "\uD835\uDC40").replace("m", "\uD835\uDCC2")
                .replace("N", "\uD835\uDCA9").replace("n", "\uD835\uDCC3")
                .replace("O", "\uD835\uDCAA").replace("o", "\uD835\uDC5C")
                .replace("P", "\uD835\uDCAB").replace("p", "\uD835\uDCC5")
                .replace("Q", "\uD835\uDCAC").replace("q", "\uD835\uDCC6")
                .replace("R", "\uD835\uDC45").replace("r", "\uD835\uDCC7")
                .replace("S", "\uD835\uDCAE").replace("s", "\uD835\uDCC8")
                .replace("T", "\uD835\uDCAF").replace("t", "\uD835\uDCC9")
                .replace("U", "\uD835\uDCB0").replace("u", "\uD835\uDCCA")
                .replace("V", "\uD835\uDCB1").replace("v", "\uD835\uDCCB")
                .replace("W", "\uD835\uDCB2").replace("w", "\uD835\uDCCC")
                .replace("X", "\uD835\uDCB3").replace("x", "\uD835\uDCCD")
                .replace("Y", "\uD835\uDCB4").replace("y", "\uD835\uDCCE")
                .replace("Z", "\uD835\uDCB5").replace("z", "\uD835\uDCCF");
        return text;
    }

    //aesthetic
    private String aesthetic(String text) {
        text = text
                .replace("A", "\uD835\uDD38").replace("a", "\uD835\uDD52")
                .replace("B", "\uD835\uDD39").replace("b", "\uD835\uDD53")
                .replace("C", "\u2102").replace("c", "\uD835\uDD54")
                .replace("D", "\uD835\uDD3B").replace("d", "\uD835\uDD55")
                .replace("E", "\uD835\uDD3C").replace("e", "\uD835\uDD56")
                .replace("F", "\uD835\uDD3D").replace("f", "\uD835\uDD57")
                .replace("G", "\uD835\uDD3E").replace("g", "\uD835\uDD58")
                .replace("H", "\u210D").replace("h", "\uD835\uDD59")
                .replace("I", "\uD835\uDD40").replace("i", "\uD835\uDD5A")
                .replace("J", "\uD835\uDD41").replace("j", "\uD835\uDD5B")
                .replace("K", "\uD835\uDD42").replace("k", "\uD835\uDD5C")
                .replace("L", "\uD835\uDD43").replace("l", "\uD835\uDD5D")
                .replace("M", "\uD835\uDD44").replace("m", "\uD835\uDD5E")
                .replace("N", "ℕ").replace("n", "\uD835\uDD5F")
                .replace("O", "\uD835\uDD46").replace("o", "\uD835\uDD60")
                .replace("P", "ℙ").replace("p", "\uD835\uDD61")
                .replace("Q", "ℚ").replace("q", "\uD835\uDD62")
                .replace("R", "ℝ").replace("r", "\uD835\uDD63")
                .replace("S", "\uD835\uDD4A").replace("s", "\uD835\uDD64")
                .replace("T", "\uD835\uDD4B").replace("t", "\uD835\uDD65")
                .replace("U", "\uD835\uDD4C").replace("u", "\uD835\uDD66")
                .replace("V", "\uD835\uDD4D").replace("v", "\uD835\uDD67")
                .replace("W", "\uD835\uDD4E").replace("w", "\uD835\uDD68")
                .replace("X", "\uD835\uDD4F").replace("x", "\uD835\uDD69")
                .replace("Y", "\uD835\uDD50").replace("y", "\uD835\uDD6A")
                .replace("Z", "ℤ").replace("z", "\uD835\uDD6B");
        return text;
    }

    private final String[] emojis = {
            "\uD83C\uDDE9\uD83C\uDDEA", // 🇩🇪
            "\uD83D\uDD8B", // 🖋
            "\uD83C\uDF39" // 🌹
    };

    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("format", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "format <text>`", "\uD83D\uDDDA │ Change the font of your text", false);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }

        // Format options
        EmbedBuilder selection = new EmbedBuilder()
                .setAuthor("format", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\uD83D\uDDDA │ format options",
                        oldGerman(emojis[0] + " │ Lorem ipsum dolor sit amet.") +
                                "\n" + handwritten(emojis[1] + " │ Lorem ipsum dolor sit amet.") +
                                "\n" + aesthetic(emojis[2] + " │ Lorem ipsum dolor sit amet."),
                        false
                );
        ctx.getChannel().sendMessage(selection.build()).queue(message -> { // Send selection message
            // Add reactions
            message.addReaction(emojis[0]).queue();
            message.addReaction(emojis[1]).queue();
            message.addReaction(emojis[2]).queue();

            Myra.WAITER.waitForEvent(
                    GuildMessageReactionAddEvent.class, // Event to wait for
                    e -> !e.getUser().isBot()// Condition
                            && e.getUser() == ctx.getAuthor()
                            && e.getMessageId().equals(message.getId()),
                    e -> { // Run on event

                        final String reaction = e.getReactionEmote().getEmoji(); // Get reacted emoji
                        // Format message embed
                        EmbedBuilder formatted = new EmbedBuilder()
                                .setAuthor("format", null, e.getUser().getEffectiveAvatarUrl())
                                .setColor(Utilities.getUtils().blue);

                        // Format old german font
                        if (reaction.equals(emojis[0])) formatted.setDescription(oldGerman(ctx.getArgumentsRaw()));
                        // Format handwritten font
                        if (reaction.equals(emojis[1])) formatted.setDescription(handwritten(ctx.getArgumentsRaw()));
                        // Format aesthetic font
                        if (reaction.equals(emojis[2])) formatted.setDescription(aesthetic(ctx.getArgumentsRaw()));

                        message.editMessage(formatted.build()).queue(); // Edit message
                        message.clearReactions().queue(); // Clear reactions
                    },
                    30L, TimeUnit.SECONDS, // Timeout
                    () -> message.clearReactions().queue() // Run on timeout
            );
        });
    }
}
