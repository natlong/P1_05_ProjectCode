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
    public float getTowerFireRate() {
		if(config.has("tower")&&config.getAsJsonObject("tower").has("fireRate")) {
    		return config.getAsJsonObject("tower").get("fireRate").getAsFloat();
    	}
		return 0f;
	}

	public float getTowerRange() {
		if(config.has("tower")&&config.getAsJsonObject("tower").has("range")) {
    		return config.getAsJsonObject("tower").get("range").getAsFloat();
    	}
		return 0f;
	}

	public float getTowerDamage() {
		if(config.has("tower")&&config.getAsJsonObject("tower").has("damage")) {
    		return config.getAsJsonObject("tower").get("damage").getAsFloat();
    	}
		return 0f;
	}

	public float getTowerCooldown() {
		if(config.has("tower")&&config.getAsJsonObject("tower").has("cooldown")) {
    		return config.getAsJsonObject("tower").get("cooldown").getAsFloat();
    	}
		return 0f;
	}

	public float getProjectileSpeed() {
		if(config.has("projectile")&&config.getAsJsonObject("projectile").has("speed")) {
    		return config.getAsJsonObject("projectile").get("speed").getAsFloat();
    	}
		return 0f;
	}

	public float getProjectileMaxHp() {
		if(config.has("projectile")&&config.getAsJsonObject("projectile").has("maxHp")) {
    		return config.getAsJsonObject("projectile").get("maxHp").getAsFloat();
    	}
		return 0f;
	}

	public float getProjectileHp() {
		if(config.has("projectile")&&config.getAsJsonObject("projectile").has("hp")) {
    		return config.getAsJsonObject("projectile").get("hp").getAsFloat();
    	}
		return 0f;
	}

	public int getButtonSize() {
		if(config.has("optionsScene")&&config.getAsJsonObject("optionsScene").has("button_size")) {
    		return config.getAsJsonObject("optionsScene").get("button_size").getAsInt();
    	}
		return 0;
	}

	public int getButtonWidth() {
		if(config.has("mainMenuScene")&&config.getAsJsonObject("mainMenuScene").has("button_width")) {
    		return config.getAsJsonObject("mainMenuScene").get("button_width").getAsInt();
    	}
		return 0;
	}

	public int getButtonHeight() {
		if(config.has("mainMenuScene")&&config.getAsJsonObject("mainMenuScene").has("button_height")) {
    		return config.getAsJsonObject("mainMenuScene").get("button_height").getAsInt();
    	}
		return 0;
	}

	public int getButtonPad() {
		if(config.has("mainMenuScene")&&config.getAsJsonObject("mainMenuScene").has("button_pad")) {
    		return config.getAsJsonObject("mainMenuScene").get("button_pad").getAsInt();
    	}
		return 0;
	}

	public int getPlayerHp() {
		if(config.has("player")&&config.getAsJsonObject("player").has("playerHp")) {
			return config.getAsJsonObject("player").get("playerHp").getAsInt();
		}
		return 0;
	}

	public int getPlayerCoins() {
		if(config.has("player")&&config.getAsJsonObject("player").has("playerCoins")) {
			return config.getAsJsonObject("player").get("playerCoins").getAsInt();
		}
		return 0;
	}

	public float getTowerRemovalRadius() {
		if(config.has("tower")&&config.getAsJsonObject("tower").has("removal_radius")) {
    		return config.getAsJsonObject("tower").get("removal_radius").getAsFloat();
    	}
		return 0f;
	}

	public float getIdleFrameDuration() {
		if(config.has("creature")&&config.getAsJsonObject("creature").has("idle_frame_duration")) {
    		return config.getAsJsonObject("creature").get("idle_frame_duration").getAsFloat();
    	}
		return 0f;
	}

	public float getEatingFrameDuration() {
		if(config.has("creature")&&config.getAsJsonObject("creature").has("eating_frame_duration")) {
    		return config.getAsJsonObject("creature").get("eating_frame_duration").getAsFloat();
    	}
		return 0f;
	}

	public int getFrameWidth() {
		if(config.has("creature")&&config.getAsJsonObject("creature").has("frame_width")) {
    		return config.getAsJsonObject("creature").get("frame_width").getAsInt();
    	}
		return 0;
	}

	public int getFrameHeight() {
		if(config.has("creature")&&config.getAsJsonObject("creature").has("frame_height")) {
    		return config.getAsJsonObject("creature").get("frame_height").getAsInt();
    	}
		return 0;
	}

	public float getEatingDuration() {
		if(config.has("creature")&&config.getAsJsonObject("creature").has("eating_duration")) {
    		return config.getAsJsonObject("creature").get("eating_duration").getAsFloat();
    	}
		return 0f;
	}

	public int getViewportWidth() {
		if(config.has("viewport")&&config.getAsJsonObject("viewport").has("viewport_width")) {
			return config.getAsJsonObject("viewport").get("viewport_width").getAsInt();
		}
		return 0;
	}

	public int getViewportHeight() {
		if(config.has("viewport")&&config.getAsJsonObject("viewport").has("viewport_height")) {
			return config.getAsJsonObject("viewport").get("viewport_height").getAsInt();
		}
		return 0;
	}
}
