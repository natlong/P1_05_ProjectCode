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

    private static final int DEFAULT_WIDTH = 32;  //the minion width
    private static final int DEFAULT_HEIGHT = 32; //the minion height
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

    public void takeDamage(float damage) {
        float newHp = this.getHp() - damage;
        if (newHp < 0) {
            newHp = 0;
        }
        this.setHp(newHp);
        hpBar.updateHealth(this.getHp(), this.getMaxHp());
        
        float orginalSpeed = super.getSpeed();
        super.setSpeed(50f); //To slow food down
        
        new Thread(()->{
        	try {
        		Thread.sleep(1000);
        		super.setSpeed(orginalSpeed);
        	}catch(InterruptedException e) {
        		e.printStackTrace();
        	}
        }).start();
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

    public enum FoodType {
        APPLE("food_pics/apple.png", false),
        STRAWBERRY("food_pics/strawberry.png", false),
        BANANAS("food_pics/bananas.png", false),
        CARROT("food_pics/carrot.png", false),
        BROCCOLI("food_pics/broccoli.png", false),
        DONUT("food_pics/donut.png", true),
        ICE_CREAM("food_pics/ice-cream.png", true),
        PIZZA("food_pics/pizza.png", true),
        FRIED_CHICKEN("food_pics/fried-chicken.png", true),
        HAMBURGER("food_pics/hamburger.png", true);

        private final String texturePath;
        private final boolean isBadFood;

        FoodType(String texturePath, boolean isBadFood) {
            this.texturePath = texturePath;
            this.isBadFood = isBadFood;
        }

        public String getTexturePath() {
            return texturePath;
        }

        public boolean isBadFood() {
            return isBadFood;
        }
    }
}