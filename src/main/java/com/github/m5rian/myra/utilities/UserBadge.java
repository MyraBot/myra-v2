package com.github.m5rian.myra.utilities;

import com.github.m5rian.myra.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum UserBadge {
    // Myra badges
    MYRA_STAFF("Myra Staff"),
    MYRA_TRANSLATOR("Myra Translator"),
    MYRA_PARTNER("Myra Partner"),
    // HypeSquad
    HYPESQUAD("HypeSquad Events"),
    HYPESQUAD_BRAVERY("HypeSquad Bravery"),
    HYPESQUAD_BRILLIANCE("HypeSquad Brilliance"),
    HYPESQUAD_BALANCE("HypeSquad Balance"),
    // Other
    STAFF("Discord Employee"),
    PARTNER("Partnered Server Owner"),
    BUG_HUNTER_LEVEL_1("Bug Hunter Level 1"),
    EARLY_SUPPORTER("Early Supporter"),
    TEAM_USER("Team User"),
    BUG_HUNTER_LEVEL_2("Bug Hunter Level 2"),
    VERIFIED_BOT("Verified Bot"),
    VERIFIED_DEVELOPER("Early Verified Bot Developer"),

    UNKNOWN("Unknown");

    private final String name;

    UserBadge(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Finds the matching {@link UserBadge}. This method is not case sensitive.
     *
     * @param search The search query to search in the enum.
     * @return Returns a matching {@link UserBadge} to the search.
     */
    public static UserBadge find(String search) {
        return Arrays.stream(UserBadge.values())
                .filter(badge -> badge.getName().equalsIgnoreCase(search))
                .findAny()
                .get();
    }

    /**
     * @param user The user to get the badges from.
     * @return Returns a {@link List<UserBadge>} with all official discord badges of a user.
     */
    public static List<UserBadge> getDiscordBadges(User user) {
        final List<UserBadge> badges = new ArrayList<>();

        // Add all discord badges to list
        for (User.UserFlag flag : user.getFlags()) {
            badges.add(UserBadge.find(flag.getName()));
        }

        return badges;
    }

    /**
     * @param member The member of {@link Config#MARIAN_SERVER_ID}.
     * @return Returns a {@link List<UserBadge>} with all custom myra badges.
     */
    public static List<UserBadge> getMyraBadges(Member member) {
        final List<UserBadge> badges = new ArrayList<>();

        // User is myra staff
        if (member.getRoles().stream().anyMatch(role -> Config.MYRA_TRANSLATOR_ROLE.equals(role.getId())))
            badges.add(UserBadge.MYRA_TRANSLATOR);
        // User is translator
        if (member.getRoles().stream().anyMatch(role -> Config.MYRA_STAFF_ROLE.equals(role.getId())))
            badges.add(UserBadge.MYRA_STAFF);
        // User is myra partner
        if (member.getRoles().stream().anyMatch(role -> Config.MYRA_PARTNER_ROLE.equals(role.getId())))
            badges.add(UserBadge.MYRA_PARTNER);

        return badges;
    }

    /**
     * The readable name as used in the Discord Client.
     *
     * @return The readable name of this UserFlag.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }
}
