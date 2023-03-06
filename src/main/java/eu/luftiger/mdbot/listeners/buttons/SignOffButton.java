package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.awt.*;

public class SignOffButton implements BotButton{

    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if (event.getComponentId().equals("signoff:accept")) {
            if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "acceptsignoff")) {
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
                return;
            }

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.signofftitle())
                    .setDescription(messageEmbed.getDescription())
                    .setColor(Color.GREEN)
                    .setAuthor(messageEmbed.getAuthor().getName(), null, messageEmbed.getAuthor().getIconUrl())
                    .addField(languageConfiguration.reasontitle(), messageEmbed.getFields().get(0).getValue(), false)
                    .addField(languageConfiguration.fromtitle(), messageEmbed.getFields().get(1).getValue(), true)
                    .addField(languageConfiguration.totitle(), messageEmbed.getFields().get(2).getValue(), true)
                    .addField(languageConfiguration.acceptbutton(), event.getUser().getAsTag(), false)
                    .setFooter(messageEmbed.getFooter().getText());

            bot.getGuildsProvider().updateSignOff(messageEmbed.getFooter().getText(), true);
            event.getMessage().editMessageEmbeds(embedBuilder.build()).queue();

            event.reply(languageConfiguration.uhaveaccepted()).setEphemeral(true).queue();

        } else if (event.getComponentId().equals("signoff:decline")) {
            if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "declinesignoff")) {
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
                return;
            }

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(languageConfiguration.signofftitle());
            embedBuilder.setDescription(messageEmbed.getDescription())
                    .setColor(Color.RED)
                    .setAuthor(messageEmbed.getAuthor().getName(), null, messageEmbed.getAuthor().getIconUrl())
                    .addField(languageConfiguration.reasontitle(), messageEmbed.getFields().get(0).getValue(), false)
                    .addField(languageConfiguration.fromtitle(), messageEmbed.getFields().get(1).getValue(), true)
                    .addField(languageConfiguration.totitle(), messageEmbed.getFields().get(2).getValue(), true)
                    .addField(languageConfiguration.declinedby(), event.getUser().getAsTag(), false)
                    .setFooter(messageEmbed.getFooter().getText());

            bot.getGuildsProvider().updateSignOff(messageEmbed.getFooter().getText(), false);
            event.getMessage().editMessageEmbeds(embedBuilder.build()).queue();

            event.reply(languageConfiguration.uhavedeclined()).setEphemeral(true).queue();

        } else if (event.getComponentId().equals("signoff:delete")) {
            if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "deletesignoff")) {
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
                return;
            }


            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            String messageId = messageEmbed.getFooter().getText();
            event.getMessage().delete().queue();
            bot.getGuildsProvider().removeSignOff(messageId);
            event.reply(languageConfiguration.uhavedeleted()).setEphemeral(true).queue();
        }
    }
}
