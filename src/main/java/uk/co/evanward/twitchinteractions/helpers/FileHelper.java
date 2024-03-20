package uk.co.evanward.twitchinteractions.helpers;

import net.minecraft.util.WorldSavePath;
import org.json.JSONObject;
import org.json.JSONTokener;
import uk.co.evanward.twitchinteractions.TwitchInteractions;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper
{
    public static Path WORLD_DATA_FOLDER_PATH;

    /**
     * Read the JSON contents of from the given file path
     *
     * @return A JSON object of the given file
     */
    public static JSONObject readJsonFile(Path path)
    {
        try {
            // Attempt to read and return json file contents
            return new JSONObject(new JSONTokener(new FileReader(path.toFile())));
        } catch (FileNotFoundException e) {
            // Returns an empty json object if file does not exist
            return new JSONObject();
        }
    }

    /**
     * Write a JSON object to the given file path

     * @throws IOException Error writing to file
     */
    public static void writeJsonFile(JSONObject json, Path path) throws IOException
    {
        File file = path.toFile();
        if (!file.exists()) {
            // If the directory doesn't exist, make it
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                // The directories could not be created
                TwitchInteractions.logger.error("Directories for path `" + path.toAbsolutePath() + "' could not be created");
            }

            // Create the new file
            if (!file.createNewFile()) {
                // File could not be created
                TwitchInteractions.logger.error("File for path `" + path.toAbsolutePath() + "' could not be created");
            }
        }

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(json.toString(4));
        fileWriter.close();
    }

    /**
     * Load world specific file paths
     */
    public static void loadPaths()
    {
        WORLD_DATA_FOLDER_PATH = Paths.get("saves/" + ServerHelper.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString() + "/data");
    }
}
