package com.github.m5rian.myra.utilities.language;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Lang {
    public enum Country {
        ENGLISH("en_UK", "English");

        private final String id;
        private final String name;

        Country(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return this.id;
        }
    }

    Map<String, ResourceBundle> languages = new HashMap<>();

    public static LanguageBundle lang(CommandContext ctx) {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle lang(MessageReceivedEvent event) {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle lang(Guild guild) {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle lang(Member member) {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle lang(Message message) {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle defaultLang() {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    private static ResourceBundle getLanguage(Country country) {
        return ResourceBundle.getBundle(country.getId());
    }

}
