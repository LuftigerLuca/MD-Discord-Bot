package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotGuild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SetGreetingCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "setgreeting")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        String greetingMessage = event.getOption("message").getAsString();
        TextChannel greetingChannel = event.getOption("channel").getAsChannel().asTextChannel();
        boolean greetingEnabled = event.getOption("enabled").getAsBoolean();

        BotGuild guild = bot.getGuildsProvider().getGuild(event.getGuild().getId());

        bot.getGuildsProvider().updateGuild(event.getGuild().getId(), guild.getGuildName(), guild.getLocale(), greetingMessage, greetingChannel.getId(), greetingEnabled);

        event.reply(languageConfiguration.greetingset()).setEphemeral(true).queue();
    }
}
