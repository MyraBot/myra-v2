package com.myra.dev.marian.utilities;

import net.dv8tion.jda.api.entities.Message;
import okhttp3.*;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to execute Discord Webhooks with low effort
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Webhook {
    private final OkHttpClient client = new OkHttpClient();

    private final String url;
    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    /**
     * Constructs a new Webhook instance.
     *
     * @param url The webhook URL obtained in Discord.
     */
    public Webhook(String url) {
        this.url = url;
    }

    /**
     * @param content The content of the send message.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * @param content Append a text to the already existing content.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook appendContent(String content) {
        if (this.content == null) this.content = content;
        else this.content += content;
        return this;
    }

    /**
     * @param attachment Add an {@link Message.Attachment} to the webhook message.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook addAttachment(Message.Attachment attachment) {
        if (this.content == null) content = attachment.getUrl() + "\\n";
        else this.content += "\\n" + attachment.getUrl();
        return this;
    }

    /**
     * @param username The displayed username of the message.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * @param avatarUrl The image URL of the avatar.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    /**
     * @param tts Should the message be spoken?
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook setTts(boolean tts) {
        this.tts = tts;
        return this;
    }

    /**
     * @param embed Add an {@link EmbedObject} to your message.
     * @return Returns a {@link Webhook} instance.
     */
    public Webhook addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
        return this;
    }

    /**
     * Send the create Webhook.
     *
     * @throws IOException Throws exception when the post request fails.
     */
    public void send() throws IOException {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        if (this.embeds.size() > 10) {
            throw new IllegalArgumentException("You can't add more than 10 embeds");
        }

        // Send a webhook message
        final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getAsJSONObject().toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final Call call = client.newCall(request); // Create call
        final Response response = call.execute();
        response.close();
    }

    /**
     * Edit a webhook message.
     *
     * @throws IOException Throws exception when the post request fails.
     */
    public void edit(String messageId) throws Exception {
        if (this.content == null && this.embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        if (this.embeds.size() > 10) {
            throw new Exception("You can't add more than 10 embeds");
        }
        if (!this.tts) {
            throw new Exception("You can't use tts on editing messages");
        }

        final Request webhookRequest = new Request.Builder()
                .url(this.url) // Get information about the webhook
                .build();
        final Call webhookCall = client.newCall(webhookRequest);
        Response webhookResponse = webhookCall.execute(); // Execute call
        JSONObject webhookInformation = new JSONObject(webhookResponse.body().string()); // Save response as JSONObject
        webhookResponse.close(); // Close Response

        final String guildId = webhookInformation.getString("guild_id"); // Get guild id
        final String webhookId = webhookInformation.getString("id"); // Get webhook id
        final String webhookToken = webhookInformation.getString("token"); // Get webhook token

        // Create url to edit webhook message
        final String editUrl = "https://discord.com/api/webhooks" + // https://discord.com/developers/docs/resources/webhook#modify-webhook
                "/" + webhookId +
                "/" + webhookToken +
                "/messages" +
                "/" + messageId;

        final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getAsJSONObject().toString());

        final Request editRequest = new Request.Builder()
                .url(editUrl)
                .patch(body)
                .build();

        final Call editCall = client.newCall(editRequest); // Create call
        final Response editResponse = editCall.execute();
        editResponse.close();
    }

    /**
     * An embed builder for a webhook message.
     */
    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private Color color;

        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private final List<Field> fields = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public Color getColor() {
            return color;
        }

        public Footer getFooter() {
            return footer;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public Image getImage() {
            return image;
        }

        public Author getAuthor() {
            return author;
        }

        public List<Field> getFields() {
            return fields;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        private static class Footer {
            private final String text;
            private final String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return text;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Thumbnail {
            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Image {
            private final String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }

    /**
     * Convert the current {@link EmbedObject} instance to a JSONObject.
     * @return Returns {@link JSONObject} out of the current embed.
     */
    private JSONObject getAsJSONObject() {
        //this.content = content.replace("\n", "\\n"); // In JSON \n is \\n

        JSONObject json = new JSONObject();

        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);

        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();

                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());

                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }

                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();

                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();

                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }

                if (image != null) {
                    JSONObject jsonImage = new JSONObject();

                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }

                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();

                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }

                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();

                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }

                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();

                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());

                    jsonFields.add(jsonField);
                }

                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }

            json.put("embeds", embedObjects.toArray());
        }
        return json;
    }
}
