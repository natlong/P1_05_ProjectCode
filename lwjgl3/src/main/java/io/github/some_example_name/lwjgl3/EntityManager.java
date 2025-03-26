package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;

import config.GameConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityManager {
	private SoundManager soundManager; 
    private List<AbstractEntity> entities;
    private OrthographicCamera camera;
    private GameConfig gameConfig;
    private Map map;
    private float spawnTimer = 0f;
    private float spawnInterval; // Now a variable, not a fixed value
    private Random random = new Random();
    private List<String> foodNameList;
	private static final String CONFIG_FILE_PATH = "config/food.json";

    
    //Level-Food Configuration,
    private LevelChangeListener levelChangeListener;
    public  int currentLevel;
    private int foodsPerLevel = 3;
    private int foodsSpawnedCurrLevel = 0;
    private int foodEatenCurrLevel = 0;
    private boolean levelInProgress = false;

    public EntityManager(GameConfig gameConfig, OrthographicCamera camera, Map map) {
        this.entities = new ArrayList<>();
        this.camera = camera;
        this.map = map;
        this.soundManager = soundManager.getInstance();
        this.gameConfig = gameConfig;
        this.foodNameList = new ArrayList<>();
        
        loadFoodEntities();
        
        setRandomSpawnInterval(); // Set the initial random spawn interval

    }

    private void setRandomSpawnInterval() {
        spawnInterval = gameConfig.getSpawnMinInterval() + random.nextFloat() * (gameConfig.getSpawnMaxInterval() - gameConfig.getSpawnMinInterval());
    }
    
    private void loadFoodEntities() {
        System.out.println("Loading Food Entities");

    	this.foodNameList = FoodFactory.getFoodNames(CONFIG_FILE_PATH);
    }

    public void addEntity(AbstractEntity entity) {
        if (entity instanceof Tower && soundManager != null) {
            Tower tower = new Tower(entity.getPosition());
            entities.add(tower);
        } else {
            entities.add(entity);
        }
    }

    public boolean removeEntity(AbstractEntity entity) {
        Iterator<AbstractEntity> iterator = entities.iterator();
        
        while (iterator.hasNext()) {
            AbstractEntity entityIterated = iterator.next();
            
            if (entityIterated instanceof Tower) {
                Tower tower = (Tower) entityIterated;
                
                if (tower.getPosition().dst(entity.position) < 15) {
                    iterator.remove();
                    return true;
                }
            } else if (entityIterated.getClass().equals(entity.getClass())) {
                iterator.remove();

                return true;
            }
        }
        
        return false;
    }

    //Updated Function for Each Level,
    public void update(float delta) {
    	
    	if (!levelInProgress) {
    		startNextLevel();
    		return;
    	}
    	
    	//Spawn Food within Range of Level,
    	if (foodsSpawnedCurrLevel < foodsPerLevel) {
            spawnTimer += delta;
            
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0f;
                setRandomSpawnInterval(); // Set a new random spawn interval
            	String nameOfRandomFood = foodNameList.get(random.nextInt(foodNameList.size()));
            	Food food = FoodFactory.createFood(CONFIG_FILE_PATH, nameOfRandomFood, gameConfig, map, camera);
                
                //Food.FoodType randomFood = Food.FoodType.values()[random.nextInt(Food.FoodType.values().length)];
               
                // Create a Food with the selected FoodType
                addEntity(food);
                foodsSpawnedCurrLevel++;
            }
    	}
    	
    	//Track Killed Food,
        List<AbstractEntity> newProjectiles = new ArrayList<>();
        Iterator<AbstractEntity> iterator = entities.iterator();

        while (iterator.hasNext()) {
            AbstractEntity entity = iterator.next();

            if (entity instanceof Food) {
                Food food = (Food) entity;
                food.update(delta);

                if (food.isDead()) {
                    iterator.remove();
                    food.dispose();
                    foodEatenCurrLevel++;
                    
                    if (foodEatenCurrLevel >= foodsPerLevel) {
                    	levelInProgress = false;
                    	
                    }
                }
                
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                projectile.movement();

                Vector2 pos = projectile.getPosition();
                if (pos.x < 0 || pos.x > GameCore.VIEWPORT_WIDTH || pos.y < 0 || pos.y > GameCore.VIEWPORT_HEIGHT) {
                    iterator.remove();
                    continue;
                }
            } else if (entity instanceof AbstractMovableObject) {
                ((AbstractMovableObject) entity).update(delta);
            } else if (entity instanceof Tower) {
                newProjectiles.addAll(((Tower) entity).shoot(entities, delta));
            }
        }

        entities.addAll(newProjectiles);

        // Use the fixed CollisionManager that returns entities to remove
        List<AbstractEntity> collisionsToRemove = CollisionManager.handleProjectileCollisions(entities);
        entities.removeAll(collisionsToRemove);
    }
    	
    //Next Level Configurations,
    public void startNextLevel() {
    	currentLevel++;
    	foodsPerLevel += 2;
        foodsSpawnedCurrLevel = 0;
        foodEatenCurrLevel = 0;
        levelInProgress = true;
        setRandomSpawnInterval();
        
        if (levelChangeListener != null) {
        	levelChangeListener.onLevelChanged(currentLevel);
        }
    }
    
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
    
    //Listener for Level Change,
    public interface LevelChangeListener {
    	void onLevelChanged(int newLevel);
    }
    
    public void setLevelChangeListener(LevelChangeListener x) {
    	this.levelChangeListener = x;
    }
    
    //Allowing Creature Eats as Food Killed,
    public void foodEaten(Food x) {
    	if (x != null) {
    		foodEatenCurrLevel++;
    		entities.remove(x);
    		x.dispose();
    		
    		if (foodEatenCurrLevel >= foodsPerLevel) {
    			levelInProgress = false;
    		}
    	}
    }
    
    public void render(ShapeRenderer shapeRenderer) {
    	//Enabling Blending for Tower Indicator,
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, 
        com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        //Rendering Tower Indicator,
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (AbstractEntity entity : entities) {
        	if (entity instanceof Tower) {
        		Tower tower = (Tower) entity;
        		tower.renderRange(shapeRenderer);
        	}
        }
        
        //Disable Blending once Done,
        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        
        //Rendering Tower Object,
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (AbstractEntity entity : entities) {
            entity.render(shapeRenderer);
        }
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (AbstractEntity entity : entities) {
            if (entity instanceof Food) {
                ((Food) entity).renderOutline(shapeRenderer);
            }
        }
        shapeRenderer.end();
    }
    
    public void render(SpriteBatch batch) {
        batch.begin();
        for (AbstractEntity entity : entities) {
            if (entity instanceof Tower) {
                ((Tower) entity).render(batch);
            } else if (entity instanceof Food || entity instanceof Projectile) {
                entity.render(batch);
            }
        }
        batch.end();
    }

    public List<AbstractEntity> getEntities() {
        return new ArrayList<>(entities);
    }
}