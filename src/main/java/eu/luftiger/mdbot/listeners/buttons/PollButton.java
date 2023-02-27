package eu.luftiger.mdbot.listeners.buttons;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.text.DecimalFormat;
import java.util.*;

public class PollButton implements BotButton {

    @Override
    public void execute(Bot bot, ButtonInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if (bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de"))
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        BotPoll botPoll = bot.getGuildsProvider().getGuild(event.getGuild().getId()).getPoll(event.getMessage().getEmbeds().get(0).getFooter().getText());

        if(event.getComponentId().equals("poll:delete")){
            if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "deletepoll")){
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
                return;
            }

            if(botPoll != null )bot.getGuildsProvider().removePoll(event.getGuild().getId(), botPoll.getId());
            event.getMessage().delete().queue();
            event.reply(languageConfiguration.polldeleted()).setEphemeral(true).queue();
        }

        if(event.getComponentId().equals("poll:close")){
            if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "closepoll")){
                event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
                return;
            }

            bot.getGuildsProvider().removePoll(event.getGuild().getId(), botPoll.getId());

            event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds().get(0)).setActionRow(
                    Button.danger("poll:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
            ).queue();

            event.reply(languageConfiguration.pollclosed()).setEphemeral(true).queue();
        }

        if(event.getComponentId().startsWith("poll:vote:")){
            String option = event.getComponentId().split(":")[2].toLowerCase().replace(" ", "");
            int participatsCount = Integer.parseInt(event.getMessage().getEmbeds().get(0).getFields().get(0).getValue());

            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            Map<String, Float> optionPercentage = new HashMap<>();
            Map<String, Integer> optionVotes = new HashMap<>();

            if(botPoll.hasParticipantOnOption(event.getUser().getId(), option)){
                event.reply(languageConfiguration.alreadyvoted()).setEphemeral(true).queue();
                return;
            }

            int i = 0;
            for (MessageEmbed.Field field : event.getMessage().getEmbeds().get(0).getFields()) {
                if(i == 0){
                    i++;
                    continue;
                }

                float percentage = Float.parseFloat(field.getValue().split(" ")[1].replace("%", "").replace(",", "."));
                optionPercentage.put(field.getName().toLowerCase(), percentage);
                optionVotes.put(field.getName().toLowerCase(), (int) (participatsCount * percentage / 100));
                i++;
            }

            if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getPoll(event.getMessage().getEmbeds().get(0).getFooter().getText()).getParticipantVote(event.getUser().getId()) != null)
                optionVotes.put(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getPoll(event.getMessage().getEmbeds().get(0).getFooter().getText()).getParticipantVote(event.getUser().getId()), optionVotes.get(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getPoll(event.getMessage().getEmbeds().get(0).getFooter().getText()).getParticipantVote(event.getUser().getId())) - 1);

            optionVotes.put(option, optionVotes.get(option) + 1);

            if(!botPoll.hasParticipant(event.getUser().getId()))
                participatsCount++;

            bot.getGuildsProvider().addPollParticipant(event.getGuild().getId(), event.getMessage().getEmbeds().get(0).getFooter().getText(), event.getUser().getId(), option);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(messageEmbed.getTitle());
            embedBuilder.setColor(messageEmbed.getColor());
            embedBuilder.setDescription(messageEmbed.getDescription());
            embedBuilder.addField(languageConfiguration.participants(), String.valueOf(participatsCount), false);

            for(Map.Entry<String, Integer> entry : optionVotes.entrySet()){
                float percentageVote = (float) entry.getValue() / participatsCount * 100;
                StringBuilder percentageString = new StringBuilder();
                percentageString.append("[");
                for(int j = 0; j <= 99; j++){
                    if(j < percentageVote) percentageString.append(":");
                    else percentageString.append(".");
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                percentageString.append("] ").append(decimalFormat.format(percentageVote)).append("%");

                embedBuilder.addField(entry.getKey(), percentageString.toString(), false);
            }

            embedBuilder.setFooter(messageEmbed.getFooter().getText());

            List<Button> optionButtons = new ArrayList<>();
            for (String s : optionVotes.keySet().stream().toList()) {
                optionButtons.add(Button.primary("poll:vote:" + s, s));
            }
            optionButtons.add(Button.danger("poll:close", languageConfiguration.pollclose()));
            optionButtons.add(Button.danger("poll:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1")));

            event.getMessage().editMessageEmbeds(embedBuilder.build()).queue();
            event.reply(languageConfiguration.pollvoted()).setEphemeral(true).queue();
        }
    }
}
