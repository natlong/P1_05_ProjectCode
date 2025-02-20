package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EntityManager is responsible for managing and updating all game entities as per our UML diagram:
 * - Towers (placed by the player)
 * - Minions (enemy units moving through the path)
 * - Projectiles (fired by towers at minions)
 */


public class EntityManager{
	private List<AbstractEntity> entities;
	private OrthographicCamera camera;
	private Map map;
	
	private float spawnTimer = 0f;
    private float spawnInterval = 3f;
	
	public EntityManager(OrthographicCamera camera, Map map) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.map = map;
	}
	
	public void addEntity(AbstractEntity entity) {
		entities.add(entity);
	}
	
	public void removeEntity(AbstractEntity entity) {
		entities.remove(entity);
	}
	
	public void update(float delta) {
		spawnTimer += delta;
    	
    	if(spawnTimer>=spawnInterval) {
    		spawnTimer = 0f;
    		Vector2 spawnPos = new Vector2(map.getSpawnPoint().x, map.getSpawnPoint().y);
    		addEntity(new Minion("monster.png", 0.1f, spawnPos, map, 100f, camera, 200f));
    	}
    	
    	List<AbstractEntity> newProjectiles = new ArrayList<>();
        Iterator<AbstractEntity> iterator = entities.iterator();

        while (iterator.hasNext()) {
            AbstractEntity entity = iterator.next();

            if(entity instanceof Minion) {
            	Minion minion = (Minion) entity;
            	minion.update(delta);
            	
            	if (minion.isDead()) {
            		iterator.remove();
            		minion.dispose();
            		continue;
            	}
            }else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                projectile.movement();

                Vector2 pos = projectile.getPosition();
                if (pos.x < 0 || pos.x > GameCore.VIEWPORT_WIDTH || pos.y < 0 || pos.y > GameCore.VIEWPORT_HEIGHT) {
                    iterator.remove(); // Remove projectile safely
                    continue;
                }
            } else if (entity instanceof AbstractMovableObject) {
                ((AbstractMovableObject) entity).update(delta);
            } else if (entity instanceof Tower) {
                // Towers shoot projectiles, but we store them in a separate list
            	newProjectiles.addAll(((Tower) entity).shoot(entities, delta));
            }
        }

        // Add new projectiles after iteration
        entities.addAll(newProjectiles);

		CollisionManager.handleCollisions(entities);
	}
	
	public void render(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(AbstractEntity entity: entities) {
			entity.render(shapeRenderer);
		}
		shapeRenderer.end();
	}
	public List<AbstractEntity> getEntities(){
		return entities;
	}
}
