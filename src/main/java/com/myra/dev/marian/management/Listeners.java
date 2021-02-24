package com.myra.dev.marian.management;

import com.myra.dev.marian.Myra;
import com.myra.dev.marian.commands.administrator.notifications.NotificationsList;
import com.myra.dev.marian.commands.general.Reminder;
import com.myra.dev.marian.commands.general.information.InformationServer;
import com.myra.dev.marian.commands.help.InviteThanks;
import com.myra.dev.marian.commands.moderation.ban.Tempban;
import com.myra.dev.marian.commands.moderation.mute.MutePermissions;
import com.myra.dev.marian.commands.moderation.mute.Tempmute;
import com.myra.dev.marian.commands.music.MusicController;
import com.myra.dev.marian.commands.music.MusicPlay;
import com.myra.dev.marian.commands.music.MusicVoteListener;
import com.myra.dev.marian.commands.premium.Unicorn;
import com.myra.dev.marian.database.MongoDbUpdate;
import com.myra.dev.marian.listeners.EasterEggs;
import com.myra.dev.marian.listeners.GlobalChat;
import com.myra.dev.marian.listeners.ReactionRoles;
import com.myra.dev.marian.listeners.Someone;
import com.myra.dev.marian.listeners.AutoroleAssign;
import com.myra.dev.marian.listeners.leveling.LevelingListener;
import com.myra.dev.marian.listeners.leveling.VoiceCall;
import com.myra.dev.marian.listeners.notifications.TwitchNotification;
import com.myra.dev.marian.listeners.notifications.YouTubeNotification;
import com.myra.dev.marian.listeners.premium.UnicornChange;
import com.myra.dev.marian.listeners.welcome.WelcomeListener;
import com.myra.dev.marian.marian.Roles;
import com.myra.dev.marian.marian.ServerTracking;
import com.myra.dev.marian.utilities.APIs.Twitch;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Listeners extends ListenerAdapter {
    public static boolean ready = false;
    private final static Logger LOGGER = LoggerFactory.getLogger(Listeners.class);
    private final static String onlineInfo = "Bot online!";
    //Combined Message Events (Combines Guild and Private message into 1 event)
    private final GlobalChat globalChat = new GlobalChat();
    private final NotificationsList notificationsList = new NotificationsList();
    private final ReactionRoles reactionRoles = new ReactionRoles();
    private final InformationServer informationServer = new InformationServer();

    private final LevelingListener levelingListener = new LevelingListener();
    private final Someone someone = new Someone();
    private final EasterEggs easterEggs = new EasterEggs();
    //Guild Member Events
    private final WelcomeListener welcomeListener = new WelcomeListener();
    private final AutoroleAssign autoroleAssign = new AutoroleAssign();
    private final Roles roles = new Roles();
    //Guild Voice Events
    private final VoiceCall voiceCall = new VoiceCall();

    private void online() {
        final int start = 60 - LocalDateTime.now().getMinute() % 60; // Get time to start changing the profile picture

        // Update status
        final int guilds = Myra.shardManager.getGuilds().size(); // Get amount of guilds
        Myra.shardManager.setActivity(Activity.listening(String.format("~help │  %s servers", guilds))); // Update server count

        // Loop
        Utilities.TIMER.scheduleAtFixedRate(this::updateBot, start, 60, TimeUnit.MINUTES);
    }

    private void updateBot() {
        try {
            final int guilds = Myra.shardManager.getGuilds().size(); // Get amount of guilds

            final int random = new Random().nextInt(37 - 1) + 1; // Get random number [min number = 0; max number = 37]
            final String baseUrl = "https://raw.githubusercontent.com/MyraBot/resources/main/profile/"; // Get base url
            final InputStream inputStream = new URL(baseUrl + random + ".png").openStream(); // Get url as input stream

            Myra.shardManager.getShards().forEach(shard -> {
                try {
                    shard.getPresence().setActivity(Activity.listening(String.format("~help │  %s servers", guilds))); // Change status|
                    shard.getSelfUser().getManager().setAvatar(Icon.from(inputStream)).queue(); // Change profile picture
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //JDA Events
    public void onReady(@Nonnull ReadyEvent event) {
        try {
            new MongoDbUpdate().updateDatabase(event); // Update database
            while (!ready) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }
            }

            new Reminder().onReady(event); // Load reminders

            new Tempban().loadUnbans(event); // Load bans
            new Tempmute().onReady(event); // Load mutes

            new Twitch().jdaReady(event); // Get access token for twitch
            // Start notifications
            new YouTubeNotification().start(event);// Start twitch notifications
            new TwitchNotification().jdaReady(event); // Start youtube notifications

            online(); // Change profile picture and activity
            new UnicornChange().change();
            LOGGER.info(onlineInfo);
            Config.startUp = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Message Events
    //Guild (TextChannel) Message Events
    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        try {
            globalChat.messageEdited(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        try {
            if (!ready) return;
            if (event.getUser().isBot()) return; // Don't react to bots

            // Administrator
            notificationsList.switchList(event); // List notification
            reactionRoles.reactionRoleAssign(event); // Reaction roles
            // Commands
            informationServer.guildMessageReactionAddEvent(event);
            // Music
            new MusicVoteListener().onVoteAdd(event);
            new MusicController().guildMessageReactionAddEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        try {
            if (!ready) return;
            reactionRoles.reactionRoleRemove(event); // Reaction roles remove listener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Combined Message Events (Combines Guild and Private message into 1 event)
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!ready) return;
        try {
            final Message msg = event.getMessage();
            if (msg.getFlags().contains(Message.MessageFlag.IS_CROSSPOST)) return; // Message is a server announcement
            if (msg.isWebhookMessage()) return; // Message is a WebHook
            if (event.getAuthor().isBot()) return; // Message is from another bot

            globalChat.onMessage(event);
            levelingListener.onMessage(event);
            someone.onMessage(event);
            easterEggs.onMessage(event);
        } catch (Exception exception) {
            new ErrorCatch().catchError(exception, event);
        }
    }

    //TextChannel Events
    @Override
    public void onTextChannelCreate(@Nonnull TextChannelCreateEvent event) {
        try {
            if (!ready) return;
            // Set permissions for mute role
            new MutePermissions().textChannelCreateEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Guild Events
    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        try {
            if (!ready) return;
            // Add guild document to database
            new MongoDbUpdate().guildJoinEvent(event);
            // Server tracking message
            new ServerTracking().guildJoinEvent(event);
            // Thank message to server owner
            new InviteThanks().guildJoinEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        try {
            if (!ready) return;
            //delete guild document
            new MongoDbUpdate().guildLeaveEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Guild Update Events
    @Override
    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
        try {
            if (!ready) return;
            new MongoDbUpdate().guildNameUpdated(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Guild Member Events
    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        try {
            if (!ready) return;

            welcomeListener.welcome(event); // Welcome
            autoroleAssign.onGuildMemberJoin(event); // Autorole
            roles.exclusive(event); // Exclusive role
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        try {
            //new Roles().categories(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Guild Voice Events
    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event.getChannelJoined()); // Start xp gian
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event.getChannelLeft()); // Update xp for users, who are still in old voice call
            voiceCall.updateXpGain(event.getChannelJoined()); // Update xp for users in new voice call
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        try {
            if (!ready) return;
            voiceCall.stopXpGain(event.getMember()); // Stop xp gain
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceMute(@Nonnull GuildVoiceMuteEvent event) {
        try {
            if (!ready) return;
            voiceCall.updateXpGain(event); // Update xp gain
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}