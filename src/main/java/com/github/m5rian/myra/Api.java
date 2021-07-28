package com.github.m5rian.myra;

import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class Api {

    public static Object onEmbed(@NotNull Request req, Response res) {
        boolean hasError = false;
        final List<String> messages = new ArrayList<>(); // List for all errors

        final JSONObject json = new JSONObject(req.body()); // Get body as JSONObject
        final JSONObject data = json.getJSONObject("data"); // Get data for embed

        final String title = data.getString("title").isBlank() ? null : data.getString("title");
        final String description = data.getString("description").isBlank() ? null : data.getString("description");
        final String thumbnail = data.getString("thumbnail").isBlank() ? null : data.getString("thumbnail");
        final String image = data.getString("image").isBlank() ? null : data.getString("image");
        final String footer = data.getString("footer").isBlank() ? null : data.getString("footer");

        final String guildId = json.getString("guildId");
        final String sendingType = data.getString("sending-type");
        final String channelId = data.getString("channel");
        final String messageId = data.getString("message-id");

        final EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Utilities.blue);
        if (title != null) embed.setTitle(title);
        if (description != null) embed.setDescription(description);
        if (thumbnail != null) {
            if (!Utilities.isValidURL(image)) {
                hasError = true;
                messages.add("The thumbnail URL is not a valid link");
            } else {
                embed.setTitle(thumbnail);
            }
        }
        if (image != null) {
            if (!Utilities.isValidURL(image)) {
                hasError = true;
                messages.add("The image URL is not a valid link");
            } else {
                embed.setImage(image);
            }
        }
        if (footer != null) embed.setFooter(footer);

        // No error occurred
        if (!hasError) {
            final TextChannel channel = DiscordBot.shardManager.getGuildById(guildId).getTextChannelById(channelId);
            // Send embed
            if (sendingType.equals("send")) {
                // Missing permissions to write in the channel
                if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)) {
                    hasError = true;
                    messages.add("I'm missing the permission \"Send Messages\"");
                }
                // Bot has permissions to write in the channel
                else {
                    channel.sendMessage(embed.build()).queue();
                    messages.add("Your embed was successfully sent in " + channel.getName());
                }
            }

            // Edit an already existing embed
            else {
                try {
                    final Message message = channel.retrieveMessageById(messageId).complete();
                    message.editMessage(embed.build()).queue(); // Edit message
                    messages.add("Your embed was successfully edited in " + channel.getName());
                } catch (IllegalArgumentException e) {
                    hasError = true;
                    messages.add("The provided message id is invalid");
                } catch (ErrorResponseException e) {
                    if (e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                        hasError = true;
                        messages.add("The provided message id is invalid");
                    }
                }
            }
        }

        return response(hasError, messages); // Return desired response
    }

    public static Object onGeneralSave(@NotNull Request req, Response res) {
        final JSONObject json = new JSONObject(req.body()); // Get json

        final String userId = json.getString("userId"); // Get user id of who invokes the actions
        final String guildId = json.getString("guildId"); // Get guild id of the modified guild

        // User has missing permissions to make changes
        if (!hasPermissions(guildId, userId)) {
            return response(true, List.of("Who the fuck are you? Well not somebody who is allowed to do that..."));
        }

        final JSONObject data = json.getJSONObject("data"); // Get data

        final String prefix = data.getString("prefix"); // Get prefix

        // Update prefix cache
        Config.CACHE_PREFIX.put(guildId, prefix);
        // Update guild cache
        final MongoGuild mongoGuild = Config.CACHE_GUILD.get(guildId)
                .setString("prefix", prefix); // Set prefix

        Config.CACHE_GUILD.put(guildId, mongoGuild); // Update cache
        mongoGuild.push(); // Push changes to database

        return response(false, List.of("Successfully saved data"));
    }

    public static Object onWelcomeSave(@NotNull Request req, Response res) {
        final JSONObject json = new JSONObject(req.body()); // Get json

        final String userId = json.getString("userId"); // Get user id of who invokes the actions
        final String guildId = json.getString("guildId"); // Get guild id of the modified guild

        // User has missing permissions to make changes
        if (!hasPermissions(guildId, userId)) {
            return response(true, List.of("Who the fuck are you? Well not somebody who is allowed to do that..."));
        }

        final JSONObject data = json.getJSONObject("data"); // Get data

        final String welcomeChannel = data.getString("welcomeChannelId");

        final boolean welcomeEmbedListener = data.getBoolean("welcomeEmbedToggled"); // Are welcome embeds enabled?
        final boolean welcomeImageListener = data.getBoolean("welcomeImageToggled"); // Are welcome images enabled?
        final boolean welcomeDirectMessageListener = data.getBoolean("welcomeDirectMessageToggled"); // Are welcome direct messages enabled?

        final String welcomeEmbedMessage = data.getString("welcomeEmbedMessage"); // Welcome embed message
        final String welcomeImageBackground = data.getString("welcomeImageBackground"); // Welcome image url
        final String welcomeDirectMessage = data.getString("welcomeDirectMessage"); // Welcome direct message
        // Update guild cache
        final MongoGuild mongoGuild = Config.CACHE_GUILD.get(guildId);
        mongoGuild.getNested("welcome")
                .setString("welcomeChannel", welcomeChannel)
                .setString("welcomeImageBackground", welcomeImageBackground)
                .setString("welcomeEmbedMessage", welcomeEmbedMessage)
                .setString("welcomeDirectMessage", welcomeDirectMessage);
        System.out.println(welcomeImageListener);
        mongoGuild.getNested("listeners")
                .setBoolean("welcomeImage", welcomeImageListener)
                .setBoolean("welcomeEmbed", welcomeEmbedListener)
                .setBoolean("welcomeDirectMessage", welcomeDirectMessageListener);
        Config.CACHE_GUILD.put(guildId, mongoGuild); // Update cache
        mongoGuild.push(); // Push changes to database

        return response(false, List.of("Successfully saved data"));
    }

    private static boolean hasPermissions(String guildId, String userId) {
        return DiscordBot.shardManager.getGuildById(guildId).retrieveMemberById(userId).complete().hasPermission(Permission.MANAGE_SERVER);
    }

    /**
     * Execute the response.
     *
     * @param hasError Did the action fail?
     * @param messages Feedback descriptions of actions.
     * @return Returns a {@link JSONObject} as a {@link String}.
     */
    private static Object response(boolean hasError, @NotNull List<String> messages) {
        final JSONObject response = new JSONObject()
                .put("status", hasError ? "422" : "200")
                .put("messages", new JSONArray());
        messages.forEach(message -> response.getJSONArray("messages").put(message));
        return response.toString();
    }

}
