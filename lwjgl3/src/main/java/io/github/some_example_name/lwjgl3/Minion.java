package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
    private Rectangle bounds; //collision boundary
    private ShapeRenderer shapeRenderer;
    private float maxHP;
    private HPBar hpBar;
    
    private int MPhase1 = generateRandomNumber(200, 270);
    private int MPhase2 = generateRandomNumber(520, 570);
    private int MPhase3 = generateRandomNumber(390, 460);
    private int MPhase4 = generateRandomNumber(300, 390);
    private int MPhase5 = generateRandomNumber(160, 250);
    private int MPhase6 = 240;
    
    private int stepsMoveP1 = 0;
    private int stepsMoveP2 = 0;
    private int stepsMoveP3 = 0;
    private int stepsMoveP4 = 0;
    private int stepsMoveP5 = 0;
    private int stepsMoveP6 = 0;
    
    private boolean moveP2 = false;
    private boolean moveP3 = false;
    private boolean moveP4 = false;
    private boolean moveP5 = false;
    private boolean moveP6 = false;

      public Minion(String spriteSheetPath, float frameDuration, float startX, float startY, Map map, float maxHP) {
          this.map = map;
          this.position = new Vector2(startX, startY);
          this.bounds = new Rectangle(startX, startY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
          this.spriteSheet = new Texture(spriteSheetPath);
          this.runningAnimation = createAnimation(frameDuration);
          this.shapeRenderer = new ShapeRenderer(); // Initialize
          this.maxHP = maxHP;
          this.hpBar = new HPBar(startX, startY + DEFAULT_HEIGHT + 5, DEFAULT_WIDTH, 8, maxHP); // Position HP bar above minion
          
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
      
      private int generateRandomNumber(int min, int max) {
          return (int) (Math.random() * (max - min + 1)) + min;
      }
      
      public void update(float delta) {
	      	hpBar.setPosition(position.x, position.y + DEFAULT_HEIGHT + 5);
	        stateTime += delta;
	        float speed = DEFAULT_SPEED * delta;
	        Vector2 originalPosition = new Vector2(position);
	        
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
		//  public void draw(SpriteBatch batch) {
		//  TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime, true);
		//  batch.draw(currentFrame, position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//}
	
		public void draw(SpriteBatch batch) {
		  shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		  shapeRenderer.begin(ShapeType.Filled); // Or ShapeType.Line for an outline
		  hpBar.draw(batch);
		
		  // Calculate triangle vertices (example: equilateral triangle)
		  float halfWidth = DEFAULT_WIDTH / 2f;
		  //float halfHeight = DEFAULT_HEIGHT / 2f;
		  float x1 = position.x + halfWidth;
		  float y1 = position.y + DEFAULT_HEIGHT; // Top vertex
		  float x2 = position.x;
		  float y2 = position.y;             // Bottom-left vertex
		  float x3 = position.x + DEFAULT_WIDTH;
		  float y3 = position.y;             // Bottom-right vertex
		
		  shapeRenderer.setColor(Color.BLUE); // Set triangle color
		  shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
		
		  shapeRenderer.end();
		}
		
		public void dispose() {
		  spriteSheet.dispose();
		  hpBar.dispose();
		}
		
		public Vector2 getPosition() {
		  return position;
		}
		
		public float getX() {
		    return position.x;
		}

		public float getY() {
		    return position.y;
		}
		
		public void takeDamage(float damage) {
		  hpBar.takeDamage(damage);
		  if (isDead()) {
		        System.out.println("Minion is dead!"); }
		}
		
		public boolean isDead() {
		  return hpBar.isDead();
		}
	}