package kaantelypeli.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import kaantelypeli.engine.Level;
import kaantelypeli.engine.Properties;
import kong.unirest.Unirest;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static kaantelypeli.ui.Game.SCALE;

/**
 * Static methods for file reading and JSON conversion.
 */
public class FileOperations {
    private FileOperations() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Load level JSON from file and parse it into a Level. If String is url load from address.
     * @param level Name of level to load
     * @return generated level
     */
    public static Level loadLevel(String level) {
        // Check if level is a URL
        if (level.contains("http")) {
            return Unirest.get(level).asObject(Level.class).getBody();
        } else {
            JsonElement json = loadJson("levels/" + level + ".json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final Level parsedLevel = gson.fromJson(json, Level.class);
            parsedLevel.levelIndex = Integer.parseInt(level);
            return parsedLevel;
        }
    }

    /**
     * Loads Properties from file and parse it into a Properties instance.
     * @param filename name of entity properties to load
     * @return generated properties
     */
    public static Properties loadProperties(String filename) {
        JsonElement json = loadJson("entities/" + filename + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, Properties.class);
    }
    
    /**
     * Loads JSON from a file containing valid JSON.
     * @param filename Path of file to load.
     * @return JsonElement containing parsed contents
     */
    public static JsonElement loadJson(String filename) {
        InputStream path = FileOperations.class.getClassLoader().getResourceAsStream(filename);
        if (path == null) {
            Logger.error("Entity: " + filename + " not found.");
        }
        try (InputStreamReader fr = new InputStreamReader(path)) {
            return JsonParser.parseReader(fr);
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }
    
    /**
     * Loads a sprite from a Filename.
     * @param filename name of sprite file to load.
     * @return a JavaFX Paint generated from loaded sprite or a green fill if not found.
     */
    public static Paint loadSprite(String filename) {
        URL spriteUrl = FileOperations.class.getClassLoader().getResource("sprites/" + filename);
        if (spriteUrl != null) {
            Image sprite = new Image(spriteUrl.toString());
            return new ImagePattern(sprite, 0, 0, 16 * SCALE, 16 * SCALE, false);
        } else {
            Logger.error("No sprite named: '" + filename + "' found");
            return Color.GREEN;
        }
    }

    /**
     * Upload level to target url.
     * @param target URL of fileserver to upload to
     * @param l Level to upload
     * @return Parsed HTTP response converted into a string
     */
    public static String uploadLevel(String target, Level l) {
        return Unirest.post(target)
                .header("Content-Type", "application/json")
                .body(l.getJson())
                .connectTimeout(4000)
                .asEmpty().toString();
    }

    /**
     * Get a File pointing to `resource` using parameter classes loader. Produces a NullPointerException if
     * resource can't be found.
     * @param resource Name of resource to load
     * @param clazz Class to get classloader from (usually the class where getClassResource gets called)
     * @return File pointing to the resource
     */
    public static File getClassResource(String resource, Class clazz) {
        return new File(clazz.getClassLoader().getResource(resource).getFile());
    }
}
