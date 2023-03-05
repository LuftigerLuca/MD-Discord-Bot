package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingButton implements BotButton{
    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        if (event.getComponentId().equals("training:delete")) {
            if (bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getUser().getId(), "deletetraining")) {
                event.getMessage().delete().queue();
                event.reply(languageConfiguration.trainingdeleted()).setEphemeral(true).queue();
            } else {
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            }
        }

        List<String> participatns = new ArrayList<>();
        participatns.addAll(List.of(event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().split("\n-")));

        if (event.getComponentId().equals("training:join")) {
            if (participatns.contains(event.getMember().getAsMention())) {
                event.reply(languageConfiguration.alreadyjoined()).setEphemeral(true).queue();
                return;
            }

            participatns.add(event.getMember().getAsMention());
            StringBuilder participantsString = new StringBuilder();

            for (int i = 0; i < Integer.parseInt(String.valueOf(event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().charAt(3))) + 1; i++) {
                if(i==0){
                    participantsString.append("[" + (participatns.size() - 1) + "/" + event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().charAt(3) + "]");
                }else {
                    if(participatns.size() - 1 >= i) participantsString.append("\n-").append(participatns.get(i));
                    else participantsString.append("\n-");
                }
            }

            boolean isFull = participatns.size() - 1 >= Integer.parseInt(String.valueOf(event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().charAt(3)));

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.addField(languageConfiguration.descriptiontitle(), messageEmbed.getFields().get(0).getValue(), false);
            embedBuilder.addField(languageConfiguration.datetitle(), messageEmbed.getFields().get(1).getValue(), true);
            embedBuilder.addField(languageConfiguration.locationtitle(), messageEmbed.getFields().get(2).getValue(), true);
            embedBuilder.addField(languageConfiguration.requirementstitle(), messageEmbed.getFields().get(3).getValue(), false);
            embedBuilder.addField(languageConfiguration.participants(), participantsString.toString(), true);
            embedBuilder.setAuthor(messageEmbed.getAuthor().getName(), null, messageEmbed.getAuthor().getIconUrl());
            if(isFull) embedBuilder.setColor(Color.RED);
            else embedBuilder.setColor(Color.YELLOW);

           MessageEditAction messageEditAction = event.getMessage().editMessageEmbeds(embedBuilder.build());
           if(isFull){
               messageEditAction.setActionRow(
                       Button.success("training:join", languageConfiguration.jointraining()).asDisabled(),
                       Button.danger("training:leave", languageConfiguration.leavetraining()),
                       Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
               );
           } else {
               messageEditAction.setActionRow(
                       Button.success("training:join", languageConfiguration.jointraining()),
                       Button.danger("training:leave", languageConfiguration.leavetraining()),
                       Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
               );
           }
           messageEditAction.queue();
           event.reply(languageConfiguration.jointraining()).setEphemeral(true).queue();
        }

        if (event.getComponentId().equals("training:leave")) {

            if (!participatns.contains(event.getMember().getAsMention())) {
                event.reply(languageConfiguration.notjoined()).setEphemeral(true).queue();
                return;
            }

            participatns.remove(event.getMember().getAsMention());
            StringBuilder participantsString = new StringBuilder();

            for (int i = 0; i < Integer.parseInt(String.valueOf(event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().charAt(3))) + 1; i++) {
                if(i==0){
                    participantsString.append("[" + (participatns.size() - 1) + "/" + event.getMessage().getEmbeds().get(0).getFields().get(4).getValue().charAt(3) + "]");
                }else {
                    if(participatns.size() - 1 >= i) participantsString.append("\n-").append(participatns.get(i));
                    else participantsString.append("\n-");
                }
            }



            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.addField(languageConfiguration.descriptiontitle(), messageEmbed.getFields().get(0).getValue(), false);
            embedBuilder.addField(languageConfiguration.datetitle(), messageEmbed.getFields().get(1).getValue(), true);
            embedBuilder.addField(languageConfiguration.locationtitle(), messageEmbed.getFields().get(2).getValue(), true);
            embedBuilder.addField(languageConfiguration.requirementstitle(), messageEmbed.getFields().get(3).getValue(), false);
            embedBuilder.addField(languageConfiguration.participants(), participantsString.toString(), true);
            embedBuilder.setAuthor(event.getMember().getNickname(), null, event.getMember().getUser().getAvatarUrl());
            embedBuilder.setColor(Color.YELLOW);

            boolean isEmpty = participatns.size() - 1 == 0;
            MessageEditAction messageEditAction = event.getMessage().editMessageEmbeds(embedBuilder.build());
            if(isEmpty){
                messageEditAction.setActionRow(
                        Button.success("training:join", languageConfiguration.jointraining()).asEnabled(),
                        Button.danger("training:leave", languageConfiguration.leavetraining()).asDisabled(),
                        Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
                );
            } else {
                messageEditAction.setActionRow(
                        Button.success("training:join", languageConfiguration.jointraining()).asEnabled(),
                        Button.danger("training:leave", languageConfiguration.leavetraining()),
                        Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
                );
            }
            messageEditAction.queue();
            event.reply(languageConfiguration.leavetraining()).setEphemeral(true).queue();
        }
    }
}
