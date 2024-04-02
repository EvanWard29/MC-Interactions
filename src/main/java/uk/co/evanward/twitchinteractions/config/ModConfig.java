package uk.co.evanward.twitchinteractions.config;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.EnumSet;

public class ModConfig
{
    private static final String CONFIG_FILE_PATH = "config/" + TwitchInteractions.MOD_ID + "/config.json";

    public static String USER_ACCESS_TOKEN;
    public static String BROADCASTER_ID;
    public static EnumSet<TwitchEvent.Type> TWITCH_EVENTS;

    /**
     * Load the mod config file
     */
    public static void loadConfig()
    {
        JSONObject config = FileHelper.readJsonFile(Paths.get(CONFIG_FILE_PATH));

        USER_ACCESS_TOKEN = config.has("user_access_token") ? config.getString("user_access_token") : "";
        BROADCASTER_ID = config.has("broadcaster_id") ? config.getString("broadcaster_id") : "";
        TWITCH_EVENTS = config.has("twitch_events") ? getTwitchEvents(config.getJSONObject("twitch_events"))  : EnumSet.allOf(TwitchEvent.Type.class);

        try {
            saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the mod config file
     */
    public static void saveConfig() throws IOException {
        JSONObject config = new JSONObject();

        config.put("user_access_token", USER_ACCESS_TOKEN);
        config.put("broadcaster_id", BROADCASTER_ID);

        JSONObject twitchEvents = new JSONObject();
        for (TwitchEvent.Type type : TwitchEvent.Type.values()) {
            twitchEvents.put(type.getString(), TWITCH_EVENTS.contains(type));
        }

        config.put("twitch_events", twitchEvents);

        FileHelper.writeJsonFile(config, Paths.get(CONFIG_FILE_PATH));
    }

    /**
     * Get the enabled twitch events
     */
    private static EnumSet<TwitchEvent.Type> getTwitchEvents(JSONObject config)
    {
        EnumSet<TwitchEvent.Type> twitchEvents = EnumSet.noneOf(TwitchEvent.Type.class);

        for (TwitchEvent.Type type : TwitchEvent.Type.values()) {
            if (config.has(type.getString()) && config.getBoolean(type.getString())) {
                // Only enable event if `true`
                twitchEvents.add(type);
            }
        }

        return twitchEvents;
    }
}
