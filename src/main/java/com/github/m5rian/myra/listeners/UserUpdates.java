package com.github.m5rian.myra.listeners;

import com.github.m5rian.myra.database.MongoUser;
import com.github.m5rian.myra.utilities.UserBadge;
import net.dv8tion.jda.api.entities.User;

public class UserUpdates {

    public void onUpdate(User user) {
        final MongoUser mongoUser = new MongoUser(user); // Get user in database
        mongoUser.setName(user.getName()); // Update name
        mongoUser.setDiscriminator(user.getDiscriminator()); // Update discriminator
        mongoUser.setAvatar(user.getEffectiveAvatarUrl()); // Update avatar url
        mongoUser.setBadges(UserBadge.getUserBadges(user)); // Update user badges
    }

}
