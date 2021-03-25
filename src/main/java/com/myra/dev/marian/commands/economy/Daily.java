package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.myra.dev.marian.database.allMethods.Database;
import com.myra.dev.marian.database.allMethods.GetMember;
import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.APIs.DiscordBoats;
import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Format;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.TimeUnit;

@CommandSubscribe(
        name = "daily",
        channel = Channel.GUILD
)
public class Daily implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return; // Check for no arguments

        final GetMember member = new Database(ctx.getGuild()).getMembers().getMember(ctx.getEvent().getMember()); // Get member from database
        long lastClaim = member.getLastClaim(); // Get last claimed reward


        long passedTime = System.currentTimeMillis() - lastClaim; // Get duration, which passed (in milliseconds)
        final String currency = new Database(ctx.getGuild()).getNested("economy").getString("currency").toString(); // Get currency

        // Create embed
        EmbedBuilder daily = new EmbedBuilder()
                .setAuthor("daily", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(Utilities.getUtils().getMemberRoleColour(ctx.getEvent().getMember()));

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
            else member.setDailyStreak(member.getInteger("dailyStreak") + 1); // Update daily streak

            // Get streak bonus
            int streakReward;
            if (member.getInteger("dailyStreak") > 14) streakReward = 14 * 100; // You can't get a higher streak than 14
            else streakReward = member.getInteger("dailyStreak") * 100; // Get streak reward

            final int dailyReward = streakReward + voteBonus; // Get daily reward

            // Maximum amount of balance reached
            if (member.getInteger("balance") + dailyReward > Config.ECONOMY_MAX) {
                member.setInteger("balance", Config.ECONOMY_MAX); // Set members balance to the maximum
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
            daily.setFooter("streak: " + member.getInteger("dailyStreak") + "/14"); // Show streak

            ctx.getChannel().sendMessage(daily.build()).queue(); // Send daily reward
        }
    }
}
