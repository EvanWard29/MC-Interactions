package uk.co.evanward.twitchinteractions.twitch.server;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;

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
        // Check if Twitch has been authenticated first
        if (!TwitchHelper.authenticated()) {
            TwitchInteractions.logger.error("User has not authenticated with Twitch");

            return;
        }

        try {
            Connection connection = connection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            // Create tables if they don't already exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS followers (id STRING PRIMARY KEY, user_login STRING, user_name STRING, followed_at DATETIME)");

            statement.close();
            connection.close();

            Thread thread = new Thread(() -> {
                try {
                    Connection databaseConnection = connection();
                    databaseConnection.setAutoCommit(false);

                    Statement sqlStatement = databaseConnection.createStatement();

                    // Populate the followers table
                    JSONObject pagination = new JSONObject();
                    do {
                        String after = pagination.has("cursor") ? pagination.getString("cursor") : "";
                        JSONObject followers = TwitchHelper.getFollowerList(after);

                        for (int i = 0; i < followers.getJSONArray("data").length(); i++) {
                            JSONObject user = followers.getJSONArray("data").getJSONObject(i);

                            String userId = user.getString("user_id");
                            String userLogin = user.getString("user_login");
                            String userName = user.getString("user_name");
                            String followedAt = user.getString("followed_at");

                            // Insert new follower or update the display name of an existing follower
                            String query = "INSERT INTO followers (id,user_login,user_name,followed_at)"
                                + " VALUES(\"" + userId + "\",\"" + userLogin + "\",\"" + userName + "\",\"" + followedAt + "\")"
                                + "ON CONFLICT (id)"
                                + "DO UPDATE SET user_name = \"" + userName + "\"";

                            try {
                                sqlStatement.executeUpdate(query);
                            } catch (SQLException e) {
                                TwitchInteractions.logger.error("Failed to add follower `" + userLogin + "` to DB: " + e.getMessage());
                            }
                        }

                        pagination = followers.getJSONObject("pagination");
                    } while (!pagination.isEmpty());

                    sqlStatement.close();
                    databaseConnection.commit();

                    databaseConnection.setAutoCommit(true);
                    sqlStatement = databaseConnection.createStatement();

                    if (sqlStatement.execute("SELECT COUNT(*) FROM followers")) {
                        TwitchInteractions.logger.info("Added `" + sqlStatement.getResultSet().getInt(1) + "` followers to DB");
                    }

                    sqlStatement.close();
                    databaseConnection.close();
                } catch (Exception e) {
                    TwitchInteractions.logger.error("Error adding followers to DB: " + e.getMessage());
                }
            });

            thread.start();

        } catch (Exception e) {
            TwitchInteractions.logger.error(e.getMessage());
        }
    }

    /**
     * Get a connection to the SQLite database
     */
    public static Connection connection() throws SQLException
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
