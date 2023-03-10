package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class SignOffInfoCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "singoffinfo")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        User user = event.getOption("user") != null ? event.getOption("user").getAsUser() : event.getUser();

        if(bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()) == null){
            event.reply(languageConfiguration.unknownuser()).setEphemeral(true).queue();
            return;
        }

        boolean isSignOff = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).isSignedOff();
        int signOffs = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).getSignOffs().size();
        int daysSingedOff = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).getDaysSignedOff();

        String author = user.getName();
        if(event.getGuild().getMemberById(user.getId()) != null && event.getGuild().getMemberById(user.getId()).getNickname() != null) author = event.getGuild().getMemberById(user.getId()).getNickname();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(languageConfiguration.signoffinfotitle())
                .setAuthor(author, null, user.getAvatarUrl())
                .setColor(Color.cyan)
                .addField(languageConfiguration.signedoff(), isSignOff ? languageConfiguration.yes() : languageConfiguration.no(), true)
                .addField(languageConfiguration.signoffs(), String.valueOf(signOffs), true)
                .addField(languageConfiguration.dayssignedoff(), String.valueOf(daysSingedOff), true);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }
}
