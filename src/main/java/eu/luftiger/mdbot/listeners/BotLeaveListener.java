package eu.luftiger.mdbot.listeners;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.GuildsProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotLeaveListener extends ListenerAdapter {

    private final GuildsProvider guildsProvider;

    public BotLeaveListener(Bot bot) {
        this.guildsProvider = bot.getGuildsProvider();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        guildsProvider.removeGuild(guild.getId());
    }
}
