package com.github.m5rian.myra.utilities;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommandCooldown {
    // Create Instance
    private final static CommandCooldown COMMAND_COOLDOWN = new CommandCooldown();

    // Return Instance
    public static CommandCooldown getInstance() {
        return COMMAND_COOLDOWN;
    }


    //                     Guild          Member          Command Time
    private final static HashMap<Guild, HashMap<Member, HashMap<String, Long>>> cooldown = new HashMap<>();

    public boolean addCommand(CommandContext ctx, String command, Integer durationInSeconds) {
        // Get variables
        final Guild guild = ctx.getGuild();
        final Member member = ctx.getMember();
        // If guild is already in the 'cooldown' HashMap
        if (cooldown.get(guild) != null) {
            // If member is already in the 'cooldown' HashMap
            if (cooldown.get(guild).get(member) != null) {
                // If command is already in the 'cooldown' HashMap
                if (cooldown.get(guild).get(member) != null) {
                    // Get duration in milliseconds
                    final int durationInMillis = durationInSeconds * 1000;
                    // Last execution
                    final long lastExecution = cooldown.get(guild).get(member).get(command);
                    // If duration didn't pass
                    if (cooldown.get(guild).get(member).get(command) + durationInMillis >= System.currentTimeMillis()) {
                        // Get time until you can use the command
                        final long wait = TimeUnit.MILLISECONDS.toSeconds((lastExecution + durationInMillis) - System.currentTimeMillis());
                        // Send cooldown message
                        ctx.getChannel().sendMessage("> \u23F3 **Cooldown** " + wait + " seconds").queue(message -> { // ⏳
                            message.delete().queueAfter((lastExecution + durationInMillis) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                        });
                        return false;
                    } else {
                        cooldown.get(guild).get(member).replace(command, System.currentTimeMillis());
                    }
                }
                // Add command to 'cooldown' HashMap
                else {
                    // Put command to member in 'cooldown' HashMap
                    cooldown.get(guild).get(member).put(command, System.currentTimeMillis());
                }
            }
            // Add member to 'cooldown' HashMap
            else {
                // Create HashMap for command
                HashMap<String, Long> commandMap = new HashMap<>();
                // Add command and execution time
                commandMap.put(command, System.currentTimeMillis());

                // Put member to guild in 'cooldown' HashMap
                cooldown.get(guild).put(member, commandMap);
            }
        }
        // Add guild to 'cooldown' HashMap
        else {
            // Create HashMap for command
            HashMap<String, Long> commandMap = new HashMap<>();
            // Add command and execution time
            commandMap.put(command, System.currentTimeMillis());

            // Create HashMap for member
            HashMap<Member, HashMap<String, Long>> memberMap = new HashMap<>();
            // Add member and 'commandMap' HashMap
            memberMap.put(member, commandMap);

            // Add guild and 'memberMap' HashMap to 'cooldown' HashMap
            cooldown.put(guild, memberMap);
        }
        return true;
    }
}
