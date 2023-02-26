package eu.luftiger.mdbot;

import eu.luftiger.mdbot.commands.CommandHandler;
import eu.luftiger.mdbot.configuration.ConfigurationHandler;
import eu.luftiger.mdbot.database.DataSourceProvider;
import eu.luftiger.mdbot.database.DatabaseQueryHandler;
import eu.luftiger.mdbot.database.DatabaseSetup;
import eu.luftiger.mdbot.listeners.BotJoinListener;
import eu.luftiger.mdbot.listeners.BotLeaveListener;
import eu.luftiger.mdbot.listeners.ButtonListener;
import eu.luftiger.mdbot.schedulers.UpdateScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.sql.DataSource;
import java.util.logging.Logger;

public class Bot {

    private Logger logger;
    private ConfigurationHandler configurationHandler;
    private DatabaseQueryHandler databaseQueryHandler;
    private GuildsProvider guildsProvider;

    private JDA jda;

    public static void main(String[] args) {
        try {
            new Bot().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws Exception {
        logger = Logger.getLogger("Bot");
        logger.info("Starting bot...");

        logger.info("Loading configuration...");
        configurationHandler = new ConfigurationHandler(this);
        configurationHandler.loadConfiguration();

        logger.info("Connecting to database...");
        DataSource dataSource = DataSourceProvider.initMySQLDataSource(this, configurationHandler.getConfiguration());
        DatabaseSetup.initDatabase(this, dataSource);
        databaseQueryHandler = new DatabaseQueryHandler(this, dataSource);


        guildsProvider = new GuildsProvider(this);
        guildsProvider.init();

        logger.info("Starting JDA...");
        jda = JDABuilder.createDefault(configurationHandler.getConfiguration().getBotToken())
                .setActivity(Activity.of(Activity.ActivityType.valueOf(configurationHandler.getConfiguration().getBotActivityType().toUpperCase()), configurationHandler.getConfiguration().getBotActivity()))
                .setStatus(OnlineStatus.valueOf(configurationHandler.getConfiguration().getBotStatus().toUpperCase()))
                .addEventListeners(new BotJoinListener(this),
                        new BotLeaveListener(this),
                        new CommandHandler(this),
                        new ButtonListener(this))
                .build();

        jda.awaitReady();

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands();

        logger.info("Starting update scheduler...");
        UpdateScheduler updateScheduler = new UpdateScheduler(this);
        updateScheduler.start();
    }


    public Logger getLogger() {
        return logger;
    }

    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;
    }

    public DatabaseQueryHandler getDatabaseQueryHandler() {
        return databaseQueryHandler;
    }

    public GuildsProvider getGuildsProvider() {
        return guildsProvider;
    }

    public JDA getJda() {
        return jda;
    }
}