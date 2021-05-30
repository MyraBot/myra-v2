package com.github.m5rian.myra.commands.member.economy.blackjack;

import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

public class Card {

    // Enum of cards
    public static enum Cards {
        HEART2, HEART3, HEART4, HEART5, HEART6, HEART7, HEART8, HEART9, HEART10, HEARTKING, HEARTQUEEN, HEARTJACK, HEARTACE,
        SPADE2, SPADE3, SPADE4, SPADE5, SPADE6, SPADE7, SPADE8, SPADE9, SPADE10, SPADEKING, SPADEQUEEN, SPADEJACK, SPADEACE,
        DIAMOND2, DIAMOND3, DIAMOND4, DIAMOND5, DIAMOND6, DIAMOND7, DIAMOND8, DIAMOND9, DIAMOND10, DIAMONDKING, DIAMONDQUEEN, DIAMONDJACK, DIAMONDACE,
        CLUB2, CLUB3, CLUB4, CLUB5, CLUB6, CLUB7, CLUB8, CLUB9, CLUB10, CLUBKING, CLUBQUEEN, CLUBJACK, CLUBACE
    }

    // Array of values
    private final static Integer[] cardValues = {
            2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
            2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
            2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
            2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11,
    };

    // Array of emotes
    private final static String[] cardEmotes = {
            "<:Heart2:774361878033203201>", "<:Heart3:774361888582271026>", "<:Heart4:774361899948441620>", "HEART5", "HEART6", "HEART7", "Heart8", "Heart9", "Heart10", "HeartAce",
            "Spade2", "Spade3", "Spade4", "Spade5", "Spade6", "Spade7", "Spade8", "Spade9", "Spade10", "SpadeAce",
            "Diamond2", "Diamond3", "Diamond4", "Diamond5", "Diamond6", "Diamond7", "Diamond8", "Diamond9", "Diamond10", "DiamondAce",
            "Club2", "Club3", "Club4", "Club5", "Club6", "Club7", "Club8", "Club9", "Club10", "ClubAce",
            "King", "Queen", "Jack"
    };

    //Values
    private final Cards card;

    private String cardName;
    private Integer value;

    // Constructor
    public Card(Cards card) {
        this.card = card;

        this.cardName = card.name();
        this.value = cardValues[card.ordinal()];
    }

    /**
     * @return Returns the name of a card.
     */
    public String getName() {
        return this.cardName;
    }

    /**
     * @return Returns the value of a card.
     */
    public Integer getValue() {
        return this.value;
    }

    public String getEmote(JDA jda) {
        if (!jda.getGuildById("776389239293607956").getEmotesByName(card.name(), true).isEmpty()) {
            return jda.getGuildById("776389239293607956").getEmotesByName(card.name(), true).get(0).getAsMention();
        } else if (!jda.getGuildById("776390154054271047").getEmotesByName(card.name(), true).isEmpty()) {
            return jda.getGuildById("776390154054271047").getEmotesByName(card.name(), true).get(0).getAsMention();
        } else return "";
    }

    /**
     * @return Returns a list with all cards.
     */
    public static List<Card> getAll() {
        // Create list for all cards
        List<Card> cardList = new ArrayList<>();
        // Add every card
        for (Cards value : Cards.values()) {
            cardList.add(new Card(value));
        }
        // Return list of all cards
        return cardList;
    }

    /**
     * Change a card's value to 1.
     */
    public static Card setValueToOne(Card card) {
        // Change value
        card.value = 1;
        // Return new card
        return card;
    }
}
