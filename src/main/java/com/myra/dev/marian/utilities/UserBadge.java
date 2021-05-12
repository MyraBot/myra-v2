package com.myra.dev.marian.utilities;

import javax.annotation.Nonnull;
import java.util.Arrays;

public enum UserBadge {
    // CUSTOM
    MYRA_STAFF("Myra Staff"),
    MYRA_PARTNER("Myra Partner"),
    // HypeSquad
    HYPESQUAD("HypeSquad Events"),
    HYPESQUAD_BRAVERY("HypeSquad Bravery"),
    HYPESQUAD_BRILLIANCE("HypeSquad Brilliance"),
    HYPESQUAD_BALANCE("HypeSquad Balance"),
    // Other
    STAFF( "Discord Employee"),
    PARTNER( "Partnered Server Owner"),
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
     * The readable name as used in the Discord Client.
     *
     * @return The readable name of this UserFlag.
     */
    @Nonnull
    public String getName() {
        return this.name;
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
}
