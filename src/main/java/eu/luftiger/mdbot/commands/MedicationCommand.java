package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.commands.interfaces.BotCommand;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import eu.luftiger.mdbot.configuration.Medication;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.List;

public class MedicationCommand implements BotCommand {

    @Override
    public void execute(Bot bot, SlashCommandInteractionEvent event) {
        List<Medication> medications = bot.getMedicationHandler().getMedications();

        LanguageConfiguration languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        if(bot.getGuildsProvider().getGuild(event.getGuild().getId()).getLocale().equals("de")) languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(languageConfiguration.medicationtitle())
                .addField(" ", languageConfiguration.medicationsymptomsarea(), false)
                .setColor(Color.BLUE);

        StringSelectMenu.Builder selectBuilder = StringSelectMenu.create("medication:area");
        selectBuilder.setPlaceholder(languageConfiguration.medicationsymptomsarea())
                .setMaxValues(1)
                .setMinValues(1);
        for (String area : bot.getMedicationHandler().getAreas()) {
            selectBuilder.addOption(area, area.toLowerCase());
        }

        event.replyEmbeds(embedBuilder.build()).addActionRow(selectBuilder.build()).setEphemeral(true).queue();

    }
}
