package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class SicknoteButton implements BotButton{
    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "deletesicknote")){
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        if(event.getComponentId().equals("sicknote:delete")){
            event.getMessage().delete().queue();
            event.reply(languageConfiguration.sicknotedeleted()).setEphemeral(true).queue();
        }
    }
}
