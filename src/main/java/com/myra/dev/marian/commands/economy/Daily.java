package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandHandler;import com.myra.dev.marian.utilities.APIs.DiscordBoats;
import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.TimeUnit;

public class Daily implements CommandHandler {

@CommandEvent(
        name = "daily",
        channel = Channel.GUILD
)
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final GuildMember member = new MongoGuild(ctx.getGuild()).getMembers().getMember(ctx.getEvent().getMember()); // Get member from database
        long lastClaim = member.getLastClaim(); // Get last claimed reward


        long passedTime = System.currentTimeMillis() - lastClaim; // Get duration, which passed (in milliseconds)
        final String currency = new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency").toString(); // Get currency

        // Create embed
        EmbedBuilder daily = new EmbedBuilder()
                .setAuthor("daily", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(ctx.getMember().getColor());

        // 12 didn't pass
        if (TimeUnit.MILLISECONDS.toHours(passedTime) < 12) {
            final long nextBonusAt = lastClaim + TimeUnit.HOURS.toMillis(12); // Get duration until you can claim your reward
            String nextBonusIn = Format.toTime(nextBonusAt - System.currentTimeMillis()); // Make time look nicer

            daily.setDescription("You need to wait more " + nextBonusIn); // Set description
            ctx.getChannel().sendMessage(daily.build()).queue(); // Send message
            return;
        }

        // Claim reward
        if (TimeUnit.MILLISECONDS.toHours(passedTime) >= 12) {

            int voteBonus = 0; // Create vote bonus
            if (TopGG.getInstance().hasVoted(ctx.getAuthor())) { // Check if user voted bot on top.gg
                voteBonus += 100; // Add 100 money to the vote bonus
            }
            if (DiscordBoats.getInstance().hasVoted(ctx.getAuthor())) { // Check if user voted bot on discord.boats
                voteBonus += 100; // Add 100 money to the vote bonus
            }

            // Missed reward
            if (TimeUnit.MILLISECONDS.toHours(passedTime) > 36) {
                member.setDailyStreak(1); // Reset daily streak
            }
            // New reward
            else member.setDailyStreak(member.getDailyStreak() + 1); // Update daily streak

            // Get streak bonus
            int streakReward;
            if (member.getDailyStreak() > 14) streakReward = 14 * 100; // You can't get a higher streak than 14
            else streakReward = member.getDailyStreak() * 100; // Get streak reward

            final int dailyReward = streakReward + voteBonus; // Get daily reward

            // Maximum amount of balance reached
            if (member.getBalance() + dailyReward > Config.ECONOMY_MAX) {
                member.setBalance(Config.ECONOMY_MAX); // Set members balance to the maximum
                daily.setDescription("You reached the limit! Now you have `"  + Config.ECONOMY_MAX + "` " + currency + "\n"); // Show streak reward
                // User voted
                if (voteBonus != 0) {
                    daily.appendDescription("Thank you for voting!"); // Show vote bonus
                }
            }
            // Maximum isn't reached yet
            else {
                member.setBalance(member.getBalance() + streakReward + voteBonus); // Update members balance
                daily.setDescription("**+" + streakReward + "** " + currency + "! Now you have `" + Utilities.getUtils().formatNumber(member.getBalance()) + "` " + currency + "\n"); // Show streak reward
                // User voted
                if (voteBonus != 0) {
                    daily.appendDescription("Thank you for voting! Your vote bonus: **+" + voteBonus + "**"); // Show vote bonus
                }
            }
            member.updateClaimedReward(); // Update last claimed reward time
            daily.setFooter("streak: " + member.getDailyStreak() + "/14"); // Show streak

            ctx.getChannel().sendMessage(daily.build()).queue(); // Send daily reward
        }
    }
}
