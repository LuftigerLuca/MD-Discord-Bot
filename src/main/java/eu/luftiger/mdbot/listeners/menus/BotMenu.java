package eu.luftiger.mdbot.listeners.menus;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface BotMenu {

    void execute(Bot bot, StringSelectInteractionEvent event);
}
