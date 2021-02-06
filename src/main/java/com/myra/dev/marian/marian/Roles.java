package com.myra.dev.marian.marian;

import com.myra.dev.marian.utilities.Config;
import com.myra.dev.marian.utilities.Utilities;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Roles {

    public void jdaReady(ReadyEvent event) {
        // Get my server
        final Guild guild = event.getJDA().getGuildById(Config.marianServer);

        unicorn(guild);
        //categories(guild);
    }

    /**
     * Change every 15 minutes the colour of the unicorn role.
     *
     * @param marianServer My discord server.
     */
    private void unicorn(Guild marianServer) {
        // Get role role
        final Role role = marianServer.getRoleById("774210055259947008");
        Utilities.TIMER.scheduleAtFixedRate(() -> {
            // Get high saturated colour
            Random random = new Random();
            final float hue = random.nextFloat();
            final float saturation = 0.5f; //1.0 for brilliant, 0.0 for dull
            final float brightness = 1.0f; //1.0 for brighter, 0.0 for black
            Color colour = Color.getHSBColor(hue, saturation, brightness);
            // Update colour
            role.getManager().setColor(colour).queue();
        }, 60, 60, TimeUnit.MINUTES);
    }

    /**
     * Give all members, who use Myra in their server the 'exclusive' role on my discord server.
     *
     * @param event The GuildMemberJoinEvent event.
     */
    public void exclusive(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals(Config.marianServer)) return; // Only run in my server
        // Get exclusive role
        final Role exclusiveRole = event.getJDA().getGuildById(Config.marianServer).getRoleById("775646920646983690");

        List<User> guildOwners = new ArrayList<>();
        for (Guild guild : event.getJDA().getGuilds()) {
            guildOwners.add(guild.getOwner().getUser());
        }
        Iterator<Member> iterator = event.getGuild().getMembers().iterator();
        while (iterator.hasNext()) {
            final Member member = iterator.next();
            // Member is owner of a server
            if (guildOwners.contains(member.getUser())) {
                if (member.getRoles().contains(exclusiveRole)) continue; // Member already owns the exclusive role
                event.getGuild().addRoleToMember(member, exclusiveRole).queue(); // Add exclusive role to member
            }
            // Member isn't owner of a server
            else {
                // Member has the exclusive role
                if (member.getRoles().contains(exclusiveRole)) {
                    event.getGuild().removeRoleFromMember(member, exclusiveRole).queue(); // Remove exclusive role from member
                }
            }
        }
    }

/*    private final List<String> special = Arrays.asList(
            "715545225057140736", // Team
            "732260877134331936", // First booster
            "726813321797566484", // Server booster
            "785170745797246987", // Premium
            "784880312000970782", // Debugger
            "775352717526171678", // Bug hunter
            "714787219784597544", // Businessman
            "769221560543477800", // Addict
            "717078181408145459", // True gambler
            "775647035315322891", // Designer
            "775646920646983690", // Exclusive
            "789415020378980383" // Explorer
    );

    public void categories(Guild guild) {
        guild.getMembers().forEach(member -> {
            final List<Role> roles = member.getRoles(); // Get roles of member

            if (roles.stream().anyMatch(role -> special.stream().anyMatch(special -> role.getId().equals(special)))) {
                guild.addRoleToMember(member, guild.getRoleById("713701785100877874")).queue();
            }
        });
    }

    public void categories(GuildMemberRoleAddEvent event) {
        if (!event.getGuild().getId().equals(Config.marianServer)) return;

        final Member member = event.getMember(); // Get member
        final List<Role> roles = member.getRoles(); // Get roles of member

        if (roles.stream().anyMatch(role -> special.stream().anyMatch(special -> role.getId().equals(special)))) {
            event.getGuild().addRoleToMember(member, event.getGuild().getRoleById("713701785100877874")).queue();
        }
    }*/
}
