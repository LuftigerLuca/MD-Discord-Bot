package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface BotButton {

    void execute(Bot bot, ButtonInteractionEvent event);
}
