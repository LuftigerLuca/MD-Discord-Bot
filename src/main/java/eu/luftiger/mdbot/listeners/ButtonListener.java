package eu.luftiger.mdbot.listeners;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class ButtonListener extends ListenerAdapter {

    private final Bot bot;

    public ButtonListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        if(event.getComponentId().equals("signoff:accept")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setDescription(messageEmbed.getDescription());
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setAuthor(messageEmbed.getAuthor().getName(), null, messageEmbed.getAuthor().getIconUrl());
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), false);
            embedBuilder.addField(messageEmbed.getFields().get(1).getName(), messageEmbed.getFields().get(1).getValue(), true);
            embedBuilder.addField(messageEmbed.getFields().get(2).getName(), messageEmbed.getFields().get(2).getValue(), true);
            embedBuilder.addField("Accepted by", event.getUser().getAsTag(), false);
            embedBuilder.setFooter(messageEmbed.getFooter().getText());

            bot.getGuildsProvider().updateSignOff(messageEmbed.getFooter().getText(), true);
            event.getMessage().editMessageEmbeds(embedBuilder.build()).queue();

            event.reply("You have accepted the signoff!").setEphemeral(true).queue();

        }else if(event.getComponentId().equals("signoff:decline")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setDescription(messageEmbed.getDescription());
            embedBuilder.setColor(Color.RED);
            embedBuilder.setAuthor(messageEmbed.getAuthor().getName(), null, messageEmbed.getAuthor().getIconUrl());
            embedBuilder.addField(messageEmbed.getFields().get(0).getName(), messageEmbed.getFields().get(0).getValue(), false);
            embedBuilder.addField(messageEmbed.getFields().get(1).getName(), messageEmbed.getFields().get(1).getValue(), true);
            embedBuilder.addField(messageEmbed.getFields().get(2).getName(), messageEmbed.getFields().get(2).getValue(), true);
            embedBuilder.addField("Declined by", event.getUser().getAsTag(), false);
            embedBuilder.setFooter(messageEmbed.getFooter().getText());

            bot.getGuildsProvider().updateSignOff(messageEmbed.getFooter().getText(), false);
            event.getMessage().editMessageEmbeds(embedBuilder.build()).queue();

            event.reply("You have declined the signoff!").setEphemeral(true).queue();
        }else if(event.getComponentId().equals("signoff:delete")){
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            String messageId = messageEmbed.getFooter().getText();
            event.getMessage().delete().queue();
            bot.getGuildsProvider().removeSignOff(messageId);
            event.reply("You have deleted the signoff!").setEphemeral(true).queue();
        }
    }
}
