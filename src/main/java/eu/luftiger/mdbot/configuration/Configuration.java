package eu.luftiger.mdbot.configuration;

public record Configuration(BotConfiguration bot, DatabaseConfiguration database, GeneralConfiguration general){
    public String getBotToken() {
        return bot.token();
    }

    public String getBotActivityType() {
        return bot.activitytype();
    }

    public String getBotActivity() {
        return bot.activity();
    }

    public String getBotStatus() {
        return bot.status();
    }

    public String getDatabaseHost() {
        return database.host();
    }

    public int getDatabasePort() {
        return database.port();
    }

    public String getDatabaseDatabase() {
        return database.database();
    }

    public String getDatabaseUsername() {
        return database.username();
    }

    public String getDatabasePassword() {
        return database.password();
    }

    public int getGeneralUpdateInterval() {
        return general.updateinterval();
    }
}

record BotConfiguration(
    String token,
    String activitytype,
    String activity,
    String status){
}

record DatabaseConfiguration(
    String host,
    int port,
    String database,
    String username,
    String password){
}

record GeneralConfiguration(int updateinterval){
}