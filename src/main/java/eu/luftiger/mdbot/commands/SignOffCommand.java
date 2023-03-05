package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotSignOff;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SignOffCommand implements BotCommand {
    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        UUID uuid = UUID.randomUUID();
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
        embedBuilder.setTitle(languageConfiguration.signofftitle());
        embedBuilder.addField(languageConfiguration.reasontitle(), reason, false);
        embedBuilder.addField(languageConfiguration.fromtitle(), formatter.format(fromDate), true);

        if (toDate != null) embedBuilder.addField(languageConfiguration.totitle(), formatter.format(toDate), true);
        else embedBuilder.addField(languageConfiguration.totitle(), "-", true);

        String author = event.getUser().getName();
        if(event.getMember().getNickname() != null) author = event.getMember().getNickname();

        embedBuilder.addField("", languageConfiguration.notacceptedyet(), false);
        embedBuilder.setAuthor(author, null, event.getUser().getAvatarUrl());
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setFooter(uuid.toString());

        event.getChannel().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.success("signoff:accept", languageConfiguration.acceptbutton()),
                        Button.danger("signoff:decline", languageConfiguration.declinebutton()),
                        Button.danger("signoff:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1")))
                .queue();

        if(toDate == null) toDate = fromDate;
        bot.getGuildsProvider().addSignOff(new BotSignOff(uuid.toString(), event.getUser().getId(), event.getGuild().getId(), reason, formatter.format(fromDate), formatter.format(toDate), false));
        event.reply(languageConfiguration.signoffcreated()).setEphemeral(true).queue();
    }
}
