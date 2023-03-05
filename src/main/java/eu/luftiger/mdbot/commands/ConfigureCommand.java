package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class ConfigureCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "configure")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(languageConfiguration.configuretitle());
        embedBuilder.setColor(Color.cyan);
        embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
        embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(false).addActionRow(
                Button.primary("configure:language", languageConfiguration.languagetitle()),
                Button.primary("configure:permissions", languageConfiguration.permissiontitle()),
                Button.danger("configure:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        ).queue();
    }
}
