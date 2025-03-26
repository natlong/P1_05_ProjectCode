package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import config.GameConfig;

import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;


public class FoodFactory {	
	private static JsonObject readConfigFile(String configPath) {
		JsonObject config = null;
		try(InputStream inputStream = FoodFactory.class.getClassLoader().getResourceAsStream(configPath)){
			if(inputStream == null) {
				System.out.println("Error: Resource '" + configPath + "' not found.");
                return null;
			}
			Gson gson = new Gson();
			config = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static List<String> getFoodNames(String configPath){
		List<String> foodNames = new ArrayList<>();
		
		JsonObject config = readConfigFile(configPath);
		JsonArray foods = config.getAsJsonArray("foods");
		
		for (int i=0;i<foods.size();i++) {
			JsonObject foodObj = foods.get(i).getAsJsonObject();
			String name = foodObj.get("name").getAsString();
			foodNames.add(name);
		}
		return foodNames;
	}
	
    
	public static Food createFood(String configPath, String foodName, GameConfig gameConfig, Map map, OrthographicCamera camera){
		Food food = null;
		
		JsonObject config = readConfigFile(configPath);
		
		JsonArray foods = config.getAsJsonArray("foods");
		
		for (int i = 0; i<foods.size();i++) {
			JsonObject foodObject = foods.get(i).getAsJsonObject();
			String name = foodObject.get("name").getAsString();
			if(name.equals(foodName)) {
				String texturePath = foodObject.get("texturePath").getAsString();
				boolean isBadFood = foodObject.get("isBadFood").getAsBoolean();
				
	            Vector2 spawnPos = new Vector2(map.getSpawnPoint().x, map.getSpawnPoint().y);
	            food = new Food(texturePath, name, spawnPos, map, GameConfig.getInstance().getMaxHp(), camera, GameConfig.getInstance().getFoodSpeed(), isBadFood);
	            break;
			}
		}
            
		
		return food;
	}
}
