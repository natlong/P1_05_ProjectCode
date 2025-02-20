package io.github.some_example_name.lwjgl3;

//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Tower extends AbstractStaticObject{
    private float cooldown = 0;

    //public Tower(Vector2 position, Texture tex, String name, float fireRate, float range, float damage) {
    //	super (position, tex, name, fireRate, range, damage);
    //}
    public Tower(Vector2 position) {
    	super(new Vector2(position), "tower", 1.0f, 250f, 10f);
    }

    public List<Projectile> shoot(List<AbstractEntity> entities, float delta) {
        List<Projectile> projectiles = new ArrayList<>();

        if (cooldown <= 0) {
            Minion target = findTarget(entities);  // Find the closest minion
            if (target != null) {
                projectiles.add(new Projectile(position, target, this.getDamage())); // Fire projectile
                cooldown = this.getFireRate();
            }
        } else {
            cooldown -= delta;
        }

        return projectiles;  // Return the newly created projectiles
    }

    private Minion findTarget(List<AbstractEntity> entites) {
        Minion closest = null;
        float closestDistance = this.getRange();

        for (AbstractEntity entity : entites) {
        	if(entity instanceof Minion) {
        		Minion minion = (Minion) entity;
        		float distance = Vector2.dst(this.position.x, this.position.y, minion.getPosition().x, minion.getPosition().y);
                if (distance < closestDistance) {
                    closest = minion;
                    closestDistance = distance;
                }
        	}
            
        }
        return closest;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0, 0, 1, 1); // Blue color for tower
        shapeRenderer.rect(this.position.x - 15, this.position.y - 15, 30, 30);
    }
}
