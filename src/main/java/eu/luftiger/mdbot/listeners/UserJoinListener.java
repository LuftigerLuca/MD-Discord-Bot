package eu.luftiger.mdbot.listeners;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.model.BotGuild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserJoinListener extends ListenerAdapter {

    private final Bot bot;

    public UserJoinListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        BotGuild guild = bot.getGuildsProvider().getGuild(event.getGuild().getId());
        if(!guild.isGreetingEnabled() || guild.getGreetingMessage() == null || guild.getGreetingChannelId() == null){
            return;
        }

        String greetingMessage = guild.getGreetingMessage();
        MessageChannel greetingChannel = event.getGuild().getTextChannelById(guild.getGreetingChannelId());

        if(greetingChannel == null){
            return;
        }

        greetingChannel.sendMessage(greetingMessage.replace("{user}", event.getUser().getAsMention())).queue();
    }
}
