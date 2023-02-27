package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PollCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "createpoll")){
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        String question = event.getOption("question").getAsString();
        List<String> options = List.of(Objects.requireNonNull(event.getOption("options")).getAsString().split(","));
        String id = UUID.randomUUID().toString();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(question);
        embedBuilder.setColor(Color.cyan);
        embedBuilder.addField(languageConfiguration.pollparticipantscount(), "0", false);
        for (String option : options) {
            embedBuilder.addField(option, "[....................................................................................................] 0%", false);
        }
        embedBuilder.setFooter(id);

        List<Button> optionButtons = new ArrayList<>();
        for (String option : options) {
            optionButtons.add(Button.primary("poll:vote:" + option, option));
        }

        optionButtons.add(Button.danger("poll:close", languageConfiguration.pollclose()));
        optionButtons.add(Button.danger("poll:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1")));

        bot.getGuildsProvider().addPoll(event.getGuild().getId(), new BotPoll(id, event.getGuild().getId(), question, options, event.getMember().getId()));

        if(optionButtons.size() <= 5) {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(optionButtons).queue();
        }else {
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(optionButtons.subList(0, 5)).addActionRow(optionButtons.subList(5, optionButtons.size())).queue();
        }


        event.reply(languageConfiguration.pollcreated()).setEphemeral(true).queue();
    }
}
