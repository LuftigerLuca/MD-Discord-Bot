package eu.luftiger.mdbot.database;

import eu.luftiger.mdbot.Bot;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSetup {

    /**
     * Creates the database tables if they don't exist
     * @param bot the discordbot instance
     * @param dataSource the datasource to use
     * @throws SQLException if an error occurs
     */
    public static void initDatabase(Bot bot, DataSource dataSource) throws SQLException {
        String setup = null;

        try (InputStream in = DatabaseSetup.class.getClassLoader().getResourceAsStream("setup.sql")){
            if (in != null) {
                setup = new String(in.readAllBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(setup == null) return;

        String[] queries = setup.split(";");
        try (Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(false);

            for (String query : queries) {
                if(query.isBlank()) continue;
                try (PreparedStatement statement = connection.prepareStatement(query)){
                    statement.execute();
                }
            }
            connection.commit();
        }



        bot.getLogger().info("Database setup complete!");
    }
}
