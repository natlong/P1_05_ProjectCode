package io.github.some_example_name.lwjgl3;

//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Tower extends AbstractStaticObject{
    
    public Tower(Vector2 position) {
    	super(new Vector2(position), "tower", 1.0f, 250f, 10f, 0f);
    }

    /**
     * Finds the closest targetable entity within range
     * @param entities List of all entities
     * @return The closest targetable entity, or null if none in range
     */
    private Targetable findTarget(List<AbstractEntity> entities) {
        Targetable closest = null;
        float closestDistance = this.getRange();

        for (AbstractEntity entity : entities) {
            if (entity instanceof Targetable) {
                Targetable target = (Targetable) entity;
                
                // Skip dead targets
                if (target.isDead()) continue;
                
                Vector2 targetPos = target.getPosition();
                float distance = Vector2.dst(
                    this.getPosition().x, 
                    this.getPosition().y, 
                    targetPos.x, 
                    targetPos.y
                );
                
                if (distance < closestDistance) {
                    closest = target;
                    closestDistance = distance;
                }
            }
        }
        
        return closest;
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
            Targetable target = findTarget(entities);
            if (target != null) {
                projectiles.add(new Projectile(getPosition(), target, this.getDamage()));
                resetCooldown();
            }
        }

        return projectiles;
    }


    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0, 0, 1, 1); // Blue color for tower
        shapeRenderer.rect(this.position.x - 15, this.position.y - 15, 30, 30);
    }
}
