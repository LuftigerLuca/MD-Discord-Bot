package eu.luftiger.mdbot.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import eu.luftiger.mdbot.Bot;
import eu.luftiger.mdbot.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides a DataSource for the MySQL database
 */
public class DataSourceProvider {

    /**
     * Initializes a MySQL DataSource
     * @param bot the discordbot instance
     * @param configuration the configuration to use
     * @return the initialized DataSource
     * @throws SQLException if an error occurs
     */
    public static DataSource initMySQLDataSource(Bot bot, Configuration configuration) throws SQLException {

        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        //dataSource.setUrl("jdbc:mysql://" +  configuration.getDatabaseUsername() +":" + configuration.getDatabasePassword() + "@" + configuration.getDatabaseHost() + ":" + configuration.getDatabasePort() + "/" + configuration.getDatabaseDatabase());
        dataSource.setUrl("jdbc:mysql://u14_9qfs6pqIXf:ruos2raSf!!RZKZxubl%3DLE%2Bo@node-1.luftiger.eu:3306/s14_md_bot_test");

        testDataSource(bot, dataSource);
        return dataSource;
    }


    /**
     * Tests the connection to the database
     * @param bot the discordbot instance
     * @param dataSource the DataSource to test
     * @throws SQLException if an error occurs
     */
    private static void testDataSource(Bot bot, DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        }
        if (bot != null) {
            bot.getLogger().info("Database connection established!");
        }
    }
}
