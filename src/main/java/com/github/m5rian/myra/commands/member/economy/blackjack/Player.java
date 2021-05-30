package com.github.m5rian.myra.commands.member.economy.blackjack;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Player {
    // List of all cards of a user
    private List<Card> playersCards = new ArrayList<>();
    private Member player;

    // Constructor
    public Player(Member member) {
        this.player = member;
    }

    // Get player
    public Member getPlayer() {
        return this.player;
    }

    /**
     * @return Returns all cards of a player.
     */
    public List<Card> getCards() {
        return this.playersCards;
    }

    /**
     * Switch the value of the ace card.
     */
    public void switchAce() {
        Iterator<Card> iterator = playersCards.iterator(); // Create iterator
        // As long as the value of the player is more than 21 or all cards have been checked
        while (getValue() > 21) {
            final Card card = iterator.next(); // Get next card
            if (!iterator.hasNext()) break; // Stop loop if all cards were checked

            // If card is an ace
            if (card.getValue() == 11) {
                playersCards.remove(card); // Remove old card
                playersCards.add(Card.setValueToOne(card)); // Add new one
                break; // Return, so only 1 ace changes their value
            }
        }
    }

    /**
     * @return Returns the value of the cards.
     */
    public Integer getValue() {
        int value = 0;
        for (Card card : playersCards) {
            value += card.getValue();
        }
        return value;
    }

    /**
     * Add card to 'cards'
     *
     * @param card The card to add.
     */
    public void add(Card... card) {
        this.playersCards.addAll(Arrays.asList(card));
    }
}
