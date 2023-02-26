package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class ConfigureButton implements BotButton{

    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(event.getComponentId().equals("configure:language")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), true);
            embedBuilder.addField("                                  ", languageConfiguration.changelanguage(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language:en", "English"),
                    Button.primary("configure:language:de", "Deutsch"),
                    Button.danger("configure:language:back", languageConfiguration.back())
            ).queue();
        }

        if(event.getComponentId().equals("configure:language:en")){
            bot.getGuildsProvider().updateGuild(event.getGuild().getId(), event.getGuild().getName(), "en");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle())
            ).queue();
        }

        if (event.getComponentId().equals("configure:language:de")) {
            bot.getGuildsProvider().updateGuild(event.getGuild().getId(), event.getGuild().getName(), "de");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle())
            ).queue();

        }

        if (event.getComponentId().equals("configure:language:back")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.configuretitle());
            embedBuilder.setColor(Color.cyan);
            embedBuilder.addField(languageConfiguration.languagetitle(), bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale(), true);
            embedBuilder.addField("                                  ", languageConfiguration.whatshouldbechanged(), false);

            event.getMessage().editMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary("configure:language", languageConfiguration.languagetitle())
            ).queue();
        }
    }
}
