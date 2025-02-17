package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;


public class Minion extends AbstractMovableObject{

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
    private Texture spriteSheet;
    private Rectangle bounds; //collision boundary
    private ShapeRenderer shapeRenderer;
    private HPBar hpBar;
    private OrthographicCamera camera;
    
    //Waypoint Configurations,
    private List<Vector2> waypoints;
    private int currentWaypointIndex = 0;
    private Vector2 goal;

      public Minion(String spriteSheetPath, float frameDuration, Vector2 position, Map map, float maxHp, OrthographicCamera  camera, float speed) {
          super(position, "Minion", maxHp, maxHp, speed);
    	  this.map = map;
          this.bounds = new Rectangle(position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
          this.spriteSheet = new Texture(spriteSheetPath);
          this.runningAnimation = createAnimation(frameDuration);
          this.shapeRenderer = new ShapeRenderer(); // Initialize
          this.hpBar = new HPBar(position.x, position.y + DEFAULT_HEIGHT + 5, DEFAULT_WIDTH, 8, maxHp, camera); // Position HP bar above minion
          this.camera = camera;
          
          // Define Waypoints,
          this.waypoints = new ArrayList<>();
          waypoints.add(new Vector2(308, 790));
          waypoints.add(new Vector2(306, 140));
          waypoints.add(new Vector2(788, 140));
          waypoints.add(new Vector2(788, 500));
          waypoints.add(new Vector2(1077, 500));
          waypoints.add(new Vector2(1073, 853));
          
          this.goal = waypoints.get(waypoints.size() - 1);
          
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
	      	hpBar.setPosition(this.getPosition().x, position.y + DEFAULT_HEIGHT + 5);
	        stateTime += delta;
	        
	        if (waypoints.isEmpty()) return;
	        
	        if (currentWaypointIndex < waypoints.size()) {
	        	Vector2 targetWaypoint = waypoints.get(currentWaypointIndex);
	        	Vector2 direction = targetWaypoint.cpy().sub(position).nor();
	        	position.add(direction.scl(DEFAULT_SPEED * delta));
	        	
	        	if (position.dst(targetWaypoint) < 5f) {
	        		currentWaypointIndex++;
	        	}
	        }
	        
	        System.out.println("Minion Position: " + position);
	           
	        //collision checking
	        //bounds.setPosition(position.x, position.y);
	        //if (map.isColliding(bounds)) {
	        //	System.out.println("Minion collided! Resetting position.");
	        //    position.x = permittedX;
	        //    position.y = permittedY;
	        //}
	        
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
	
		public void draw(ShapeRenderer shapeRenderer) {
		  shapeRenderer.setProjectionMatrix(camera.combined);
		  //shapeRenderer.begin(ShapeType.Filled); // Or ShapeType.Line for an outline
		  
		
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
		
		  //shapeRenderer.end();
		  hpBar.draw(shapeRenderer);
		}
		
		public void dispose() {
		  spriteSheet.dispose();
		  hpBar.dispose();
		  shapeRenderer.dispose();
		}
		
		public void takeDamage(float damage) {
		  float newHp = this.getHp() - damage;
		  if (newHp < 0) {
			  newHp = 0;
		  }
		  
		  this.setHp(newHp);
		  hpBar.updateHealth(this.getHp(), this.getMaxHp());
		  
		  if (isDead()) {
		        System.out.println("Minion is dead!"); }
		}
		
		public Rectangle getBounds() {
			return this.bounds;
		}
		
		public boolean isDead() {
		  return this.getHp() <= 0;
		}

		@Override
		public void movement() {
			// TODO Auto-generated method stub
			
		}
	}