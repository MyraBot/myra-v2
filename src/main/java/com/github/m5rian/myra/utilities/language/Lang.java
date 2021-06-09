package com.github.m5rian.myra.utilities.language;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.CustomEmoji;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

/**
 * @author Marian
 * <p>
 * The guild language manager.
 */
public class Lang {
    public enum Country {
        ENGLISH("en_UK", "English", "English", "\uD83C\uDDEC\uD83C\uDDE7"),
        FRENCH("fr", "French", "Français", "\uD83C\uDDEB\uD83C\uDDF7"),
        CATALAN("ca", "Catalan", "Català", CustomEmoji.CATALAN),
        ITALIAN("it", "Italian", "Italia", "\uD83C\uDDEE\uD83C\uDDF9"),
        GERMAN("de", "German", "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA");

        private final String id;
        private final String name;
        private final String nativeName;
        private final String emoji;
        private final CustomEmoji customEmoji;
        private final boolean isEmote;

        Country(String id, String name, String nativeName, String flag) {
            this.id = id;
            this.name = name;
            this.nativeName = nativeName;
            this.emoji = flag;
            this.customEmoji = null;
            this.isEmote = false;
        }

        Country(String id, String name, String nativeName, CustomEmoji flag) {
            this.id = id;
            this.name = name;
            this.nativeName = nativeName;
            this.emoji = null;
            this.customEmoji = flag;
            this.isEmote = true;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getNativeName() {
            return this.nativeName;
        }

        public String getFlag() {
            if (this.isEmote) return this.customEmoji.getAsMention();
            else return this.emoji;
        }

        public String getCodepoints() {
            if (this.isEmote) return this.customEmoji.getCodepoints();
            else return Utilities.toCodepoints(this.emoji);
        }

        public boolean hasCustomFlag() {
            return this.isEmote;
        }

        public static Country getById(String id) {
            for (Country language : Country.values()) {
                if (language.getId().equals(id)) {
                    return language;
                }
            }
            return ENGLISH;
        }

        public static List<String> getFlagsAsCodepoints() {
            List<String> codepoints = new ArrayList<>();
            for (Country lang : Country.values()) {
                if (lang.isEmote) codepoints.add(lang.customEmoji.getCodepoints());
                else codepoints.add(Utilities.toCodepoints(lang.emoji));
            }
            return codepoints;
        }
    }

    /**
     * Stores the {@link Country} of each {@link Guild}.
     * Through saving it (instead of getting it from the database every time) we can save time.
     */
    public static final Map<String, Country> languages = new HashMap<>();

    public static LanguageBundle lang(CommandContext ctx) {
        // Message is from guild
        if (ctx.getEvent().isFromGuild()) return new LanguageBundle(getGuildLanguage(ctx.getGuild()));
            // Message isn't from guild
        else return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    public static LanguageBundle lang(MessageReceivedEvent event) {
        return new LanguageBundle(getGuildLanguage(event.getGuild()));
    }

    public static LanguageBundle lang(Guild guild) {
        return new LanguageBundle(getGuildLanguage(guild));
    }

    public static LanguageBundle lang(Member member) {
        return new LanguageBundle(getGuildLanguage(member.getGuild()));
    }

    public static LanguageBundle lang(Message message) {
        return new LanguageBundle(getGuildLanguage(message.getGuild()));
    }

    public static LanguageBundle defaultLang() {
        return new LanguageBundle(getLanguage(Country.ENGLISH));
    }

    /**
     * @param guild The guild of which to get the language from.
     * @return Returns a {@link ResourceBundle} with all language specific strings.
     */
    private static ResourceBundle getGuildLanguage(Guild guild) {
        final Country language = languages.get(guild.getId()); // Get language of guild
        return getLanguage(language); // Return language
    }

    /**
     * @param country The language to get.
     * @return Returns a {@link ResourceBundle} with all language specific strings.
     */
    private static ResourceBundle getLanguage(Country country) {
        return ResourceBundle.getBundle("languages/" + country.getId());
    }

    public static void load(List<Guild> guilds) {
        for (Guild guild : guilds) {
            final String languageId = new MongoGuild(guild).getString("lang");
            languages.put(guild.getId(), Country.getById(languageId));
        }
    }

}
