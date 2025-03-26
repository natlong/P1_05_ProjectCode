package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Food extends AbstractMovableObject implements Targetable{

    private static final int DEFAULT_WIDTH = 50;  //the food width
    private static final int DEFAULT_HEIGHT = 50; //the food height
    private static final int MAP_WIDTH = 40; //the tiled map i set 40x30
    private static final int MAP_HEIGHT = 30;

    private Map map;
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    private HPBar hpBar;
    private OrthographicCamera camera;
    private ArrayList<Vector2> waypoints;
    private int currentWaypointIndex = 0;
    private Texture texture;
    private boolean isBadFood;
    private boolean isUserTargeted = false;
    
    private float slowDuration = 1f;
    private float slowTimer = 0f;
    private boolean isSlowed = false;
    private float originalSpeed;


    public Food(String texturePath, String foodName, Vector2 position, Map map, float maxHp, OrthographicCamera camera, float speed, boolean isBadFood) {
        super(position, foodName, maxHp, maxHp, speed);
        this.map = map;
        this.bounds = new Rectangle(position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();
        this.hpBar = new HPBar(position.x, position.y + DEFAULT_HEIGHT + 5, DEFAULT_WIDTH, 8, maxHp, camera);
        this.camera = camera;
        this.waypoints = map.getPathWaypoints();
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.isBadFood = isBadFood;
        this.originalSpeed = speed;
    }
    
    public boolean isUserTargeted() {
        return isUserTargeted;
    }

    public void setUserTargeted(boolean targeted) {
        isUserTargeted = targeted;
    }

    public void update(float delta) {
        hpBar.setPosition(this.getPosition().x, position.y + DEFAULT_HEIGHT + 5);
        super.update(delta); // Call the superclass update to handle movement
        
        if(isSlowed) {
        	slowTimer += delta;
        	if(slowTimer >= slowDuration) {
        		resetSpeed();
        	}
        }
    }

    private void ScreenBounds() {
        if (position.x < 0) position.x = 0;
        if (position.x > MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH)
            position.x = MAP_WIDTH * DEFAULT_WIDTH - DEFAULT_WIDTH;
        if (position.y < 0) position.y = 0;
        if (position.y > MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT)
            position.y = MAP_HEIGHT * DEFAULT_HEIGHT - DEFAULT_HEIGHT;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        SpriteBatch batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(texture, position.x, position.y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        batch.end();
        batch.dispose();
        hpBar.render(shapeRenderer);
    }
    
    // Renders a red outline if the minion is flagged.
    public void renderOutline(ShapeRenderer shapeRenderer) {
        if (isUserTargeted) {
            shapeRenderer.setColor(1, 0, 0, 1); // red color for outline
            Rectangle bounds = getBounds();
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void resetSpeed() {
    	if(isSlowed) {
    		super.setSpeed(originalSpeed);
    		isSlowed = false;
    		slowTimer = 0f;
    	}
    }
    public void takeDamage(float damage) {
        float newHp = this.getHp() - damage;
        if (newHp < 0) {
            newHp = 0;
        }
        this.setHp(newHp);
        hpBar.updateHealth(this.getHp(), this.getMaxHp());
        
        if(!isSlowed) {
        	super.setSpeed(50f);
        	isSlowed = true;
        	slowTimer = 0f;
        }
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
        if (texture != null) {
            texture.dispose();
        }
    }

    public boolean isBadFood() {
        return isBadFood;
    }

    public boolean isGoodFood() {
        return !isBadFood;
    }

}