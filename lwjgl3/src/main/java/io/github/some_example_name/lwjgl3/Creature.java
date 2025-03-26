package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import config.GameConfig;


public class Creature {
	private SoundManager soundManager;
    public enum State {
        IDLE,
        EATING
    }
    
    private static final float IDLE_FRAME_DURATION = GameConfig.getInstance().getIdleFrameDuration();
    private static final float EATING_FRAME_DURATION = GameConfig.getInstance().getEatingFrameDuration();
    
    //size of the creature
    private static final int FRAME_WIDTH = GameConfig.getInstance().getFrameWidth();
    private static final int FRAME_HEIGHT = GameConfig.getInstance().getFrameHeight();
    
    
    private Vector2 position;
    private Rectangle bounds;
    
    
    private Texture spriteSheet;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> eatingAnimation;
    private float stateTime = 0;
    private State currentState = State.IDLE;
    
    
    private float eatingTimer = 0;
    private static final float EATING_DURATION = 0.5f;
    

    public Creature(float x, float y) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, FRAME_WIDTH, FRAME_HEIGHT);
        this.soundManager = SoundManager.getInstance();
        //load sprite sheet and split it into frames
        loadAnimations();
    }
    

    private void loadAnimations() {
        spriteSheet = new Texture(Gdx.files.internal("creature-eating.png"));

        TextureRegion[][] frames = TextureRegion.split(
            spriteSheet, 
            spriteSheet.getWidth() / 5,
            spriteSheet.getHeight()
        );
        
        //idle animation (frames 0, 1, 2)
        TextureRegion[] idleFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            idleFrames[i] = frames[0][i];
        }
        idleAnimation = new Animation<>(IDLE_FRAME_DURATION, idleFrames);
        
        // frames 3 and 4 for eating
        TextureRegion[] eatingFrames = new TextureRegion[2]; // Only need frames 3 and 4
        eatingFrames[0] = frames[0][3];
        eatingFrames[1] = frames[0][4];
        
        eatingAnimation = new Animation<>(EATING_FRAME_DURATION, eatingFrames);
    }
    
    public void update(float delta) {
    	stateTime += delta;
        
        if (currentState == State.EATING) {
            eatingTimer += delta;
            if (eatingTimer >= EATING_DURATION) {
                eatingTimer = 0;
                currentState = State.IDLE;
            }
        }
    }
    
    
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        
        batch.draw(
            currentFrame,
            position.x,
            position.y,
            FRAME_WIDTH,
            FRAME_HEIGHT
        );
    }
    
    private TextureRegion getCurrentFrame() {
        if (currentState == State.IDLE) {
            return idleAnimation.getKeyFrame(stateTime, true);
        } else {
            return eatingAnimation.getKeyFrame(stateTime, false);
        }
    }

    public void startEating() {
        if (currentState != State.EATING) {
            currentState = State.EATING;
            eatingTimer = 0;
            stateTime = 0;

            if (soundManager != null) {
                soundManager.playEatingSoundeffect();
            }
        }
    }

    public Vector2 getPosition() {
        return position;
    }
    

    public Rectangle getBounds() {
        return bounds;
    }
    

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x, y);
    }
    

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }
}