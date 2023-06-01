package eu.luftiger.mdbot.listeners;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.GuildsProvider;
import eu.luftiger.mdbot.model.BotGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotJoinListener extends ListenerAdapter {

    private final GuildsProvider guildsProvider;

    public BotJoinListener(Bot bot) {
        this.guildsProvider = bot.getGuildsProvider();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        guildsProvider.addGuild(new BotGuild(guild.getId(), guild.getName(), "en", "", event.getGuild().getId().toString(), false));
    }
}
