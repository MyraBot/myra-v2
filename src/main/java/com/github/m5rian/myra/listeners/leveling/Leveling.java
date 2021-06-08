package com.github.m5rian.myra.listeners.leveling;

import com.github.m5rian.myra.database.guild.*;
import com.github.m5rian.myra.database.guild.member.GuildMember;
import com.github.m5rian.myra.utilities.Graphic;
import com.github.m5rian.myra.utilities.language.Lang;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;

public class Leveling {

    public void levelUp(Member member, MessageChannel channel, GuildMember db, int xp) {
        try {
            final int newLevel = getLevelFromXp(db.getXp() + xp); // Get new level
            if (db.getLevel() == newLevel) return; // Current level is equal to new one

            // Level up
            db.setLevel(newLevel); // Update level in database
            final Guild guild = member.getGuild(); // Get guild
            // Level up message
            final String levelingChannel = new MongoGuild(guild).getNested("leveling").getString("channel"); // Get leveling channel
            final BufferedImage levelUpImage = getLevelUpImage(member, newLevel); // Get level up image

            // There is a custom level-up channel
            if (!levelingChannel.equals("not set")) {
                final TextChannel textChannel = guild.getTextChannelById(levelingChannel); // Get leveling channel
                // Custom leveling channel is invalid
                if (textChannel == null) {
                    new MongoGuild(guild).getNested("leveling").setString("channel", "not set"); // Remove leveling channel
                    return;
                }
                // Missing permissions
                if (!guild.getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) return;

                channel.sendMessage(Lang.lang(member.getGuild()).get("listener.leveling.levelUp")
                        .replace("{$member.mention}", member.getAsMention())) // Member who leveled up
                        .addFile(Graphic.toInputStream(levelUpImage), member.getUser().getName().toLowerCase() + "_level_up.png")
                        .queue();
            }
            // Send in current channel
            else if (channel != null) { // If channel is not null
                channel.sendMessage(Lang.lang(member.getGuild()).get("listener.leveling.levelUp")
                        .replace("{$member.mention}", member.getAsMention())) // Member who leveled up
                        .addFile(Graphic.toInputStream(levelUpImage), member.getUser().getName().toLowerCase() + "_level_up.png")
                        .queue();
            }

            updateLevelingRoles(guild, member, db); // Update leveling roles
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getLevelUpImage(Member member, int level) throws IOException, FontFormatException {
        final BufferedImage background = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("levelUp.png")); // Get level up image background
        BufferedImage avatar = Graphic.getAvatar(member.getUser().getEffectiveAvatarUrl()); // Get avatar as a BufferedImage

        avatar = Graphic.resizeImage(avatar, 85, 85); // Resize avatar
        // Graphics
        Graphics graphics = background.getGraphics(); // Create graphics object from background
        Graphics2D graphics2D = (Graphics2D) graphics; // Create graphics2D object from background

        Graphic.enableAntiAliasing(graphics2D); //Enable anti aliasing
        // Load font
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("default.ttf"); // Get font as InputStream
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream); // Create font
        font = font.deriveFont(45f); // Set font size
        graphics.setFont(font); // Set font

        // Draw avatar
        graphics2D.drawImage(
                avatar,
                Graphic.imageCenter('X', avatar, background) - 200,
                Graphic.imageCenter('Y', avatar, background),
                null);

        // Draw circle around avatar
        graphics2D.setColor(Color.white); // Set circle colour
        graphics2D.setStroke(new BasicStroke(
                2.5f, // Set stroke width
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                Graphic.imageCenter('X', avatar, background) - 200,
                Graphic.imageCenter('Y', avatar, background),
                avatar.getWidth(), avatar.getHeight()
        );

        // Draw 'level'
        graphics.drawString("level " + level,
                Graphic.textCenter('X', "level " + level, font, background) - 55,
                Graphic.textCenter('Y', "level " + level, font, background) + 40
        );

        return background;
    }

    public static void updateLevelingRoles(Guild guild, Member member, GuildMember dbMember) {
        final Nested guildLeveling = new MongoGuild(guild).getNested("leveling"); // Get leveling document
        final Document levelingRolesDocument = guildLeveling.get("roles", Document.class); // Get leveling roles

        // Create list of leveling roles documents
        List<LevelingRole> levelingRoles = new ArrayList<>();
        levelingRolesDocument.keySet().forEach(key -> {
            Document levelingRole = levelingRolesDocument.get(key, Document.class); // Get current value as document
            LevelingRole rolesDocument = new LevelingRole(levelingRole); // Create new leveling roles document
            levelingRoles.add(rolesDocument); // Add document
        });
        Collections.sort(levelingRoles, Comparator.comparing(LevelingRole::getLevel).reversed()); // Sort list by level

        final Boolean unique = guildLeveling.getBoolean("uniqueRoles"); // Should a member have only one leveling role at the same time?
        final int level = dbMember.getLevel(); // Get members level

        // Members should have only 1 leveling role at the same time
        if (unique) {
            // Get highest leveling role this member can have
            Optional<LevelingRole> newRoleDocument = levelingRoles
                    .stream()
                    .filter(r -> level >= r.getLevel()) // Member level is higher or equal to required role
                    .findFirst();
            // A role which has a lower required rank than the member exists
            if (newRoleDocument.isPresent()) {
                final Role newRole = guild.getRoleById(newRoleDocument.get().getRole()); // Get leveling role as role
                // Role is invalid
                if (newRole == null) {
                    final Document newLevelingRoles = guildLeveling.get("roles", Document.class); // Get current leveling roles
                    newLevelingRoles.remove(newRoleDocument.get().getRole()); // Remove leveling role from list
                    guildLeveling.set("roles", newLevelingRoles); // Update database
                }
                // Role is valid
                else guild.addRoleToMember(member, newRole).queue(); // Add role to member
            }

            // For each role
            for (LevelingRole levelingRole : levelingRoles) {
                // Current leveling role is the same as the one the user just got
                if (newRoleDocument.isPresent() && levelingRole.getRole().equals(newRoleDocument.get().getRole()))
                    continue;

                final Role r = guild.getRoleById(levelingRole.getRole()); // Get role
                // Role is invalid
                if (r == null) {
                    final Document newLevelingRoles = guildLeveling.get("roles", Document.class); // Get current leveling roles
                    newLevelingRoles.remove(levelingRole.getRole()); // Remove leveling role from list
                    guildLeveling.set("roles", newLevelingRoles); // Update database
                }
                // Role is valid
                else guild.removeRoleFromMember(member, r).queue(); // Remove role from member
            }
        }

        // Member can have unlimited leveling roles
        else {
            levelingRoles.forEach(levelingRole -> {
                final Role role = guild.getRoleById(levelingRole.getRole()); // Get leveling role as role
                // Member has higher or equal required level
                if (level >= levelingRole.getLevel()) guild.addRoleToMember(member, role).queue(); // ADd role to member
                    // Members level doesn't meet the required level
                else guild.removeRoleFromMember(member, role).queue(); // Remove role from member
            });
        }

    }

    /**
     * @param xp The experience, which should get converted to a level.
     * @return Returns the level calculated by the experience.
     */
    public static int getLevelFromXp(long xp) {
        // Parabola
        long dividedNumber = xp / 5;
        double exactLevel = Math.sqrt(dividedNumber);
        return (int) exactLevel;
    }

    /**
     * @param level The level, which should get converted to experience points.
     * @return Returns the experience needed to reach the given level.
     */
    public static long getXpFromLevel(int level) {
        // Parabola
        double squaredNumber = Math.pow(level, 2);
        double exactXp = squaredNumber * 5;
        return (int) exactXp;
    }

    //return missing xp
    public static Integer requiredXpForNextLevel(Guild guild, Member member) {
        int currentLevel = new MongoGuild(guild).getMembers().getMember(member).getLevel();
        //define variable
        double xp;
        //parabola
        double squaredNumber = Math.pow(currentLevel + 1, 2);
        double exactXp = squaredNumber * 5;
        return (int) exactXp;
        /*
        //round off
        DecimalFormat f = new DecimalFormat("###");
        xp = Double.parseDouble(f.format(exactXp));
        //round down number
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        //convert to int and remove the '.0'
        System.out.println(xp);
        return Integer.parseInt(String.valueOf(xp).replace(".0", ""));
        */
    }
}
