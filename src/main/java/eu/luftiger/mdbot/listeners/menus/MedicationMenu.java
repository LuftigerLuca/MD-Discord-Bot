package eu.luftiger.mdbot.listeners.menus;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.List;

public class MedicationMenu implements BotMenu{

    @Override
    public void execute(Bot bot, StringSelectInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if (event.getComponentId().equals("medication:area")) {
            List<String> symptoms = bot.getMedicationHandler().getSymptomsOfArea(event.getValues().get(0));

            event.getMessage().editMessageEmbeds();
        }
    }
}
