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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;


public class Minion extends AbstractMovableObject implements Targetable{

    private static final int FRAME_COLS = 6;
    private static final int FRAME_ROWS = 1;
    private static final int DEFAULT_WIDTH = 32;  //the minion width
    private static final int DEFAULT_HEIGHT = 32; //the minion height
    private static final int MAP_WIDTH = 40; //the tiled map i set 40x30
    private static final int MAP_HEIGHT = 30;
    
    private Map map;
    private float stateTime;
    private Rectangle bounds; //collision boundary
    private ShapeRenderer shapeRenderer;
    private HPBar hpBar;
    private OrthographicCamera camera;
    
    // New fields for waypoint-based movement
    private ArrayList<Vector2> waypoints;
    private int currentWaypointIndex = 0;

      public Minion(String spriteSheetPath, float frameDuration, Vector2 position, Map map, float maxHp, OrthographicCamera  camera, float speed) {
          super(position, "Minion", maxHp, maxHp, speed);
    	  this.map = map;
          this.bounds = new Rectangle(position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
          this.shapeRenderer = new ShapeRenderer(); // Initialize
          this.hpBar = new HPBar(position.x, position.y + DEFAULT_HEIGHT + 5, DEFAULT_WIDTH, 8, maxHp, camera); // Position HP bar above minion
          this.camera = camera;
          this.waypoints = map.getPathWaypoints();
          
      }      
      
      public void update(float delta) {
	      	hpBar.setPosition(this.getPosition().x, position.y + DEFAULT_HEIGHT + 5);
	        stateTime += delta;
	        movement();
	    }
	      
	    private void ScreenBounds() {
	        if (position.x < 0) position.x = 0;
	        if (position.x > MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH)
	            position.x = MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH;
	        if (position.y < 0) position.y = 0;
	        if (position.y > MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT)
	            position.y = MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT;
	    }
	
		public void render(ShapeRenderer shapeRenderer) {
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
		  hpBar.render(shapeRenderer);
		}
		
		public void takeDamage(float damage) {
		  float newHp = this.getHp() - damage;
		  if (newHp < 0) {
			  newHp = 0;
		  }
		  
		  this.setHp(newHp);
		  hpBar.updateHealth(this.getHp(), this.getMaxHp());
		}
		
		public Rectangle getBounds() {
			return this.bounds;
		}
		
		public float getWidth() {
	        return DEFAULT_WIDTH;
	    }

	    public float getHeight() {
	        return DEFAULT_HEIGHT;
	    }
		
		public boolean isDead() {
		  return this.getHp() <= 0;
		}
		
		public boolean hitGameoverArea() {
	        Rectangle gameoverArea = map.getGameoverPoint();
	        if (gameoverArea != null) {
	            return gameoverArea.overlaps(bounds);
	        }
	        return false;
	    }

		@Override
		public void movement() {
		    if (waypoints == null || waypoints.isEmpty() || currentWaypointIndex >= waypoints.size())
		        return;
		    
		    Vector2 currentPos = new Vector2(getPosition());
		    Vector2 targetWaypoint = waypoints.get(currentWaypointIndex);
		    Vector2 toTarget = new Vector2(targetWaypoint).sub(currentPos);
		    
		    float distance = toTarget.len();
		    float step = getSpeed() * Gdx.graphics.getDeltaTime();
		    float epsilon = 0.1f;
		    
		    if (distance <= step || distance < epsilon) {
		        setPosition(new Vector2(targetWaypoint));
		        getBounds().setPosition(targetWaypoint.x, targetWaypoint.y);
		        currentWaypointIndex++;
		    } else {
		        toTarget.nor().scl(step);
		        currentPos.add(toTarget);
		        setPosition(currentPos);
		        getBounds().setPosition(currentPos.x, currentPos.y);
		    }
		}
		public void dispose() {
			  shapeRenderer.dispose();
			}
	}