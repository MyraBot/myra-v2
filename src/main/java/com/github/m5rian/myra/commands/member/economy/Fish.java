package com.github.m5rian.myra.commands.member.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.language.Lang;

import java.util.*;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Fish implements CommandHandler {
    private final int winChance = 60; // In percent
    private final int looseAmount = 25; // Maximum amount to loose
    private final List<FishSpecies> fish = new ArrayList<>() {{
        add(new FishSpecies("\uD83D\uDC1F", 5)); // 🐟
        add(new FishSpecies("\uD83D\uDC20", 10)); // 🐠
        add(new FishSpecies("\uD83D\uDC21", 10)); //🐡
        add(new FishSpecies("\uD83C\uDF8F", 15)); // 🎏
        add(new FishSpecies("\uD83E\uDD88", 30)); // 🦈
    }};
    private final int maxCatch = fish.stream().max(Comparator.comparing(FishSpecies::price)).get().price();
    private final int looseMessages = 5; // Amount of loose messages

    private record FishSpecies(String emoji, Integer price) {
    }

    @CommandEvent(
            name = "fish",
            emoji = "\uD83C\uDFA3",
            description = "description.economy.fish",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final GuildMember db = GuildMember.get(ctx.getMember()); // Get Member in database
        final String currency = new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"); // Get guild currency

        // Balance limit would be reached
        if (db.getBalance() + maxCatch > Config.ECONOMY_MAX) {
            error(ctx).setDescription(Lang.lang(ctx).get("command.economy.fish.balanceLimit")).send();
            return;
        }

        // Caught a fish
        if (new Random().nextInt(100) <= winChance) {
            final int randomFish = new Random().nextInt(fish.size()); // Get random number
            final FishSpecies caughtFish = fish.get(randomFish); // Get caught fish

            info(ctx).setDescription("\uD83C\uDFA3 " + lang(ctx).get("command.economy.fish.info.caught")
                    .replace("{$fish}", caughtFish.emoji()) // Fish emoji
                    .replace("{$price}", String.valueOf(caughtFish.price())) // Price of fish
                    .replace("{$currency}", currency)) // Guild currency
                    .send();
            db.setBalance(db.getBalance() + caughtFish.price()); // Update balance
        }
        // Didn't catch a fish
        else {
            final int price = new Random().nextInt(looseAmount);
            final int randomLooseMessage = new Random().nextInt(looseMessages - 1);
            final String looseMessage = lang(ctx).get("command.economy.fish.info.loose.0" + randomLooseMessage);
            // Send message
            info(ctx).setDescription("\uD83C\uDFA3 " + looseMessage + lang(ctx).get("command.economy.fish.info.loose.money")
                    .replace("{$price}", String.valueOf(price)) // Price of fish
                    .replace("{$currency}", currency)) // Guild currency
                    .send();
            db.setBalance(db.getBalance() - price); // Update balance
        }
    }

}
