package eu.luftiger.mdbot.listeners.menus;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MenuListener extends ListenerAdapter {

    private final Bot bot;

    public MenuListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event){
        String componentId = event.getComponentId();
        switch (componentId.split(":")[0]){
            case "configure" -> new ConfigurationMenu().execute(bot, event);
        }
    }
}
