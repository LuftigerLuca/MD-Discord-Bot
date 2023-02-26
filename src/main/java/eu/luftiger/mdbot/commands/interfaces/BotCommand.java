package eu.luftiger.mdbot.commands.interfaces;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface BotCommand {
     void execute(Bot bot, SlashCommandInteractionEvent event);
}
