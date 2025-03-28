package io.github.some_example_name.lwjgl3;

//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractMovableObject extends AbstractEntity implements MovementManager{
	private float speed;
	private float hp;
	private float maxHp;
	
	public AbstractMovableObject(Vector2 position, String name, float hp, float maxHp, float speed) {
		super(position, name);
		this.speed = speed;
		this.hp = hp;
		this.maxHp = maxHp;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getHp() {
		 return this.hp;
	 }
	 
	 public void setHp(float hp) {
		 this.hp = hp;
	 }
	 
	 public float getMaxHp() {
		 return this.maxHp;
	 }
	 
	 public void setMaxHp(float maxHp) {
		 this.maxHp = maxHp;
	 }

	 public void update(float delta) {
		 movement();
	 }

	 public abstract void movement();
}