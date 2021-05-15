package com.myra.dev.marian.marian;

import com.myra.dev.marian.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class Roles {
    /**
     * Give all members, who use Myra in their server the 'exclusive' role on my discord server.
     *
     * @param event The GuildMemberJoinEvent event.
     */
    public void exclusive(GuildMemberJoinEvent event) {
        final Guild server = event.getJDA().getGuildById(Config.MARIAN_SERVER_ID);
        if (event.getGuild() != server) return;
        // Get exclusive role
        final Role exclusiveRole = server.getRoleById("775646920646983690");

        List<String> guildOwners = new ArrayList<>();
        for (Guild guild : event.getJDA().getGuilds()) {
            guildOwners.add(guild.retrieveOwner().complete().getId());
        }

        server.loadMembers().onSuccess(members -> {
            for (Member member : members) {
                // Member is owner of a server
                if (guildOwners.contains(member.getId())) {
                    server.addRoleToMember(member, exclusiveRole).queue(); // Add exclusive role to member
                }
                // Member isn't owner of a server
                else {
                    server.removeRoleFromMember(member, exclusiveRole).queue(); // Remove exclusive role from member
                }
            }
        });
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
