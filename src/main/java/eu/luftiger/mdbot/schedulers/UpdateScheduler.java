package eu.luftiger.mdbot.schedulers;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.GuildsProvider;
import eu.luftiger.mdbot.configuration.Configuration;
import eu.luftiger.mdbot.model.BotGuild;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateScheduler {

    private final Bot bot;
    private final Configuration configuration;
    private final GuildsProvider guildsProvider;

    private ScheduledExecutorService executor;

    public UpdateScheduler(Bot bot) {
        this.bot = bot;
        this.configuration = bot.getConfigurationHandler().getConfiguration();
        this.guildsProvider = bot.getGuildsProvider();
    }

    public void start(){
        Runnable runnable = () -> {
            List<BotGuild> botGuilds = guildsProvider.getGuilds();
            List<Guild> guilds = bot.getJda().getGuilds();

            for (Guild guild : guilds) {
                if(botGuilds.stream().noneMatch(botGuild -> botGuild.getGuildId().equals(guild.getId()))){
                    try {
                        guildsProvider.addGuild(new BotGuild(guild.getId(), guild.getName(), "en", "", guild.getDefaultChannel().getId(), false));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                if(botGuilds.stream().anyMatch(botGuild -> botGuild.getGuildId().equals(guild.getId()) && !botGuild.getGuildName().equals(guild.getName()))){
                    try {
                        BotGuild botGuild = guildsProvider.getGuild(guild.getId());
                        guildsProvider.updateGuild(guild.getId(), guild.getName(), botGuild.getLocale(), botGuild.getGreetingMessage(), botGuild.getGreetingChannelId(), botGuild.isGreetingEnabled());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            for (BotGuild botGuild : botGuilds) {
                if(guilds.stream().noneMatch(guild -> guild.getId().equals(botGuild.getGuildId()))){
                    try {
                        guildsProvider.removeGuild(botGuild.getGuildId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);

    }

    public void stop(){
        executor.shutdown();
    }
}
