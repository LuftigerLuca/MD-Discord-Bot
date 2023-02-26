package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class SignOffInfoCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        //TODO: implement permission check

        User user = event.getOption("user") != null ? event.getOption("user").getAsUser() : event.getUser();
        boolean isSignOff = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).isSignedOff();
        int signOffs = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).getSignOffs().size();
        int daysSingedOff = bot.getGuildsProvider().getMember(event.getGuild().getId(), user.getId()).getDaysSignedOff();


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Sign Off Info");
        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
        embedBuilder.setColor(Color.cyan);
        embedBuilder.addField("Signed Off", isSignOff ? "Yes" : "No", true);
        embedBuilder.addField("Sign Offs", String.valueOf(signOffs), true);
        embedBuilder.addField("Days Signed Off", String.valueOf(daysSingedOff), true);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();

    }
}
