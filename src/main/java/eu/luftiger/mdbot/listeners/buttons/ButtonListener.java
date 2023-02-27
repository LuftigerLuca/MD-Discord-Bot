package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class ButtonListener extends ListenerAdapter {

    private final Bot bot;

    public ButtonListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        String componentId = event.getComponentId();
        switch (componentId.split(":")[0]){
            case "signoff" -> new SignOffButton().execute(bot, event);
            case "configure" -> new ConfigureButton().execute(bot, event);
            case "training" -> new TrainingButton().execute(bot, event);
            case "sicknote" -> new SicknoteButton().execute(bot, event);
            case "poll" -> new PollButton().execute(bot, event);
        }


    }
}
