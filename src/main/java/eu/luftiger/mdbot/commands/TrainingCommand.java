package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.model.BotMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class TrainingCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        BotMember member = bot.getGuildsProvider().getMember(event.getGuild().getId(), event.getUser().getId());

        if(!bot.getGuildsProvider().hasPermission(event.getGuild().getId(), event.getMember().getId(), "createtraining")) {
            event.reply(languageConfiguration.permissiondenied()).setEphemeral(true).queue();
            return;
        }

        String name = event.getOption("name").getAsString();
        String description = event.getOption("description").getAsString();
        String date = event.getOption("date").getAsString();
        String location = event.getOption("location").getAsString();
        String requirements = event.getOption("requirements").getAsString();
        int maxParticipants = event.getOption("maxparticipants").getAsInt();

        String author = event.getUser().getName();
        if(event.getMember().getNickname() != null) author = event.getMember().getNickname();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(name)
                .addField(languageConfiguration.descriptiontitle(), description, false)
                .addField(languageConfiguration.datetitle(), date, true)
                .addField(languageConfiguration.locationtitle(), location, true)
                .addField(languageConfiguration.requirementstitle(), requirements, false)
                .setAuthor(author, null, event.getUser().getAvatarUrl())
                .setColor(Color.YELLOW);

        String participants = "\n-".repeat(Math.max(0, maxParticipants));
        embedBuilder.addField(languageConfiguration.participants(), "[0/" + maxParticipants + "]" + participants, true);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).addActionRow(
                Button.success("training:join", languageConfiguration.jointraining()),
                Button.danger("training:leave", languageConfiguration.leavetraining()),
                Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        ).queue();

        event.reply(languageConfiguration.trainingcreated()).setEphemeral(true).queue();
    }
}
