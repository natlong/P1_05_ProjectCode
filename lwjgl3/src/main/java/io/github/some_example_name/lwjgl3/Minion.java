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


public class Minion extends AbstractMovableObject{

    private static int FRAME_COLS = 6;
    private static int FRAME_ROWS = 1;
    private static int DEFAULT_WIDTH = 32;  //the minion width
    private static int DEFAULT_HEIGHT = 32; //the minion height
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
    
    private int MPhase1 = 240;	//Minimum 200 to 270,
    private int MPhase2 = 560;	//Minimum 520 to 570,
    private int MPhase3 = 400;	//Minimum 390 to 460,
    private int MPhase4 = 300;	//Minimum 300 to 390,
    private int MPhase5 = 220;	//Minimum 160 to 250,
    private int MPhase6 = 250;	//Minimum 240,
    
    private int stepsMoveP1 = 0;	//Track how many steps Minion move Right,
    private int stepsMoveP2 = 0;	//Track how many steps Minion move Down,
    private int stepsMoveP3 = 0;	//Track how many steps Minion move Right,
    private int stepsMoveP4 = 0;	//Track how many steps Minion move Up,
    private int stepsMoveP5 = 0;	//Track how many steps Minion move Right,
    private int stepsMoveP6 = 0;	//Track how many steps Minion move Up,
    
    private boolean moveP2 = false;
    private boolean moveP3 = false;
    private boolean moveP4 = false;
    private boolean moveP5 = false;
    private boolean moveP6 = false;

      public Minion(String spriteSheetPath, float frameDuration, Vector2 position, Map map, float maxHp, OrthographicCamera  camera, float speed) {
          super(position, "Minion", maxHp, maxHp, speed);
    	  this.map = map;
          this.bounds = new Rectangle(position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
          this.spriteSheet = new Texture(spriteSheetPath);
          this.runningAnimation = createAnimation(frameDuration);
          this.shapeRenderer = new ShapeRenderer(); // Initialize
          this.hpBar = new HPBar(position.x, position.y + DEFAULT_HEIGHT + 5, DEFAULT_WIDTH, 8, maxHp, camera); // Position HP bar above minion
          this.camera = camera;
          
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
	        float speed = this.getSpeed() * delta;
	        //Vector2 originalPosition = new Vector2(position);
	        
	        //float permittedX = position.x;
	        //float permittedY = position.y;
	        
	        //Phase 1: Moving Right,
	        if (!moveP2 && stepsMoveP1 < MPhase1) {
	            position.x += speed;
	            stepsMoveP1++;
	        }
	        
	        //Phase 2: Moving Down,
	        if (stepsMoveP1 >= MPhase1 && stepsMoveP2 < MPhase2) {
	        	moveP2 = true;
	            position.y -= speed;
	            stepsMoveP2++;
	        }
	        
	        //Phase 3: Moving Right,
	        if (stepsMoveP2 >= MPhase2 && stepsMoveP3 < MPhase3) {
	        	moveP3 = true;
	            position.x += speed;
	            stepsMoveP3++;
	        }
	        
	        //Phase 4: Moving Up,
	        if (stepsMoveP3 >= MPhase3 && stepsMoveP4 < MPhase4) {
	        	moveP4 = true;
	            position.y += speed;
	            stepsMoveP4++;
	        }
	        
	        //Phase 5: Moving Right,
	        if (stepsMoveP4 >= MPhase4 && stepsMoveP5 < MPhase5) {
	        	moveP5 = true;
	            position.x += speed;
	            stepsMoveP5++;
	        }
	        
	        //Phase 6: Moving Up,
	        if (stepsMoveP5 >= MPhase5 && stepsMoveP6 < MPhase6) {
	        	moveP6 = true;
	            position.y += speed;
	            stepsMoveP6++;
	        }
	           
	        //collision checking
	        //bounds.setPosition(position.x, position.y);
	        //if (map.isColliding(bounds)) {
	        //	System.out.println("Minion collided! Resetting position.");
	        //    position.x = permittedX;
	        //    position.y = permittedY;
	        //}
	        
	        //ScreenBounds(); //character wont walk out of frame
	       
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
		  hpBar.takeDamage(damage);
		  if (isDead()) {
		        System.out.println("Minion is dead!"); }
		}
		
		public boolean isDead() {
		  return hpBar.isDead();
		@Override
		public void movement() {
			// TODO Auto-generated method stub
			
		}
	}