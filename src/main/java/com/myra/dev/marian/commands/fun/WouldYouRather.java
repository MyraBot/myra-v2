package com.myra.dev.marian.commands.fun;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Random;

@CommandSubscribe(
        name = "would you rather",
        aliases = {"wyr"}
)
public class WouldYouRather implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        //list
        String[] A = {
                "be poor but have lots of friends?",
                "be liked by many but not loved by anyone?",
                "lose the ability to speak?"

        };
        String[] B = {
                "be rich but have no friends?",
                "be liked by nobody, but loved by someone?",
                "lose the ability to read?"
        };
        //output
        Random random = new Random();
        int number = random.nextInt(A.length);

        EmbedBuilder wouldYouRather = new EmbedBuilder();
        wouldYouRather.setAuthor(ctx.getAuthor().getName(), null, ctx.getAuthor().getEffectiveAvatarUrl());
        wouldYouRather.addField("Would you rather ...", "\uD83C\uDDE6 " + A[number] + "\n *or* \n \uD83C\uDDE7 " + B[number], false);

        ctx.getChannel().sendMessage(wouldYouRather.build()).queue((message) -> {
            //reactions
            message.addReaction("\uD83C\uDDE6").queue();
            message.addReaction("\uD83C\uDDE7").queue();
        });
    }
}
