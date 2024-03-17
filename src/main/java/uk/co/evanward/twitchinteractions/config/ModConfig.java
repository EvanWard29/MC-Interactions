package uk.co.evanward.twitchinteractions.config;

import org.json.JSONObject;
import uk.co.evanward.twitchinteractions.TwitchInteractions;
import uk.co.evanward.twitchinteractions.helpers.FileHelper;
import uk.co.evanward.twitchinteractions.twitch.event.TwitchEvent;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModConfig
{
    private static final String CONFIG_FILE_PATH = "config/" + TwitchInteractions.MOD_ID + "/config.json";

    public static String USER_ACCESS_TOKEN;
    public static String BROADCASTER_ID;
    public static List<TwitchEvent.TwitchEventInterface> TWITCH_EVENTS;

    public static void loadConfig()
    {
        JSONObject config = FileHelper.readJsonFile(Paths.get(CONFIG_FILE_PATH));

        USER_ACCESS_TOKEN = config.has("user_access_token") ? config.getString("user_access_token") : "";
        BROADCASTER_ID = config.has("broadcaster_id") ? config.getString("broadcaster_id") : "";
        TWITCH_EVENTS = config.has("twitch_events") ? getTwitchEvents(config)  : new ArrayList<>();
    }

    public static void saveConfig() throws IOException {
        JSONObject config = new JSONObject();

        config.put("user_access_token", USER_ACCESS_TOKEN);
        config.put("broadcaster_id", BROADCASTER_ID);

        FileHelper.writeJsonFile(config, Paths.get(CONFIG_FILE_PATH));
    }

    private static List<TwitchEvent.TwitchEventInterface> getTwitchEvents(JSONObject config)
    {
        List<TwitchEvent.TwitchEventInterface> twitchEvents = new ArrayList<>();

        for (Object twitchEvent : config.getJSONArray("twitch_events")) {
            twitchEvents.add(new TwitchEvent(TwitchEvent.Type.from(twitchEvent.toString())).getEvent());
        }

        return twitchEvents;
    }
}
