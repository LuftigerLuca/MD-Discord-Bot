package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class UprankCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "uprank")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        User user = event.getOption("user").getAsUser();
        Role role = event.getOption("role").getAsRole();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(languageConfiguration.upranktitle())
                .setDescription(languageConfiguration.upranktext().replace("{target}", user.getAsMention()).replace("{role}", role.getAsMention()))
                .setFooter(languageConfiguration.uprankfooter().replace("{user}", event.getMember().getEffectiveName()))
                .setColor(Color.cyan);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        event.reply(languageConfiguration.upranked()).setEphemeral(true).queue();
    }
}
