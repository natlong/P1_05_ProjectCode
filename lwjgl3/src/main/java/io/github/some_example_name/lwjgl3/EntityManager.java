package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityManager {
    private List<AbstractEntity> entities;
    private OrthographicCamera camera;
    private Map map;
    private float spawnTimer = 0f;
    private float spawnInterval; // Now a variable, not a fixed value
    private float minSpawnInterval = 0.5f; // Minimum spawn interval (e.g., 0.5 seconds)
    private float maxSpawnInterval = 3f; // Maximum spawn interval (e.g., 3 seconds)
    private Random random = new Random();

    public EntityManager(OrthographicCamera camera, Map map) {
        this.entities = new ArrayList<>();
        this.camera = camera;
        this.map = map;
        setRandomSpawnInterval(); // Set the initial random spawn interval
    }

    private void setRandomSpawnInterval() {
        spawnInterval = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
    }

    public void addEntity(AbstractEntity entity) {
        entities.add(entity);
    }

    public void removeEntity(AbstractEntity entity) {
        Iterator<AbstractEntity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            AbstractEntity entityIterated = iterator.next();
            if (entityIterated instanceof Tower) {
                Tower tower = (Tower) entityIterated;
                if (tower.getPosition().dst(entity.position) < 15) {
                    iterator.remove();
                    break;
                }
            } else if (entityIterated.getClass().equals(entity.getClass())) {
                iterator.remove();
                break;
            }
        }
    }

    public void update(float delta) {
        spawnTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            setRandomSpawnInterval(); // Set a new random spawn interval
            Vector2 spawnPos = new Vector2(map.getSpawnPoint().x, map.getSpawnPoint().y);

            // Randomly select a FoodType
            Minion.FoodType[] foodTypes = Minion.FoodType.values();
            Minion.FoodType randomFood = foodTypes[random.nextInt(foodTypes.length)];

            // Create a Minion with the selected FoodType
            addEntity(Minion.createMinion(randomFood, spawnPos, map, camera, 200f));
        }

        List<AbstractEntity> newProjectiles = new ArrayList<>();
        Iterator<AbstractEntity> iterator = entities.iterator();

        while (iterator.hasNext()) {
            AbstractEntity entity = iterator.next();

            if (entity instanceof Minion) {
                Minion minion = (Minion) entity;
                minion.update(delta);

                if (minion.isDead()) {
                    iterator.remove();
                    minion.dispose();
                    continue;
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
        List<AbstractEntity> collisionsToRemove = CollisionManager.handleCollisions(entities);
        entities.removeAll(collisionsToRemove);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (AbstractEntity entity : entities) {
            entity.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public List<AbstractEntity> getEntities() {
        return new ArrayList<>(entities);
    }
}