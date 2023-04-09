package eu.luftiger.mdbot.model;

import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.LanguageConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class BotLogger {

    private final Bot bot;
    private final TextChannel channel;
    private final LanguageConfiguration languageConfiguration;

    public BotLogger(Bot bot, String locale, TextChannel channel) {
        this.bot = bot;
        this.channel = channel;

        if (locale.equals("de")) {
            languageConfiguration = bot.getConfigurationHandler().getGermanLanguageConfiguration();
        } else {
            languageConfiguration = bot.getConfigurationHandler().getEnglishLanguageConfiguration();
        }
    }
}
