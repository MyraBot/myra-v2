package com.github.m5rian.myra.commands.member.economy.blackjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
    // Values
    private int bet;
    private List<Card> leftCards;
    private List<Player> players = new ArrayList<>();

    // Constructor
    public Game(int bet, Player... player) {
        // Set bet
        this.bet = bet;
        // Add all cards to Array
        this.leftCards = Card.getAll();
        // Add all players
        this.players.addAll(Arrays.asList(player));
    }

    /**
     * @return Returns all cards, which aren't used yet.
     */
    public List<Card> getLeftCards() {
        return leftCards;
    }

    /**
     * @return Returns the bet money.
     */
    public Integer getBetMoney() {
        return bet;
    }

    /**
     * @return Returns all players in a List.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Remove a card from the 'leftCards' list
     *
     * @param card The card to remove.
     */
    public void removeCard(Card card) {
        leftCards.remove(card);
    }

    public Card getRandomCard() {
        // Generate a random number
        int random = new Random().nextInt(leftCards.size() - 1);
        // Remove the cart from the game
        removeCard(leftCards.get(random));
        // Return a card
        return leftCards.get(random);
    }
}
