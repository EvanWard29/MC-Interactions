package uk.co.evanward.twitchinteractions.twitch.server;

import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite
{
    /**
     * Check if the SQLite db exists and contains the required tables
     */
    public static void initialiseSQLite()
    {
        try {
            Statement statement = connection().createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            // Create the relevant tables if they don't already exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS followers (id string, username string)");
        } catch (SQLException e) {
            TwitchInteractions.logger.error(e.getMessage());
        }
    }

    /**
     * Get a connection to the SQLite database
     */
    private static Connection connection() throws SQLException
    {
        // Create DB file if it doesn't exist
        File db = new File(FileHelper.WORLD_DATA_FOLDER_PATH + "/" + TwitchInteractions.MOD_ID + ".sqlite");

        // Create the DB file if it doesn't exist
        if (!db.isFile()) {
            try {
                if (!db.createNewFile()) {
                    TwitchInteractions.logger.error("`" + db + "` could not be created");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DriverManager.getConnection("jdbc:sqlite:" + db);
    }
}
