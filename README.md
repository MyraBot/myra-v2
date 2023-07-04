# ✨ Myra v2

This version includes the backend for the [almost good-looking :skull: dashboard](https://github.com/MyraBot/Website-v1).
Other than that, this version adds new features and other code improvements. I advise you to not copy code from here. Use the
code snippets to get inspiration on how to do things, but not to just copy and paste in your project. Also, this Bot uses a
very old Discord API version and probably won't work well with the new changes. Hope you can find some helpful things here!

![Warning](https://raw.githubusercontent.com/MyraBot/.github/main/code-advise.png)

## 📌 Table of content

* [Features](#-features)
* [Dependencies](#-dependencies)

## 📚 Features

* Leveling
    * Changing level up channel
    * Toggle
    * Leveling roles (role rewards)
        * ability to roles unique, you will only have one role at the time
    * Set background of leveling card
    * Rank command with leveling card
    * Voice call time tracking
* Media notifications
    * Changing channel
    * YouTube notifications
    * Twitch notifications
* Reaction roles
* Welcoming
    * Direct message embed
        * Change message content
        * Change embed colour
        * Toggle
    * Guild embed
        * Change message content
        * Change embed colour
        * Toggle
    * Image
        * Change background
        * Change font
        * Toggle
    * Change channel
    * Preview your welcome settings
    * Autorole
* Economy
    * Role shop system
    * Blackjack game
    * Balance command
    * Daily rewards
    * Streak command
    * Little fishing game
    * Give command
* Fun
    * Meme command
    * Text formatter
* Miscellaneous
    * Information command about
        * A member
        * A server
        * A user
        * The bot
    * About command (shows personal information set by the users)
    * Avatar command
    * Calculator
    * Emoji unicode information
    * Reminders
    * Guild suggestions
* Help
    * Commands overview
    * Feature request command
    * Help command
    * Invite command
    * Invite thanks <3
    * Ping command
    * Report bug command
    * Support command
    * Vote command (for top.gg)
* Moderation
    * Banning
        * Banning
        * Temporary ban
        * Unbanning
    * Muting
        * Muting
        * Temporary mute
        * Unmuting
        * Setting a custom mute role
    * Clear messages
    * Kicking
    * Change nickname of user
* Music
    * Filters
    * Music announcer
    * Listing music queue
    * Clear queue command
    * Shuffle queue
    * Repeat queue
    * Join / Leave
    * Play
    * Skip
    * Stop
    * Action voting
* Leaderboard for different categories (leveling, streak, voice time, ...)
* @someone

#### Bot owner only

* Blacklist people
* Get invite by server id
* Special roles
    * Exclusive role (reward for inviting Myra)
* Server tracking
* Set premium (no usage)
* Unicorn role (shhhh!)
* Shutdown

## 📌 Dependencies

* [JDA](https://github.com/DV8FromTheWorld/JDA) Discord API wrapper
* [JDA Utilities](https://github.com/JDA-Applications/JDA-Utilities) Event waiter
* [mongodb](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver) Mongodb driver
* [Lavaplayer](https://github.com/sedmelluq/lavaplayer) Encoding opus audio
* [Lavadsp](https://github.com/natanbc/lavadsp) Audio filters for Lavaplayer
* [Logback classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic) Logging
* [org.json](https://mvnrepository.com/artifact/org.json/json) JSON parser
* [OkHttp](https://square.github.io/okhttp/) Http library
* [jsoup](https://jsoup.org/) HTML parser
* [Spark](https://sparkjava.com/) API-Web server