package eu.luftiger.mdbot.commands;

import eu.luftiger.mdbot.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandHandler extends ListenerAdapter {

    private final Bot bot;
    private final JDA jda;

    public CommandHandler(Bot bot) {
        this.bot = bot;
        this.jda = bot.getJda();
    }

    public void registerCommands(){
        SlashCommandData signOffCommand = Commands.slash("signoff", "Sign off from work for an defined amount of time")
                .addOption(OptionType.STRING, "reason", "Reason for the sign off", true)
                .addOption(OptionType.STRING, "from", "Start of the sign off, format: dd.MM.yyyy", true)
                .addOption(OptionType.STRING, "to", "End of the sign off, format: dd.MM.yyyy", false);

        SlashCommandData signOffInfoCommand = Commands.slash("signoffinfo", "get information about your or another users sign offs")
                .addOption(OptionType.USER, "user", "User to get the sign offs from", false);

        jda.updateCommands().addCommands(signOffCommand, signOffInfoCommand).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        switch (event.getName()){
            case "signoff" -> new SignOffCommand().execute(bot, event);
            case "signoffinfo" -> new SignOffInfoCommand().execute(bot, event);
        }
    }
}
