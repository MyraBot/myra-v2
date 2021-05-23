package com.myra.dev.marian.commands.general;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.mongodb.client.MongoCollection;
import com.myra.dev.marian.database.MongoDb;
import com.myra.dev.marian.utilities.EmbedMessage.CommandUsage;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.EmbedMessage.Success;
import com.myra.dev.marian.utilities.EmbedMessage.Usage;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static com.myra.dev.marian.utilities.language.Lang.*;


public class Reminder implements CommandHandler {
    @CommandEvent(
            name = "reminder",
            aliases = {"remind"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            new CommandUsage(ctx.getEvent())
                    .setCommand("reminder")
                    .addUsages(new Usage()
                            .setUsage("reminder <duration><time unit> <description>")
                            .setEmoji("\u23F0")
                            .setDescription(lang(ctx).get("description.general.reminder")))
                    .send();
            return;
        }


        final String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get remind reason
        final String durationRaw = ctx.getArguments()[0]; // Get duration
        // Duration doesn't match [numberLetters]
        if (!durationRaw.matches("[0-9]+[a-zA-z]+")) {
            new Error(ctx.getEvent())
                    .setCommand("reminder")
                    .setEmoji("\u23F0")
                    .setMessage(lang(ctx).get("error.invalidTime"))
                    .send();
            return;
        }
        // Get duration
        final JSONObject durationList = Utilities.getDuration(durationRaw); // Split duration in time and time unit
        String duration = String.valueOf(durationList.getLong("duration")); // Get duration
        long durationInMilliseconds = durationList.getLong("durationInMilliseconds"); // Get duration in milliseconds
        TimeUnit timeUnit = (TimeUnit) durationList.get("timeUnit"); // Get the time unit
        // Reminder info
        new Success(ctx.getEvent())
                .setCommand("reminder")
                .setEmoji("\u23F0")
                .setMessage(lang(ctx).get("command.general.reminder.info")
                        .replace("{$duration}", duration) // Duration
                        .replace("{$timeunit}", timeUnit.toString().toLowerCase())) // Timeout
                .send();
        // Create reminder document
        final Document document = createReminder(ctx.getAuthor().getId(), durationInMilliseconds + System.currentTimeMillis(), reason, timeUnit);
        // Delay
        Utilities.TIMER.schedule(() -> {
            remindMessage(ctx.getAuthor(), reason); //send reminder
            MongoDb.getInstance().getCollection("reminders").deleteOne(document); // Delete document
        }, durationInMilliseconds, TimeUnit.MILLISECONDS);
    }

    //create document
    private Document createReminder(String userId, Long timeInMillis, String description, TimeUnit timeUnit) {
        final MongoCollection<Document> reminders = MongoDb.getInstance().getCollection("reminders"); // Get collection
        // Create Document
        Document docToInsert = new Document()
                .append("userId", userId)
                .append("remindTime", timeInMillis)
                .append("timeUnit", timeUnit.toString())
                .append("description", description);
        reminders.insertOne(docToInsert); // Insert document in database
        return docToInsert; // Return document
    }

    //reminder message
    public void remindMessage(User author, String description) {
        // Open direct message
        author.openPrivateChannel().queue(channel -> {
            new Success(null)
                    .setCommand("reminder")
                    .setAvatar(author.getEffectiveAvatarUrl())
                    .setEmoji("\u23F0")
                    .addField("\u23F0 │ " + defaultLang().get("command.general.reminder.reminder"), description)
                    .setChannel(channel)
                    .send();
        });
    }

    public void onReady(ReadyEvent event) {
        //for each document
        for (Document doc : MongoDb.getInstance().getCollection("reminders").find()) {
            Long timeInMillis = doc.getLong("remindTime"); // Get remind time in milliseconds
            event.getJDA().retrieveUserById(doc.getString("userId")).queue(user -> { // Get user
                if (user == null) return; // No user found

                // Remind time already reached
                if (timeInMillis < System.currentTimeMillis()) {
                    remindMessage(user, doc.getString("description")); // Send reminder
                    MongoDb.getInstance().getCollection("reminders").deleteOne(doc); // Delete document
                }
                // Remind time isn't reached yet
                else {
                    // Delay
                    Utilities.TIMER.schedule(() -> {
                        remindMessage(user, doc.getString("description")); // Send reminder
                        MongoDb.getInstance().getCollection("reminders").deleteOne(doc); // Delete document
                    }, timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
            });
        }
    }
}