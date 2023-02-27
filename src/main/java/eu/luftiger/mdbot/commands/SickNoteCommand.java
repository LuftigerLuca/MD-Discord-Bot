package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SickNoteCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "sicknote")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        String person = event.getOption("person").getAsString();
        String reason = event.getOption("reason").getAsString();
        String from = event.getOption("from").getAsString();

        String to = null;
        if(event.getOption("to") != null) to = event.getOption("to").getAsString();
        if(!from.matches("\\d{2}.\\w{2}.\\d{4}")) {
            event.reply(languageConfiguration.invaliddateformat()).setEphemeral(true).queue();
            return;
        }

        if(to != null) {
            if(!to.matches("\\d{2}.\\w{2}.\\d{4}")) {
                event.reply(languageConfiguration.invaliddateformat()).setEphemeral(true).queue();
                return;
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date fromDate;
        Date toDate = null;
        try {
            fromDate = formatter.parse(from);
            if(to != null) toDate = formatter.parse(to);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(languageConfiguration.sicknotetitle());
        embedBuilder.addField(languageConfiguration.persontitle(), person, false);
        embedBuilder.addField(languageConfiguration.reasontitle(), reason, false);
        embedBuilder.addField(languageConfiguration.fromtitle(), formatter.format(fromDate), true);

        if (toDate != null) embedBuilder.addField(languageConfiguration.totitle(), formatter.format(toDate), true);
        else embedBuilder.addField(languageConfiguration.totitle(), "-", true);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(
                Button.danger("sicknote:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        ).queue();
        event.reply(languageConfiguration.sicknotecreated()).setEphemeral(true).queue();


    }
}
