package com.github.m5rian.myra.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.Config;
import com.github.m5rian.myra.database.guild.MongoGuild;
import com.github.m5rian.myra.utilities.language.Lang;
import com.github.m5rian.myra.utilities.permissions.Administrator;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class Language implements CommandHandler {

    @CommandEvent(
            name = "language",
            aliases = {"languages", "lang", "langs"},
            requires = Administrator.class,
            description = "Change the language for your server"
    )
    public void onLanguageCommand(CommandContext ctx) {
        if (ctx.getEvent().getJDA().getGuildById(Config.MARIAN_SERVER_ID).isMember(ctx.getAuthor()) && !ctx.getEvent().getJDA().getGuildById(Config.MARIAN_SERVER_ID).getMemberById(ctx.getMember().getIdLong()).getRoles().stream().anyMatch(role -> role.getId().equals(Config.MYRA_TRANSLATOR_ROLE)))
            return;

        final MongoGuild mongoGuild = MongoGuild.get(ctx.getGuild());
        final String currentLanguage = mongoGuild.getString("lang"); // Get current language's ISO-Code

        final List<SelectOption> options = new ArrayList<>(); // Create list for all language options
        Arrays.asList(Lang.Country.values()).forEach(lang -> options.add(SelectOption.of(lang.getNativeName(), lang.getIsoCode()).withEmoji(lang.hasCustomEmoji() ? Emoji.fromEmote(lang.getCustomEmoji().getEmote()) : Emoji.fromUnicode(lang.getUnicodeEmoji()))));

        final SelectionMenu.Builder menu = SelectionMenu.create("language_" + ctx.getMessage().getId())
                .addOptions(options)
                .setMaxValues(1)
                .setDefaultOptions(List.of(options.stream().filter(option -> option.getValue().equals(currentLanguage)).findFirst().get()));
        info(ctx).setDescription(lang(ctx).get("command.language.info.selection")).addComponents(menu.build()).send(); // Send message

        onSelectionMenuEvent(menu.getId(), event -> {
            final Lang.Country newLanguage = Lang.Country.getByIsoCode(event.getSelectedOptions().get(0).getValue());

            MongoGuild.get(ctx.getGuild()).setString("lang", newLanguage.getIsoCode()); // Update database

            event.editSelectionMenu(event.getSelectionMenu().createCopy()
                    .setDefaultOptions(List.of(event.getSelectedOptions().get(0))) // Set new lanuage as selected
                    .setDisabled(true) // Disable selection menu
                    .build()).queue();

            event.getMessage().editMessageEmbeds(
                    info(ctx).setDescription(lang(ctx).get("command.language.info.changed")
                            .replace("{$language.name}", newLanguage.getNativeName()))
                            .getEmbed()
                            .build())
                    .queue();

            finishSelectionMenu(menu.getId());
        });
    }
}
