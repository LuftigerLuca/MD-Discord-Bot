package eu.luftiger.mdbot.controller;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.model.BotGuild;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guilds")
public class GuildsMapping {

    private final Bot bot;

    public GuildsMapping(Bot bot) {
        this.bot = bot;
    }

    @GetMapping("/amount")
    public String getAmountOfGuilds(){
        return String.valueOf(bot.getGuildsProvider().getGuilds().size());
    }

    @GetMapping("/users/amount")
    public String getAmountOfUsers(){
        return String.valueOf(bot.getGuildsProvider().getGuilds().stream().mapToInt(guild -> guild.getMembers().size()).sum());
    }

    @PostMapping("/update/greeting/enabled/{guildId}/{enabled}")
    public String updateGreetingEnabled(@PathVariable String guildId, @PathVariable boolean enabled){
        BotGuild guild = bot.getGuildsProvider().getGuild(guildId);
        guild.setGreetingEnabled(enabled);
        bot.getGuildsProvider().updateGuild(guild, guild.getGuildName(), guild.getLocale(), guild.getGreetingMessage(), guild.getGreetingChannelId(), guild.isGreetingEnabled());

        return "check";
    }

}
