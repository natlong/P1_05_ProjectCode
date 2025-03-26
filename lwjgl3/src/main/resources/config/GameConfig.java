package config;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.Reader;

public class GameConfig {
	private static GameConfig instance;
	private JsonObject config;
	private static final String CONFIG_FILE_PATH = "config/stats_config.json";
	private final Gson gson;
	
	private GameConfig() {
		gson = new Gson();
	}
	
	public static GameConfig getInstance() {
		if(instance == null) {
			instance = new GameConfig();
		}
		Gdx.app.log("GameConfig","return instance");
		return instance;
	}
	
    public void loadConfig() {
        String configFilePath = String.format(CONFIG_FILE_PATH);  // Build file path for the current level
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(configFilePath))) {
            config = gson.fromJson(reader, JsonObject.class);  // Parse JSON into JsonObject
            System.out.println("Stats loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading configuration for stats" + e.getMessage());
            config = new JsonObject();  // Use an empty config as fallback
        }
    }
    
 // Getter for spawn interval
    public float getSpawnMinInterval() {
        if (config.has("spawn") && config.getAsJsonObject("spawn").has("minInterval")) {
            return config.getAsJsonObject("spawn").get("minInterval").getAsFloat();
        }
        return 1f;  // Default value
    }

    public float getSpawnMaxInterval() {
        if (config.has("spawn") && config.getAsJsonObject("spawn").has("maxInterval")) {
            return config.getAsJsonObject("spawn").get("maxInterval").getAsFloat();
        }
        return 3f;  // Default value
    }
    
    //Getter for food attributes
    public float getFoodSpeed() {
    	if(config.has("food")&&config.getAsJsonObject("food").has("foodSpeed")) {
    		return config.getAsJsonObject("food").get("foodSpeed").getAsFloat();
    	}
    	return 200f;
    }
    
    public float getMaxHp() {
    	if(config.has("food")&&config.getAsJsonObject("food").has("maxHp")) {
    		return config.getAsJsonObject("food").get("maxHp").getAsFloat();
    	}
    	return 100f;
    }
    
    public int getNumOfFood() {
    	if(config.has("food")&&config.getAsJsonObject("food").has("numOfFood")) {
    		return config.getAsJsonObject("food").get("numOfFood").getAsInt();
    	}
    	return 20;
    }
}
