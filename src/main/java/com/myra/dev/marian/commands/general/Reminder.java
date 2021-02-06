package com.myra.dev.marian.commands.general;

import com.mongodb.client.MongoCollection;
import com.myra.dev.marian.database.MongoDb;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "reminder",
        aliases = {"remind"}
)
public class Reminder implements Command {
    //database
    private final MongoDb mongoDb = MongoDb.getInstance();

    @Override
    public void execute(CommandContext ctx) throws Exception {
        //usage
        if (ctx.getArguments().length == 0) {
            EmbedBuilder usage = new EmbedBuilder()
                    .setAuthor("reminder", null, ctx.getAuthor().getEffectiveAvatarUrl())
                    .setColor(Utilities.getUtils().gray)
                    .addField("`" + ctx.getPrefix() + "reminder <duration><time unit> <description>`", "\u23F0 │ Reminds you after a specific amount of time", false)
                    .setFooter("Accepted time units: seconds, minutes, hours, days");
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }
// Set reminder
        String reason = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Get remind reason
        String durationRaw = ctx.getArguments()[0]; // Get duration
        //if the duration is not (NumberLetters)
        if (!durationRaw.matches("[0-9]+[a-zA-z]+")) {
            new Error(ctx.getEvent())
                    .setCommand("reminder")
                    .setEmoji("\u23F0")
                    .setMessage("Invalid time")
                    .send();
            return;
        }
        // Get duration
        JSONObject durationList = Utilities.getUtils().getDuration(durationRaw); // Split duration in time and time unit
        String duration = String.valueOf(durationList.getLong("duration")); // Get duration
        long durationInMilliseconds = durationList.getLong("durationInMilliseconds"); // Get duration in milliseconds
        TimeUnit timeUnit = (TimeUnit) durationList.get("timeUnit"); // Get the time unit
        // Reminder info
        EmbedBuilder reminderInfo = new EmbedBuilder()
                .setAuthor("reminder", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .setDescription("Im gonna remind you in " + duration + " " + timeUnit.toString().toLowerCase() + "!");
        ctx.getChannel().sendMessage(reminderInfo.build()).queue();
        // Create reminder document
        Document document = createReminder(ctx.getAuthor().getId(), durationInMilliseconds + System.currentTimeMillis(), reason, timeUnit);
        // Delay
        Utilities.TIMER.schedule(() -> {
            remindMessage(ctx.getAuthor(), reason); //send reminder
            mongoDb.getCollection("reminders").deleteOne(document); // Delete document
        }, durationInMilliseconds, TimeUnit.MILLISECONDS);
    }

    //create document
    private Document createReminder(String userId, Long timeInMillis, String description, TimeUnit timeUnit) {
        final MongoCollection<Document> reminders = mongoDb.getCollection("reminders"); // Get collection
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
        // Create embed
        EmbedBuilder reminder = new EmbedBuilder()
                .setAuthor("reminder", null, author.getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().blue)
                .addField("\u23F0 │ reminder", description, false);
        // Send direct message
        author.openPrivateChannel().queue(channel -> {
            channel.sendMessage(reminder.build()).queue();
        });
    }

    public void onReady(ReadyEvent event) {
        //for each document
        for (Document doc : mongoDb.getCollection("reminders").find()) {
            Long timeInMillis = doc.getLong("remindTime"); // Get remind time in milliseconds
            event.getJDA().retrieveUserById(doc.getString("userId")).queue(user -> { // Get user
                if (user == null) return; // No user found

                // Remind time already reached
                if (timeInMillis < System.currentTimeMillis()) {
                    remindMessage(user, doc.getString("description")); // Send reminder
                    mongoDb.getCollection("reminders").deleteOne(doc); // Delete document
                }
                // Remind time isn't reached yet
                else {
                    // Delay
                    Utilities.TIMER.schedule(() -> {
                        remindMessage(user, doc.getString("description")); // Send reminder
                        mongoDb.getCollection("reminders").deleteOne(doc); // Delete document
                    }, timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
            });
        }
    }
}