package uk.co.evanward.twitchinteractions.config;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;

import java.io.IOException;
import java.nio.file.Paths;

public class ModConfig
{
    private static final String CONFIG_FILE_PATH = "config/" + TwitchInteractions.MOD_ID + "/config.json";

    public static String USER_ACCESS_TOKEN;
    public static String BROADCASTER_ID;

    public static void loadConfig()
    {
        JSONObject config = FileHelper.readJsonFile(Paths.get(CONFIG_FILE_PATH));

        USER_ACCESS_TOKEN = config.has("user_access_token") ? config.getString("user_access_token") : "";
        BROADCASTER_ID = config.has("broadcaster_id") ? config.getString("broadcaster_id") : "";
    }

    public static void saveConfig() throws IOException {
        JSONObject config = new JSONObject();

        config.put("user_access_token", USER_ACCESS_TOKEN);
        config.put("broadcaster_id", BROADCASTER_ID);

        FileHelper.writeJsonFile(config, Paths.get(CONFIG_FILE_PATH));
    }
}
