package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractEntity{
	protected Vector2 position;
	//private Texture tex;
	private String name;
	
	
	//public AbstractEntity(Vector2 position, Texture tex, String name) {
	//	this.position = position;
	//	this.tex = tex;
	//	this.name = name;
	//}
	
	public AbstractEntity(Vector2 position, String name) {
		this.position = new Vector2(position);
		this.name = name;
	}
	
	public Vector2 getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	 
	 //public Texture getTex() {
	//	 return this.tex;
	 //}
	 
	 //public void setTex(Texture tex) {
	//	 this.tex = tex;
	 //}x`
	 
	 public String getName() {
		 return this.name;
	 }
	 
	 public void setName(String name) {
		 this.name = name;
	 }
	 public abstract void render(ShapeRenderer shapeRenderer);
}
