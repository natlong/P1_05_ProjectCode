package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Tower extends AbstractStaticObject{
	
	private Texture towerTexture;
	private SoundManager soundManager;
    
    public Tower(Vector2 position) {
    	super(new Vector2(position), "tower", 1.0f, 250f, 10f, 0f);
    	towerTexture = new Texture("tower.png");
    	this.soundManager = SoundManager.getInstance();
    }
    
  /**
  * Finds the targetable entity within range
  * @param entities List of all entities
  * @return The targetable entity, or null if none in range
  */
    private Targetable findUserTargetedFood(List<AbstractEntity> entities) {
        Targetable userTarget = null;
        float closestDistance = this.getRange();
        for (AbstractEntity entity : entities) {
            if (entity instanceof Food) {
                Food food = (Food) entity;
                if (food.isUserTargeted() && !food.isDead()) {
                    float distance = Vector2.dst(this.getPosition().x, this.getPosition().y,
                                                 food.getPosition().x, food.getPosition().y);
                    if (distance < closestDistance) {
                        userTarget = food;
                        closestDistance = distance;
                    }
                }
            }
        }
        return userTarget;
    }

  /**
  * Creates projectiles targeting nearby enemies
  * @param entities List of all entities
  * @param delta Time elapsed since last frame
  * @return List of newly created projectiles
  */
    public List<Projectile> shoot(List<AbstractEntity> entities, float delta) {
        List<Projectile> projectiles = new ArrayList<>();
        updateCooldown(delta);
        
        if (canFire()) {
            // Try to get a user-targeted food first.
            Targetable target = findUserTargetedFood(entities);
            if (target == null) {
                // Fallback to your default targeting (findTarget).
                target = findUserTargetedFood(entities);
            }
            
            if (target != null) {
                projectiles.add(new Projectile(getPosition(), target, this.getDamage()));
                
                if (soundManager != null) {
                    soundManager.playShootingSoundeffect(0.1f);
                }
                resetCooldown();
            }
        }
        return projectiles;
    }

    //Renders the tower using ShapeRenderer.
    public void render(ShapeRenderer shapeRenderer) {
       shapeRenderer.setColor(0, 0, 1, 1); // Blue color for tower
       shapeRenderer.rect(this.position.x - 15, this.position.y - 15, 0, 0);
        
    }
    
    //Rendering Tower Indicator using ShapeRenderer,
    public void renderRange(ShapeRenderer shapeRenderer) {
    	shapeRenderer.setColor(1, 1, 1, 0.25f);
    	shapeRenderer.circle(this.position.x, this.position.y, this.getRange());
    }
	
    //Renders the tower with its texture using SpriteBatch.
    public void render(SpriteBatch batch) {
        batch.draw(towerTexture, this.position.x - 15, this.position.y - 15, 50, 50); // Added from Tower 2.java
    }
    
    public void dispose() {
        if (towerTexture != null) {
            towerTexture.dispose();
        }
    }
}
