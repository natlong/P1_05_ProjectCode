package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;

/**
 * EntityManager is responsible for managing and updating all game entities as per our UML diagram:
 * - Towers (placed by the player)
 * - Minions (enemy units moving through the path)
 * - Projectiles (fired by towers at minions)
 */

public class EntityManager {
    private List<Tower> towers;
    private List<Minion> minions;
    private List<Projectile> projectiles;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f; 
    private OrthographicCamera camera;
    private Map map;

    public EntityManager(Map map, OrthographicCamera camera) {
        this.towers = new ArrayList<>();
        this.minions = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.camera = camera;
        this.map = map;
    }

    public void addTower(Vector2 position) {
        towers.add(new Tower(new Vector2(position)));
    }

    public void spawnMinion(String spritePath, float frameDuration, Vector2 position, Map map, float maxHP, OrthographicCamera camera) {
        minions.add(new Minion(spritePath, frameDuration, position, map, maxHP, camera, 200f));
    }

    // Updates all game entities (minions, towers, and projectiles)
    public void update(float delta) {
    	spawnTimer += delta;
    	
    	if(spawnTimer>=spawnInterval) {
    		spawnTimer = 0f;
    		
    		if(map!=null) {
    			Rectangle spawnArea = map.getSpawnPoint();
        		float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
        		float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
        		Vector2 position = new Vector2(spawnX, spawnY);
        		
        		minions.add(new Minion("monster.png", 0.1f, position, map, 100f, camera, 200f));
    		}else {
                System.out.println("⚠️ Map is null! Cannot spawn minions.");
            }
    		
    		
    	}
        for (Minion minion : minions) {
            minion.update(delta);
        }

        // Towers shoot at minions (generates new projectiles)
        for (Tower tower : towers) {
            tower.shoot(projectiles, minions, delta);
        }

     // Move projectiles and remove any that reach the minion or go off-screen.
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.movement();
            Vector2 pos = projectile.getPosition();
            if (pos.x < 0 || pos.x > GameCore.VIEWPORT_WIDTH || pos.y < 0 || pos.y > GameCore.VIEWPORT_HEIGHT) {
                iterator.remove();
            }
        }
        
        // Handle projectile-minion collisions.
        CollisionManager.projectileMinionCollision(projectiles, minions);
        
        // Remove dead minions.
        Iterator<Minion> minionIterator = minions.iterator();
        while (minionIterator.hasNext()) {
            Minion minion = minionIterator.next();
            if (minion.isDead()) {
                minion.dispose();
                minionIterator.remove();
            }
        }
    }
    
    //Remove Existing Tower,
    public void removeTower(Vector2 position) {
        Iterator<Tower> iterator = towers.iterator();
        
        while (iterator.hasNext()) {
            Tower tower = iterator.next();
            
            //Check if there is a Tower near User Mouse,
            if (tower.getPosition().dst(position) < 15) {  //Radius of Mouse,
                iterator.remove(); 
                return; //Break after Deleting,
            }
        }
    }

    
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        //Enable Blending for Transparent Tower Range,
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        //Render Minion Entity,
        for (Minion minion : minions) {
            minion.draw(shapeRenderer);
        }

        //Render Tower Projectile Entity,
        for (Projectile projectile : projectiles) {
            projectile.draw(shapeRenderer);
        }
        
        shapeRenderer.end();

        //Render Tower Entity (Must be Outside of ShapeRenderer for Blending),
        for (Tower tower : towers) {
            tower.draw(shapeRenderer);
        }

        //Disable Blending to Prevent Issue,
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        for (Minion minion : minions) {
            minion.dispose();
        }
    }
}