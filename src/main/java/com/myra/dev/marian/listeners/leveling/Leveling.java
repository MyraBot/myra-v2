package com.myra.dev.marian.listeners.leveling;

import com.myra.dev.marian.database.guild.LevelingRole;
import com.myra.dev.marian.database.guild.MongoGuild;
import com.myra.dev.marian.database.guild.member.GuildMember;
import com.myra.dev.marian.utilities.EmbedMessage.Error;
import com.myra.dev.marian.utilities.Graphic;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leveling {

    public void levelUp(Member member, MessageChannel channel, GuildMember db, int xp) {
        try {
            final int newLevel = level(db.getXp() + xp); // Get new level
            if (db.getLevel() == newLevel) return; // Current level is equal to new one

            // Level up
            db.setLevel(newLevel); // Update level in database
            final Guild guild = member.getGuild(); // Get guild
            final Graphic graphic = Graphic.getInstance(); // Get graphics
            // Level up message
            final String levelingChannel = new MongoGuild(guild).getNested("leveling").getString("channel"); // Get leveling channel

            if (!levelingChannel.equals("not set")) { // Custom level up channel
                final BufferedImage levelUpImage = getLevelUpImage(member, newLevel); // Get level up image

                if (guild.getTextChannelById(levelingChannel) == null) { // Channel is invalid
                    if (channel != null) {
                        new Error(null)
                                .setCommand("rank up")
                                .setEmoji("\uD83C\uDF96")
                                .setAvatar(guild.getIconUrl())
                                .setMessage("The leveling channel is invalid")
                                .send();
                    }
                }
                guild.getTextChannelById(levelingChannel).sendMessage("> **" + member.getUser().getAsMention() + " reached a new level!**")
                        .addFile(graphic.toInputStream(levelUpImage), member.getUser().getName().toLowerCase() + "_level_up.png")
                        .queue();
            }
            else if (channel != null) {
                final BufferedImage levelUpImage = getLevelUpImage(member, newLevel); // Get level up image
                channel
                        .sendMessage("> **" + member.getUser().getAsMention() + " reached a new level!**")
                        .addFile(graphic.toInputStream(levelUpImage), member.getUser().getName().toLowerCase() + "_level_up.png")
                        .queue();
            }
            // Leveling role
            updateLevelingRoles(guild, member, db); // Check for leveling roles
        } catch (FontFormatException | IOException e){
            e.printStackTrace();
        }
    }

    private BufferedImage getLevelUpImage(Member member, int level) throws IOException, FontFormatException {
        Graphic graphic = Graphic.getInstance(); // Get graphics
        final BufferedImage background = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("levelUp.png")); // Get level up image background
        BufferedImage avatar = graphic.getAvatar(member.getUser().getEffectiveAvatarUrl()); // Get avatar as a BufferedImage

        avatar = graphic.resizeImage(avatar, 85, 85); // Resize avatar
        // Graphics
        Graphics graphics = background.getGraphics(); // Create graphics object from background
        Graphics2D graphics2D = (Graphics2D) graphics; // Create graphics2D object from background

        graphic.enableAntiAliasing(graphics); //Enable anti aliasing
        // Load font
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("default.ttf"); // Get font as InputStream
        Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream); // Create font
        font = font.deriveFont(45f); // Set font size
        graphics.setFont(font); // Set font

        // Draw avatar
        graphics2D.drawImage(
                avatar,
                graphic.imageCenter('X', avatar, background) - 200,
                graphic.imageCenter('Y', avatar, background),
                null);

        // Draw circle around avatar
        graphics2D.setColor(Color.white); // Set circle colour
        graphics2D.setStroke(new BasicStroke(
                2.5f, // Set stroke width
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));
        graphics2D.drawOval(
                graphic.imageCenter('X', avatar, background) - 200,
                graphic.imageCenter('Y', avatar, background),
                avatar.getWidth(), avatar.getHeight()
        );

        // Draw 'level'
        graphics.drawString("level " + level,
                graphic.textCenter('X', "level " + level, font, background) - 55,
                graphic.textCenter('Y', "level " + level, font, background) + 40
        );

        return background;
    }

    public void updateLevelingRoles(Guild guild, Member member, GuildMember dbMember) {
        final Document levelingRolesDocument = new MongoGuild(guild).getNested("leveling").get("roles", Document.class); // Get leveling roles

        // Create list of leveling roles documents
        List<LevelingRole> levelingRoles = new ArrayList<>();
        levelingRolesDocument.keySet().forEach(key -> {
            Document levelingRole = levelingRolesDocument.get(key, Document.class); // Get current value as document
            LevelingRole rolesDocument = new LevelingRole(levelingRole); // Create new leveling roles document
            levelingRoles.add(rolesDocument); // Add document
        });
        Collections.sort(levelingRoles, Comparator.comparing(LevelingRole::getLevel).reversed()); // Sort list by level

        final Boolean unique = new MongoGuild(guild).getNested("leveling").getBoolean("uniqueRoles"); // Should a member have only one leveling role at the same time?
        final int level = dbMember.getLevel(); // Get members level

        // Members should have only 1 leveling role at the same time
        if (unique) {
            // Get highest leveling role this member can have
            final LevelingRole highestLevelingRole = levelingRoles
                    .stream()
                    .filter(r -> level >= r.getLevel()) // Member level is higher or equal to required role
                    .findFirst()
                    .get();
            final Role role = guild.getRoleById(highestLevelingRole.getRole()); // Get leveling role as role


            guild.addRoleToMember(member, role).queue(); // Add role to member
            // For each role
            levelingRoles.forEach(lvlRole -> {
                if (lvlRole != highestLevelingRole) {
                    final Role r = guild.getRoleByBot(lvlRole.getRole()); // Get role
                    // TODO Remove invalid roles
                    if (r != null) {
                        guild.removeRoleFromMember(member, r).queue(); // Remove role from member
                    }
                }
            });
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

    //return level
    public int level(int xp) {
        //parabola
        double dividedNumber = xp / 5;
        double exactLevel = Math.sqrt(dividedNumber);
        //round
        return (int) Math.round(exactLevel);
    }

    public Integer xpFromLevel(int level) {
        //parabola
        double squaredNumber = Math.pow(level, 2);
        double exactXp = squaredNumber * 5;
        //round
        return (int) Math.round(exactXp);
    }

    //return missing xp
    public int requiredXpForNextLevel(Guild guild, Member member) {
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
