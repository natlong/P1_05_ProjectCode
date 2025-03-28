package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import config.GameConfig;

public class Projectile extends AbstractMovableObject {
	private Targetable target;
    private float damage;
    private Vector2 direction;

    public Projectile(Vector2 position, Targetable target, float damage) {
        // For projectiles, HP values are not relevant so we use dummy values (1, 1)
        // Speed is set to 200f.
        super(position, "Projectile", GameConfig.getInstance().getProjectileHp(), GameConfig.getInstance().getProjectileMaxHp(), GameConfig.getInstance().getProjectileSpeed());
        this.position = new Vector2(position);
        this.target = target;
        this.damage = damage;
        this.direction = new Vector2();
        // Initialize the direction toward the target if it exists.
        if (target != null) {
            this.direction.set(target.getPosition()).sub(getPosition()).nor();
        }
    }
    
    @Override
    public void update(float delta) {
    	movement();
    }
    
    @Override
    public void movement() {
    	// If there is a target...
    	if (target != null) {
    	    if (target.isDead() /* or no longer in the entity list */) {
    	        // either clear the target or set it to null
    	        target = null;
    	    } else {
    	        // continue homing
    	        direction.set(target.getPosition()).sub(getPosition()).nor();
    	    }
    	}
        // Otherwise, keep the last computed direction
        Vector2 velocity = new Vector2(direction).scl(getSpeed() * Gdx.graphics.getDeltaTime());
        Vector2 newPos = new Vector2(getPosition()).add(velocity);
        setPosition(newPos);
    }
    
    public float getDamage() {
        return damage;
    }
    
    public Vector2 getTargetPosition() {
        return target != null ? target.getPosition() : null;
    }
    
    /**
     * Checks if the target is still valid (not null and not dead)
     * @return true if the target is valid, false otherwise
     */
    public boolean hasValidTarget() {
        return target != null && !target.isDead();
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for projectiles
        shapeRenderer.circle(position.x, position.y, 5);
    }
}
