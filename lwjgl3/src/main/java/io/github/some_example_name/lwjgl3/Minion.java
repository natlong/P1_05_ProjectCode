package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Minion {

    private static int FRAME_COLS = 6;
    private static int FRAME_ROWS = 1;
    private static int DEFAULT_WIDTH = 32;  //the minion width
    private static int DEFAULT_HEIGHT = 32; //the minion height
    private float DEFAULT_SPEED = 200;
    private static int MAP_WIDTH = 40; //the tiled map i set 40x30
    private static int MAP_HEIGHT = 30;
    
    private Map map;
    private Animation<TextureRegion> runningAnimation;
    private float stateTime;
    private Vector2 position;
    private Texture spriteSheet;
    private Rectangle bounds; //collison boundary


	    public Minion(String spriteSheetPath, float frameDuration, float startX, float startY, Map map) {
	        this.map = map;
	        this.position = new Vector2(startX, startY);
	        this.bounds = new Rectangle(startX, startY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	        this.spriteSheet = new Texture(spriteSheetPath);
	        this.runningAnimation = createAnimation(frameDuration);
	        
	    }
	    //this is to animate the character movement
	    private Animation<TextureRegion> createAnimation(float frameDuration) {
	        TextureRegion[][] frames = TextureRegion.split(
	            spriteSheet, 
	            spriteSheet.getWidth() / FRAME_COLS, 
	            spriteSheet.getHeight() / FRAME_ROWS
	        );
	    
	        TextureRegion[] runningFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
	        int index = 0;
	        for (int i = 0; i < FRAME_ROWS; i++) {
	            for (int j = 0; j < FRAME_COLS; j++) {
	                runningFrames[index++] = frames[i][j];
	            }
	        }
	        return new Animation<>(frameDuration, runningFrames);
	    }
	    
 
    public void update(float delta) {
        stateTime += delta;
        float speed = DEFAULT_SPEED * delta;
        Vector2 originalPosition = new Vector2(position);
        
        
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        	position.x -= speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	position.y += speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	position.y -= speed;
        }
        
        //collision checking
        bounds.setPosition(position.x, position.y);
        if (map.isColliding(bounds)) {
            position.set(originalPosition);
        }
        
        ScreenBounds(); //character wont walk out of frame
       
    }
    private void ScreenBounds() {
        if (position.x < 0) position.x = 0;
        if (position.x > MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH)
            position.x = MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH;
        if (position.y < 0) position.y = 0;
        if (position.y > MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT)
            position.y = MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT;
    }
    

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void dispose() {
        spriteSheet.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }
    
    public float getWidth() {
        return DEFAULT_WIDTH;
    }

    public float getHeight() {
        return DEFAULT_HEIGHT;
    }
}