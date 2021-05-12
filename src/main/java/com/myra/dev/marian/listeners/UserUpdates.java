package com.myra.dev.marian.listeners;

import com.myra.dev.marian.database.MongoUser;
import com.myra.dev.marian.utilities.UserBadge;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;

public class UserUpdates {

    public void onNameChange(UserUpdateNameEvent event) {
        new MongoUser(event.getUser()).setName(event.getNewName());
    }

    public void onDiscriminatorChange(UserUpdateDiscriminatorEvent event) {
        new MongoUser(event.getUser()).setDiscriminator(event.getNewDiscriminator());
    }

    public void onAvatarChange(UserUpdateAvatarEvent event) {
        new MongoUser(event.getUser()).setAvatar(event.getNewAvatarUrl());
    }

    public void onBadgesChange(User user) {
        new MongoUser(user).setBadges(UserBadge.getUserBadges(user)); // Update user badges
    }
}
