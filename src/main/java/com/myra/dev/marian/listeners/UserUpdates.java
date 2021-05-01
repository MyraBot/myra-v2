package com.myra.dev.marian.listeners;

import com.myra.dev.marian.database.MongoUser;
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
}
