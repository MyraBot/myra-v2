package com.github.m5rian.myra.commands.member.economy.blackjack;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.EmbedMessage.CommandUsage;
import com.github.m5rian.myra.utilities.EmbedMessage.Error;
import com.github.m5rian.myra.utilities.EmbedMessage.Usage;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlackJack implements CommandHandler {
    private HashMap<String, HashMap<String, Game>> games = new HashMap<>();


    @CommandEvent(
            name = "blackjack",
            aliases = {"bj"},
            args = {"<bet>"},
            emoji = "\uD83C\uDCCF",
            description = "description.economy.blackjack",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        // Usage
        if (ctx.getArguments().length != 1) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("blackjack")
                    .addUsages(new Usage()
                            .setUsage("blackjack <bet>")
                            .setEmoji("\uD83C\uDCCF")
                            .setDescription(Lang.lang(ctx).get("description.economy.blackjack")
                                    .replace("{$myra}", ctx.getEvent().getJDA().getSelfUser().getName())))
                    .send();
            return;
        }

        // Search user in games
        if (games.containsKey(ctx.getGuild().getId())) { // Only check for user if guild is already in the hashmap
            AtomicBoolean isPlaying = new AtomicBoolean(false);
            games.get(ctx.getGuild().getId()).values().forEach(game -> {
                if (game.getPlayers().stream().anyMatch(player -> player.getPlayer().getIdLong() == ctx.getMember().getIdLong())) {
                    games.get(ctx.getGuild().getId()).forEach((key, value) -> {
                        if (value == game) {
                            isPlaying.set(true);
                            ctx.getChannel().retrieveMessageById(key).queue(
                                    success -> {
                                        new Error(ctx.getEvent())
                                                .setCommand("blackjack")
                                                .setEmoji("\uD83C\uDCCF")
                                                .setLink(success.getJumpUrl())
                                                .setMessage(Lang.lang(ctx).get("command.economy.blackjack.error.gameIsRunning")
                                                        .replace("{$gameJumpUrl}", success.getJumpUrl())) // Current game
                                                .send();
                                    },
                                    error -> {
                                        games.get(ctx.getGuild().getId()).remove(key);
                                        new Error(ctx.getEvent())
                                                .setCommand("blackjack")
                                                .setEmoji("\uD83C\uDCCF")
                                                .setMessage(Lang.lang(ctx).get("command.economy.blackjack.error.messageRetrieveFailed"))
                                                .send();
                                    }
                            );

                        }
                    });
                }
            });
            if (isPlaying.get()) return;
        }
        // Invalid amount of money
        if (!ctx.getArguments()[0].matches("\\d+")) {
            new Error(ctx.getEvent())
                    .setCommand("blackjack")
                    .setEmoji("\uD83C\uDCCF")
                    .setMessage(Lang.lang(ctx).get("error.invalid"))
                    .send();
            return;
        }
        // If game isn't a test match
        if (!ctx.getArguments()[0].equals("0")) {
            final int win = GuildMember.get(ctx.getMember()).getBalance() + Integer.parseInt(ctx.getArguments()[0]); // Get amount of money you would ge if you win
            // Balance limit would be reached
            if (win > Config.ECONOMY_MAX) {
                new Error(ctx.getEvent())
                        .setCommand("blackjack")
                        .setEmoji("\uD83C\uDCCF")
                        .setMessage("We don't want people to get too rich... What about giving other members money? Then try again!")
                        .send();
                return;
            }
        }
        // Not enough money
        if (GuildMember.get(ctx.getMember()).getBalance() < Integer.parseInt(ctx.getArguments()[0])) {
            new Error(ctx.getEvent())
                    .setCommand("blackjack")
                    .setEmoji("\uD83C\uDCCF")
                    .setMessage(Lang.lang(ctx).get("error.lessMoney"))
                    .send();
            return;
        }


        // Get all players
        final Player player = new Player(ctx.getMember()); // Get member
        final Player dealer = new Player(ctx.getGuild().getMember(ctx.getEvent().getJDA().getSelfUser())); // Get dealer
        // Create a new Game
        final Game game = new Game(Integer.parseInt(ctx.getArguments()[0]), player, dealer);

        // Add cards
        player.add(game.getRandomCard(), game.getRandomCard()); // Add new Cards to player
        dealer.add(game.getRandomCard(), game.getRandomCard()); // Add new Cards to dealer

        // If player's value is more than 21
        if (player.getValue() > 21) player.switchAce(); // Switch ace
        // If dealer's value is more than 21
        if (dealer.getValue() > 21) dealer.switchAce(); // Switch ace

        // Send match message
        ctx.getChannel().sendMessage(getEmbed(player, dealer, game, ctx.getGuild()).build()).queue(message -> {
            final MessageEmbed embed = message.getEmbeds().get(0); // Get send embed

            // Game continues
            if (embed.getFooter().getText().equals(Lang.lang(ctx).get("command.economy.blackjack.message.actionQuestion"))) {
                // Add reactions
                message.addReaction("\u23CF").queue(); // Hit
                message.addReaction("\u23F8").queue(); // Stay

                // Guild isn't in the hashmap yet
                if (!games.containsKey(ctx.getGuild().getId())) {
                    games.put(ctx.getGuild().getId(), new HashMap<>()); // Add guild to hashmap
                }
                games.get(ctx.getGuild().getId()).put(message.getId(), game); // Add game to hashmap

                waitForReaction(message, ctx); // Event waiter
            }
        });
    }

    public void waitForReaction(Message message, CommandContext ctx) {
        ctx.getWaiter().waitForEvent(GuildMessageReactionAddEvent.class)
                .setCondition(e -> !e.getUser().isBot() // Condition
                        && e.getMember().getIdLong() == ctx.getMember().getIdLong()
                        && e.getMessageIdLong() == message.getIdLong())
                .setAction(e -> {
                    // Get variables
                    final String guildId = e.getGuild().getId(); // Get guild id
                    final String messageId = e.getMessageId(); // Get message id

                    // Wrong reaction
                    if (!games.containsKey(guildId)) return;
                    if (!games.get(guildId).containsKey(messageId)) return;

                    final Game game = games.get(guildId).get(messageId); // Get game

                    // Wrong user reacted to the message
                    if (game.getPlayers().get(0).getPlayer().equals(e.getMember()) || game.getPlayers().get(1).getPlayer().equals(e.getMember())) {
                        // Get players
                        final Player player = game.getPlayers().get(0);
                        final Player dealer = game.getPlayers().get(1);
                        // Hit
                        if (e.getReactionEmote().getEmoji().equals("\u23CF")) {
                            player.add(game.getRandomCard()); // Add a new card to player

                            // If player's value is more than 21
                            if (player.getValue() > 21) {
                                player.switchAce(); // Switch ace value
                            }

                            // Update match message
                            message.editMessage(getEmbed(player, dealer, game, e.getGuild()).build()).queue(updateMessage -> {// Update message
                                final MessageEmbed embed = updateMessage.getEmbeds().get(0); // Get embed

                                // gamed continues
                                if (embed.getFooter().getText().equals(Lang.lang(ctx).get("command.economy.blackjack.message.actionQuestion"))) {
                                    e.getReaction().removeReaction(e.getUser()).queue(); // Remove reaction
                                    waitForReaction(message, ctx);
                                }
                                // Game ended
                                else {
                                    message.clearReactions().queue(); // Clear reactions
                                    games.get(guildId).remove(e.getMessageId()); // Remove game
                                }
                            });
                        }
                        //Stay
                        else if (e.getReactionEmote().getEmoji().equals("\u23F8")) {
                            final GuildMember dbMember = GuildMember.get(ctx.getMember()); // Get database

                            // Add cards to the dealer until his card value is at least 17
                            while (dealer.getValue() < 17) {
                                dealer.add(game.getRandomCard()); // Add a random card
                            }

                            String footer = "";
                            final int playerValue = player.getValue(); // Get value of player
                            final int dealerValue = dealer.getValue(); // Get value of dealer
                            // Return credits
                            // Player and dealer have the same value and they aren't over 21
                            if (playerValue == dealerValue && playerValue <= 21) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.tie") // Set footer
                                        .replace("{$amount}", String.valueOf(game.getBetMoney()));
                            }
                            // Won
                            // Player has higher value than dealer and player's value is not more than 21
                            else if (playerValue > dealerValue && playerValue <= 21) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.win") // Set footer
                                        .replace("{$amount}", String.valueOf(game.getBetMoney() * 2)); // Amount of won money
                                dbMember.setBalance(dbMember.getBalance() + game.getBetMoney()); // Add money
                            }
                            // Dealer's value is more than 21
                            else if (dealerValue > 21) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.win") // Set footer
                                        .replace("{$amount}", String.valueOf(game.getBetMoney() * 2)); // Amount of won money
                                dbMember.setBalance(dbMember.getBalance() + game.getBetMoney()); // Add money
                            }
                            // Lost
                            // Dealer has higher value than player and dealer's value is not more than 21
                            else if (dealerValue > player.getValue() && dealerValue <= 21) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.loose"); // Set footer
                                dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                            }
                            // If dealer and player have the same value
                            else if (playerValue == dealerValue) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.loose"); // Set footer
                                dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                            }
                            // Player's value is more than 21
                            else if (playerValue > 21 && dealerValue <= 21) {
                                footer = Lang.lang(ctx).get("command.economy.blackjack.message.loose"); // Set footer
                                dbMember.setBalance(dbMember.getBalance() - game.getBetMoney()); // Remove money
                            }
                            // Create match message
                            EmbedBuilder match = new EmbedBuilder()
                                    .setAuthor("blackjack", null, player.getPlayer().getUser().getEffectiveAvatarUrl())
                                    .setColor(e.getMember().getColor())
                                    // Player cards
                                    .addField(Lang.lang(ctx).get("command.economy.blackjack.message.player")
                                                    .replace("{$cardsValue}", String.valueOf(playerValue)), // Player cards value
                                            getPlayerCards(player, e.getJDA()), // Display player cards as custom emojis
                                            false)
                                    // Dealer cards
                                    .addField(Lang.lang(ctx).get("command.economy.blackjack.message.dealer")
                                                    .replace("{$cardsValue}", String.valueOf(dealerValue)), // Dealer cards value
                                            getPlayerCards(dealer, e.getJDA()), // Display dealer cards as custom emojis
                                            false)
                                    .setFooter(footer);
                            // Update message
                            e.getChannel().editMessageById(e.getMessageId(), match.build()).queue();
                            e.getChannel().editMessageById(e.getMessageId(), match.build()).queue();

                            message.clearReactions().queue(); // Clear reaction
                            games.get(guildId).remove(messageId); // Remove game
                        }
                    }
                })
                .load();
    }

    /**
     * @param player The player.
     * @param dealer The dealer.
     * @param game   The game.
     * @param guild  The guild.
     * @return Returns an embed for the match.
     */
    private EmbedBuilder getEmbed(Player player, Player dealer, Game game, Guild guild) {
        // Create embed
        EmbedBuilder match = new EmbedBuilder()
                .setAuthor("blackjack", null, player.getPlayer().getUser().getEffectiveAvatarUrl())
                // Player cards
                .addField("Your cards: " + player.getValue(), getPlayerCards(player, guild.getJDA()), false);

        match.setColor(player.getPlayer().getColor());

        // Get member in database
        final GuildMember dbMember = GuildMember.get(player.getPlayer());
        // Lost
        // Dealer and player have a value of 21
        if (dealer.getValue() == player.getValue() && dealer.getValue() == 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
        // Dealer's value is 21
        else if (dealer.getValue() == 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
        // If player's value is more than 21
        else if (player.getValue() > 21) {
            // Remove balance
            dbMember.setBalance(dbMember.getBalance() - game.getBetMoney());
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("The dealer won!");
        }
        // Won
        // Player's value is 21
        else if (player.getValue() == 21) {
            // Add balance
            dbMember.setBalance(dbMember.getBalance() + game.getBetMoney() * 2);
            match
                    .addField("Dealer cards: " + dealer.getValue(), getDealerCards(dealer, guild.getJDA(), true), false)
                    .setFooter("You won! +" + game.getBetMoney() * 2);
        }
        // Continue game
        else {
            match
                    .addField("Dealer shows:", getDealerCards(dealer, guild.getJDA(), false), false)
                    .setFooter("Hit or stay?");
        }
        return match;
    }

    /**
     * @param player The player.
     * @param jda    The jda entity.
     * @return Returns a String with the cards of the player as emotes.
     */
    private String getPlayerCards(Player player, JDA jda) {
        // Get cards of player as emotes
        String playerCards = "";
        for (Card playerCard : player.getCards()) {
            playerCards += playerCard.getEmote(jda) + " ";
        }
        return playerCards;
    }

    /**
     * @param dealer The dealer.
     * @param jda    The jda entity.
     * @return Returns a String with the cards of the dealer as emotes.
     */
    private String getDealerCards(Player dealer, JDA jda, boolean showsAll) {
        // Get cards of dealer as emotes
        String dealerCards = "";
        for (Card dealerCard : dealer.getCards()) {
            if (dealer.getCards().get(0).equals(dealerCard) && !showsAll) {
                dealerCards += jda.getGuildById("776389239293607956").getEmotesByName("CardBlank", true).get(0).getAsMention() + " ";
            } else
                dealerCards += dealerCard.getEmote(jda) + " ";

        }
        return dealerCards;
    }
}
