package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.Food.FoodType;

public class FoodFactory {
	public static Food createFood(FoodType foodType, Vector2 position, Map map, float maxHp, OrthographicCamera camera, float speed) {
			return new Food(foodType.getTexturePath(), position, map, maxHp, camera, speed, foodType.isBadFood());
    }
}
