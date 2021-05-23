package com.myra.dev.marian.commands.economy;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandEvent;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.myra.dev.marian.Config;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.utilities.APIs.DiscordBoats;
import com.myra.dev.marian.utilities.APIs.TopGG;
import com.myra.dev.marian.utilities.Format;
import static com.myra.dev.marian.utilities.language.Lang.*;
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
        final String currency = new MongoGuild(ctx.getGuild()).getNested("economy").getString("currency"); // Get currency

        // Create embed
        EmbedBuilder daily = new EmbedBuilder()
                .setAuthor("daily", null, ctx.getAuthor().getEffectiveAvatarUrl())
                .setColor(ctx.getMember().getColor());

        // 12 didn't pass
        if (TimeUnit.MILLISECONDS.toHours(passedTime) < 12) {
            final long nextBonusAt = lastClaim + TimeUnit.HOURS.toMillis(12); // Get duration until you can claim your reward
            String nextBonusIn = Format.toTime(nextBonusAt - System.currentTimeMillis()); // Make time look nicer

            daily.setDescription(lang(ctx).get("command.economy.daily.wait") // Set description
                    .replace("{$time}", nextBonusIn));
            ctx.getChannel().sendMessage(daily.build()).queue(); // Send message
            return;
        }

        // Claim reward
        if (TimeUnit.MILLISECONDS.toHours(passedTime) >= 12) {
            int voteBonus = 0; // Create vote bonus
            // User voted on top.gg
            if (TopGG.getInstance().hasVoted(ctx.getAuthor())) voteBonus += 100;
            // User voted on discord.boats
            if (DiscordBoats.getInstance().hasVoted(ctx.getAuthor())) voteBonus += 100;

            // Missed reward
            if (TimeUnit.MILLISECONDS.toHours(passedTime) > 36) member.setDailyStreak(1); // Reset daily streak
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
                daily.setDescription(lang(ctx).get("command.economy.daily.balanceLimit") // Show streak reward
                        .replace("{$maxBalance}", String.valueOf(Config.ECONOMY_MAX)));
                // User voted
                if (voteBonus != 0) {
                    daily.appendDescription("\n" + lang(ctx).get("command.economy.daily.voteThank")); // Show vote bonus
                }
            }
            // Maximum isn't reached yet
            else {
                member.setBalance(member.getBalance() + streakReward + voteBonus); // Update members balance
                daily.setDescription(lang(ctx).get("command.economy.daily.success")
                        .replace("{$streakReward}", String.valueOf(streakReward)) // Show streak reward
                        .replace("{$balance}", Format.number(member.getBalance())) // Show current balance
                        .replace("{$currency}", currency)); // Guild currency

                // User voted
                if (voteBonus != 0) {
                    daily.appendDescription(lang(ctx).get("command.economy.daily.voteBonus")
                            .replace("{$bonus}", Format.number(voteBonus))); // Vote bonus
                }
            }

            member.updateClaimedReward(); // Update last claimed reward time
            daily.setFooter("streak: " + member.getDailyStreak() + "/14"); // Show streak
            ctx.getChannel().sendMessage(daily.build()).queue(); // Send daily reward
        }
    }
}
