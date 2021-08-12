package com.github.m5rian.myra.commands.member.general;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.myra.database.MongoUser;
import com.github.m5rian.myra.utilities.Utilities;
import com.github.myra.commons.Social;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.net.MalformedURLException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.m5rian.myra.utilities.language.Lang.lang;

public class About implements CommandHandler {
    @CommandEvent(
            name = "about",
            emoji = "\uD83D\uDCD6",
            description = "description.general.about"
    )
    public void onAbout(CommandContext ctx) {
        if (ctx.getArguments().length != 0) return;
        usage(ctx)
                .addUsages(About.class)
                .allowCommands("onAboutSocials")
                .send();
    }

    @CommandEvent(
            name = "about socials",
            emoji = "\uD83C\uDF0D",
            description = "description.general.about.socials"
    )
    public void onAboutSocials(CommandContext ctx) throws UnexpectedException {
        final MongoUser mongoUser = MongoUser.get(ctx.getAuthor());
        final List<Social> socials = mongoUser.getSocials(); // Get registered social media accounts of user

        // Create list for options
        final List<SelectOption> options = new ArrayList<>() {{
            for (Social social : Social.values()) {
                final SelectOption option = SelectOption.of(social.getName(), social.getName()); // Create option

                // User has an account of this social media registered
                if (socials.stream().anyMatch(s -> s.getName().equalsIgnoreCase(social.getName()))) {
                    final Optional<Social> registeredSocial = socials.stream().filter(s -> s.getName().equalsIgnoreCase(social.getName())).findFirst();
                    if (registeredSocial.isEmpty()) throw new UnexpectedException("Unexpected social media entry");
                    option.withDescription(registeredSocial.get().getValue()); // Add current username as description
                }

                add(option); // Add option to list
            }
        }};

        final SelectionMenu.Builder selection = SelectionMenu.create(ctx.getMessage() + "_about_socials")
                .setMaxValues(1)
                .addOptions(options);

        info(ctx).setDescription(lang(ctx).get("command.general.about.socials.info.socialsOption")).addComponents(selection.build()).send(message -> {
            Utilities.TIMER.schedule(() -> {
                final SelectionMenu selectionMenu = (SelectionMenu) message.getActionRows().get(0);
                if (!selectionMenu.isDisabled()) {
                    message.editMessageComponents(ActionRow.of(selection.setDisabled(true).build())).queue(); // Disable selection
                    error(ctx).setDescription(lang(ctx).get("error.timeout")).send(); // Send timeout message
                }

            }, 30, TimeUnit.SECONDS);
        });
        onSelectionMenuEvent(selection.getId(), event -> {
            final SelectionMenu disabledSelection = selection.setDefaultOptions(event.getSelectedOptions()).setDisabled(true).build();
            event.editSelectionMenu(disabledSelection).queue(); // Disable selection
            finishSelectionMenu(selection.getId());

            // Get selected social media platform
            final String socialMedia = event.getSelectedOptions().get(0).getValue(); // Get social media name to edit
            final Social social = Social.getByName(socialMedia); // Create Social object

            event.getHook().sendMessage(lang(ctx).get("command.general.about.socials.info.inputRequest")
                    .replace("{$social.name}", social.getName())
                    .replace("{$social.loginOption}", String.format("%s %s %s",
                            String.join(" " + lang(ctx).get("word.or") + " ", Arrays.stream(social.getUrlData()).map(data -> data.name().toLowerCase()).toList()),
                            lang(ctx).get("word.or"), lang(ctx).get("word.url")))
            ).queue(message -> {
                ctx.getWaiter().waitForEvent(MessageReceivedEvent.class)
                        .setCondition(e -> !e.getAuthor().isBot()
                                && e.getAuthor().getIdLong() == ctx.getAuthor().getIdLong()
                                && e.getChannel().getIdLong() == ctx.getChannel().getIdLong())
                        .setAction(e -> {
                            try {
                                final String value = social.getValue(e.getMessage().getContentRaw()); // Get user value of social media platform
                                social.setValue(value);
                                MongoUser.get(e.getAuthor()).setSocial(social); // Edit social

                                final EmbedBuilder info = info(ctx).setDescription(lang(ctx).get("command.general.about.socials.info.success")
                                                .replace("{$social.name}", social.getName())
                                                .replace("{$user.social}", String.format(social.getUrl(), social.getValue())))
                                        .getEmbed();
                                e.getMessage().replyEmbeds(info.build()).queue(); // Send success message
                            } catch (MalformedURLException ex) {
                                error(ctx).setDescription(lang(ctx).get("command.general.about.socials.error.invalidUrl")
                                                .replace("{$social.name}", social.getName()))
                                        .send();
                            }
                        })
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setTimeoutAction(() -> error(ctx).setDescription(lang(ctx).get("error.timeout")).send())
                        .load();
            });

        });
    }

}
