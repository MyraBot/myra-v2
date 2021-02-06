package com.myra.dev.marian.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EasterEggs {

    public void onMessage(MessageReceivedEvent event) {
        final var rawMessage = event.getMessage().getContentRaw(); // Get message

        // Chocolate
        if (rawMessage.toLowerCase().contains("i like chocolate")) {
            event.getChannel().sendMessage(String.format("I like chocolate too" +
                    "%n(\\\\__/)" +
                    "%n( • - • )" +
                    "%n/っ \uD83C\uDF6B"))
                    .queue();
        }
    }
}
