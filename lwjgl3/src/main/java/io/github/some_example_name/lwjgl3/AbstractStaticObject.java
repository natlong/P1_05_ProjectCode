package io.github.some_example_name.lwjgl3;

//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractStaticObject extends AbstractEntity{

	private float range;
	private float damage;
	private float fireRate;
	private float cooldown = 0;
	
	//public AbstractStaticObject(Vector2 position, Texture tex, String name, float fireRate, float range, float damage) {
	//	super(position, tex, name);
	//	this.range = range;
	//	this.damage = damage;
	//	this.fireRate = fireRate;
	//}
	public AbstractStaticObject(Vector2 position, String name, float fireRate, float range, float damage, float cooldown) {
		super(new Vector2 (position), name);
		this.range = range;
		this.damage = damage;
		this.fireRate = fireRate;
		this.cooldown = cooldown;
	}
	
	public float getDamage() {
		return this.damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public float getRange() {
		return this.range;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	 
	 public float getFireRate() {
		 return this.fireRate;
	 }
	 
	 public void setFireRate(float fireRate) {
		 this.fireRate = fireRate;
	 }
	 
	 public float getCooldown() {
		 return this.cooldown;
	 }
	 
	 public void setCooldown(float cooldown) {
		 this.cooldown = cooldown;
	 }
	 
	 /**
	     * Updates the cooldown by subtracting the specified delta time
	     * @param delta The time elapsed since the last update
	     */
	    protected void updateCooldown(float delta) {
	        if (cooldown > 0) {
	            cooldown = Math.max(0, cooldown - delta);
	        }
	    }
	    
	    /**
	     * Checks if the object is ready to fire
	     * @return true if cooldown is 0, false otherwise
	     */
	    protected boolean canFire() {
	        return cooldown <= 0;
	    }
	    
	    /**
	     * Resets the cooldown to the fire rate after firing
	     */
	    protected void resetCooldown() {
	        cooldown = fireRate;
	    }
}
