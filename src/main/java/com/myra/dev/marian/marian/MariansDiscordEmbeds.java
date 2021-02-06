package com.myra.dev.marian.marian;

import com.github.m5rian.jdaCommandHandler.Command;
import com.github.m5rian.jdaCommandHandler.CommandContext;
import com.github.m5rian.jdaCommandHandler.CommandSubscribe;import com.myra.dev.marian.utilities.permissions.Administrator;
import com.myra.dev.marian.utilities.Utilities;
import com.myra.dev.marian.utilities.permissions.Marian;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

@CommandSubscribe(
        name = "update embeds",
        requires = Marian.class
)
public class MariansDiscordEmbeds implements Command {

    @Override
    public void execute(CommandContext ctx) throws Exception {
        final TextChannel information = ctx.getGuild().getTextChannelById("717655383451107339");

        // Welcome
        MessageEmbed welcome = new EmbedBuilder()
                .setTitle("Welcome to " + ctx.getGuild().getName())
                .setThumbnail(ctx.getGuild().getIconUrl())
                .setColor(0x00FFFF)
                .addField("", ctx.getGuild().getName() + " is a server dedicated to have fun and write with other people", false)
                .addField("", "Underneath you can find all information you will need to get you started in our server! If you still have questions after reading this, just contact one of our staff members! Also check out <#696420618995761192> in case something changes.", false)
                .addField("", "**\uD83D\uDD17 invite link : https://discord.gg/nG4uKuB**", false)
                .build();
        information.editMessageById("726130459079213138", welcome).queue();

        // Buyable roles
        information.editMessageById("726130523998781513",
                new EmbedBuilder()
                        .setTitle("buyable roles")
                        .setColor(0xFFD800)
                        .setDescription("You can buy these roles by typing in commands `..buy`")
                        .addField("15 000", "<@&715462771688603678>  <@&715508379807645697>", false)
                        .addField("25 000", "<@&715462773512863804> <@&715498605162791042> <@&715500070610534480>", false)
                        .addField("10 000", "<@&715462773525708832> <@&715462772615413811> <@&715462649390956567>", false)
                        .addField("5 000", "<@&715498892900302929>", false)
                        .addField("25 000", "<@&715462771688603678>  <@&715508379807645697>", false)
                        .addField("10 000", "<@&714787219784597544> ➪ BE ABLE TO ADVERTISE IN  <#668403563223056384>", false)
                        .addField("100 000", "<@&774210055259947008> ➪ UR COLOUR WILL CHANGE EVERY HOUR", false)
                        .build()
        ).queue();

        // Leveling roles
        information.editMessageById("726130554721927168",
                new EmbedBuilder()
                        .setAuthor("leveling roles", null, ctx.getGuild().getIconUrl())
                        .setColor(0xFF006E)
                        .setDescription("You can get these roles if you reach a specific level.")
                        .addField("", "level 5 ➪ <@&688477543594197044>", true)
                        .addField("", "level 15 ➪ <@&688477562313244763>", true)
                        .addField("", "level 25 ➪ <@&688480420685545527>", true)
                        .addField("", "level 50 ➪ <@&688480479371985031>", true)
                        .addField("", "level 75 ➪ <@&688480479371985031>", true)
                        .addField("", "level 100 ➪ <@&689878622361747494>", true)
                        .build()
        ).queue();

        // Designer roles
        information.editMessageById("726130588184084490",
                new EmbedBuilder()
                        .setTitle("designer roles")
                        .setColor(0x00FF90)
                        .setDescription("These roles you can get by reaching **desinger rank** of a specific kind")
                        .addField("IF YOU REACHED THE **DESIGNER** RANK WITH MINECRAFT DESINGS", "<@&698884619130634292>", false)
                        .addField("IF YOU REACHED THE **DESIGNER** RANK WITH INTROS", "<@&711972287330648155>", false)
                        .addField("IF YOU REACHED THE **DESIGNER** RANK WITH ANIME DESIGNS", "<@&711972209580572732>", false)
                        .addField("WHEN YOU REACHED THE **DESIGNER** RANK WITH TEXTURE PACKS", "<@&698884621110083717>", false)
                        .addField("", "<@&698884600654594108> \n <@&698884548783636500> \n <@&698884462460534835> \n <@&698884227059417128> \n <@&698883418070450177> \n <@&698883364773429258> \n <@&698883381966143568>", false)
                        .build()
        ).queue();

        // Social media
        information.editMessageById("726130619653816381",
                new EmbedBuilder()
                        .setTitle("my social Media")
                        .setColor(0x0094FF)
                        .setDescription(Utilities.getUtils().hyperlink("YouTube", "https://www.youtube.com/channel/UCw4EmB5OUHFN5RplLLon_Xw") + "\n" +
                                Utilities.getUtils().hyperlink("Twitter", "https://twitter.com/MarianGFX") + "\n" +
                                Utilities.getUtils().hyperlink("Instagram", "https://www.instagram.com/mar._.ian/") + "\n" +
                                Utilities.getUtils().hyperlink("SoundCloud", "https://soundcloud.com/user-533142830") + "\n" +
                                Utilities.getUtils().hyperlink("Twitch", "https://www.twitch.tv/m5rian") + "\n" +
                                Utilities.getUtils().hyperlink("Discord", "https://discord.gg/nG4uKuB")
                        )
                        .build()
        ).queue();

        // Bot related channels
        information.editMessageById("726130651593441400",
                new EmbedBuilder()
                        .setTitle("bot related channels")
                        .setColor(0xE9E637)
                        .setDescription("React with [\uD83D\uDD0C] to see the bot related channels. You will see updates, bugs and planned features for the bot!")
                        .build()
        ).queue();

        // Texture pack related channels
        information.editMessageById("726130652365324360",
                new EmbedBuilder()
                        .setTitle("texture pack related channels")
                        .setColor(0x40DAAB)
                        .setDescription("React with [\u270F\uFE0F] to see the texture pack related channels. You will see sneak peaks and my releases!")
                        .build()
        ).queue();
    }
}
