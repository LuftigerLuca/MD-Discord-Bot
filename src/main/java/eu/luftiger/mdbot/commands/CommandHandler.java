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
        SlashCommandData configureCommand = Commands.slash("configurate", "Configure the bot for your server");

        SlashCommandData signOffCommand = Commands.slash("signoff", "Sign off from work for an defined amount of time")
                .addOption(OptionType.STRING, "reason", "Reason for the sign off", true)
                .addOption(OptionType.STRING, "from", "Start of the sign off, format: dd.MM.yyyy", true)
                .addOption(OptionType.STRING, "to", "End of the sign off, format: dd.MM.yyyy", false);

        SlashCommandData signOffInfoCommand = Commands.slash("signoffinfo", "get information about your or another users sign offs")
                .addOption(OptionType.USER, "user", "User to get the sign offs from", false);

        SlashCommandData infoCommand = Commands.slash("info", "Get information about the bot");

        SlashCommandData trainingData = Commands.slash("training", "Create a new training")
                        .addOption(OptionType.STRING, "name", "Name of the training", true)
                        .addOption(OptionType.STRING, "date", "Date of the training, format: dd.MM.yyyy mm:hh", true)
                        .addOption(OptionType.STRING, "description", "Description of the training", true)
                        .addOption(OptionType.STRING, "location", "Location of the training", true)
                        .addOption(OptionType.STRING, "requirements", "Requirements for the training", true)
                        .addOption(OptionType.INTEGER, "maxparticipants", "Maximum amount of participants", true);

        SlashCommandData sickNoteCommand = Commands.slash("sicknote", "Create a new sick note")
                .addOption(OptionType.STRING, "person", "Person who is sick", true)
                .addOption(OptionType.STRING, "reason", "Reason for the sick note", true)
                .addOption(OptionType.STRING, "from", "Start of the sick note, format: dd.MM.yyyy", true)
                .addOption(OptionType.STRING, "to", "End of the sick note, format: dd.MM.yyyy", false)
                .addOption(OptionType.STRING, "other", "Other information about the sick note", false);

        SlashCommandData pollCommand = Commands.slash("poll", "create a new poll")
                .addOption(OptionType.STRING, "question", "Question of the poll", true)
                .addOption(OptionType.STRING, "options", "Options for the poll", true);

        SlashCommandData setGreetingCommand = Commands.slash("setgreeting", "Set the greeting message for the bot")
                .addOption(OptionType.STRING, "message", "Message to greet the user", true)
                .addOption(OptionType.CHANNEL, "channel", "Channel to send the message", true)
                .addOption(OptionType.BOOLEAN, "enabled", "Enable or disable the greeting message", true);

        SlashCommandData uprankCommand = Commands.slash("uprank", "Uprank a user")
                .addOption(OptionType.USER, "user", "User to uprank", true)
                .addOption(OptionType.ROLE, "role", "Role to uprank to", true);

        jda.updateCommands().addCommands(configureCommand, signOffCommand, signOffInfoCommand, infoCommand, trainingData, sickNoteCommand, pollCommand, setGreetingCommand, uprankCommand).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        switch (event.getName()){
            case "configurate" -> new ConfigureCommand().execute(bot, event);
            case "signoff" -> new SignOffCommand().execute(bot, event);
            case "signoffinfo" -> new SignOffInfoCommand().execute(bot, event);
            case "info" -> new InfoCommand().execute(bot, event);
            case "training" -> new TrainingCommand().execute(bot, event);
            case "sicknote" -> new SickNoteCommand().execute(bot, event);
            case "poll" -> new PollCommand().execute(bot, event);
            case "setgreeting" -> new SetGreetingCommand().execute(bot, event);
            case "uprank" -> new UprankCommand().execute(bot, event);
        }
    }
}
