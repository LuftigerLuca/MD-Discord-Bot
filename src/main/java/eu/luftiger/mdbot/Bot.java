package eu.luftiger.mdbot;

import eu.luftiger.mdbot.commands.CommandHandler;
import eu.luftiger.mdbot.configuration.ConfigurationHandler;
import eu.luftiger.mdbot.database.DataSourceProvider;
import eu.luftiger.mdbot.database.DatabaseQueryHandler;
import eu.luftiger.mdbot.database.DatabaseSetup;
import eu.luftiger.mdbot.listeners.BotJoinListener;
import eu.luftiger.mdbot.listeners.BotLeaveListener;
import eu.luftiger.mdbot.listeners.UserJoinListener;
import eu.luftiger.mdbot.listeners.buttons.ButtonListener;
import eu.luftiger.mdbot.listeners.menus.MenuListener;
import eu.luftiger.mdbot.schedulers.UpdateScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class Bot {

    private Logger logger;
    private ConfigurationHandler configurationHandler;
    private MedicationHandler medicationHandler;
    private DatabaseQueryHandler databaseQueryHandler;
    private GuildsProvider guildsProvider;
    private Properties properties;
    private JDA jda;

    public static void main(String[] args) {
        new Bot().start();
    }

    public void start() {
        logger = Logger.getLogger("Bot");
        logger.info("Starting bot...");

        logger.info("Loading properties...");
        properties = new Properties();
        try {
            properties.load(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("bot.properties"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Loading configuration...");
        configurationHandler = new ConfigurationHandler(this);
        try {
            configurationHandler.loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Loading medications...");
        medicationHandler = new MedicationHandler();
        try {
            medicationHandler.loadMedications();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Connecting to database...");
        DataSource dataSource = null;
        try {
            dataSource = DataSourceProvider.initMySQLDataSource(this, configurationHandler.getConfiguration());
            DatabaseSetup.initDatabase(this, dataSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        databaseQueryHandler = new DatabaseQueryHandler(this, dataSource);


        guildsProvider = new GuildsProvider(this);
        guildsProvider.init();

        logger.info("Starting JDA...");
        jda = JDABuilder.createDefault(configurationHandler.getConfiguration().getBotToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(configurationHandler.getConfiguration().getBotActivityType().toUpperCase()), configurationHandler.getConfiguration().getBotActivity()))
                .setStatus(OnlineStatus.valueOf(configurationHandler.getConfiguration().getBotStatus().toUpperCase()))
                .addEventListeners(new BotJoinListener(this),
                        new BotLeaveListener(this),
                        new CommandHandler(this),
                        new ButtonListener(this),
                        new MenuListener(this),
                        new UserJoinListener(this))
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands();

        logger.info("Starting update scheduler...");
        UpdateScheduler updateScheduler = new UpdateScheduler(this);
        updateScheduler.start();

        logger.info("Bot started. Type 'stop' to stop the bot.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        while (running) {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line.equals("exit") || line.equals("quit") || line.equals("stop")) {
                logger.info("Shutting down...");
                updateScheduler.stop();
                jda.shutdown();
                running = false;
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;
    }

    public MedicationHandler getMedicationHandler() {
        return medicationHandler;
    }

    public DatabaseQueryHandler getDatabaseQueryHandler() {
        return databaseQueryHandler;
    }

    public GuildsProvider getGuildsProvider() {
        return guildsProvider;
    }

    public Properties getProperties() {
        return properties;
    }

    public JDA getJda() {
        return jda;
    }
}