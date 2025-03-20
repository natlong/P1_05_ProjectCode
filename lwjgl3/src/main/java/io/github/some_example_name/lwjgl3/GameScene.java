package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;


public class GameScene extends AbstractScene {
	private static final int BUTTON_SIZE = 60;
	private static final int GAME_OVER_THRESHOLD = 5;
	private DebugRenderer debugRenderer;
    private ImageButton pauseButton;
    private ImageButton settingsButton;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private EntityManager entityManager;
    private SoundManager soundManager;
    private Map map;
    private OptionsScene optionsScreen;
    private GameOverScene gameOverScene;
    private Texture pauseTexture;
    private Texture isPauseTexture;
    private TextureRegionDrawable pauseDrawable;
    private TextureRegionDrawable isPauseDrawable;
    private Creature creature;
    
    //Game Pause Variable,
    private boolean isPaused;
    
    //UI Level Variable,
    private static final float LEVEL_DISPLAY_DURATION = 2.0f;
    private BitmapFont levelFont;
    private float displayTimer;
    private int currentLevel = 1;


    public GameScene(GameCore game) {
    	super();
//    	this.game = game;
    	
//    	//i created this to have a clearer view of the tiled map collision
    	debugRenderer = new DebugRenderer();
    	//put to false to close it
    	debugRenderer.setEnabled(false);
    }
    
    protected void init() {
    	//Level Display,
    	levelFont = new BitmapFont();
    	levelFont.getData().setScale(3);
    	levelFont.setColor(Color.WHITE);
    	resetLevelDisplay();
    	
    	
         camera.position.set(GameCore.VIEWPORT_WIDTH / 2, GameCore.VIEWPORT_HEIGHT / 2, 0);
         camera.update();
         
         batch = new SpriteBatch();
         shapeRenderer = new ShapeRenderer();
         soundManager = new SoundManager();
         soundManager.playGameMusic();
         
         // load tilemap
         map = new Map("level.tmx");
         entityManager = new EntityManager(camera, map, soundManager);

     	Rectangle gameoverArea = map.getGameoverPoint();
     	if (gameoverArea != null) { 
     		float xOffset = 10f;
     		float yOffset = -40f;
     	    creature = new Creature(gameoverArea.x + xOffset, gameoverArea.y + yOffset);
     	    creature.setSoundManager(soundManager);
     	}
         
         createButtons();
         handleInput();
    }
   
	    
    private void createButtons() {
        //Loading Textures,
        pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        isPauseTexture = new Texture(Gdx.files.internal("start.png"));
        Texture settingsTexture = new Texture(Gdx.files.internal("settings.png"));

        //Create Drawables,
        pauseDrawable = new TextureRegionDrawable(pauseTexture);
        isPauseDrawable = new TextureRegionDrawable(isPauseTexture);
        TextureRegionDrawable settingsDrawable = new TextureRegionDrawable(settingsTexture);
        
        //Button Configuration,
        ImageButton.ImageButtonStyle pauseButtonStyle = new ImageButton.ImageButtonStyle();
        pauseButtonStyle.up = pauseDrawable; //Settings Default State,
        
        //Create Dynamic Buttons,
        pauseButton = new ImageButton(pauseButtonStyle);
        settingsButton = new ImageButton(settingsDrawable);
        
        
        //button position, top down arrangement
        float topY = GameCore.VIEWPORT_HEIGHT-60;
        float rightX = GameCore.VIEWPORT_WIDTH - 5;
        pauseButton.setPosition(rightX - BUTTON_SIZE, topY);
        settingsButton.setPosition(rightX - BUTTON_SIZE, topY - BUTTON_SIZE);
        
        pauseButtonListener();
        settingsButtonListener();
        
        stage.addActor(pauseButton);
        stage.addActor(settingsButton);
        }

    //Reset Display Timer,
    private void resetLevelDisplay() {
    	displayTimer = LEVEL_DISPLAY_DURATION;
    }
    
    //Next Level Display,
    public void nextLevel() {
    	currentLevel++;
    	resetLevelDisplay();
    }
  
    
    //Configure Dynamic Changes,
    private void pauseButtonListener() {
        pauseButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (isPaused) {
                    resume();
                    ((ImageButton.ImageButtonStyle)pauseButton.getStyle()).up = pauseDrawable;
                    
                } else {
                    pause();
                    ((ImageButton.ImageButtonStyle)pauseButton.getStyle()).up = isPauseDrawable;

                }
                return true;
            }
        });
    }
     
    private void settingsButtonListener() {
    	settingsButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
        @Override
        public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
            openSettings();
            return true;
	        }
	    });
	}
    

    private void openSettings() {
        Gdx.app.log("GameScreen", "Settings button clicked");
        
        //Pause the Game,
        if (!isPaused) {
        	pause();
        }
        
        //Create the Options Popup,
        if (optionsScreen != null) {
            optionsScreen.dispose();
        }
        
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        optionsScreen = new OptionsScene(skin, stage, soundManager);
        
        //Resume Game when Options Menu is Closed,
        optionsScreen.setOptionsClosedListener(new OptionsScene.OptionsClosedListener() {
			@Override
			//Allows Resuming, if Game is not Over,
			public void onOptionsClosed() {
				if (gameOverScene == null) {
					resume();
				}
			}
		});
        
        stage.addActor(optionsScreen);
    }
    
    @Override
	public void update(float delta) {
    	if (!isPaused) {
    		if (displayTimer > 0) {
    			displayTimer -= delta;
    		}
    		
            entityManager.update(delta);
            
            if (creature != null) {
                creature.update(delta);
            }
            
         // Count minions that have reached the gameover area.
            int count = 0;
            Rectangle gameoverArea = map.getGameoverPoint();
            if (gameoverArea != null) {
                for (AbstractEntity entity : entityManager.getEntities()) {
                	if (entity instanceof Minion && ((Minion) entity).getBounds().overlaps(gameoverArea)) {
                        count++;
                        
                        if (creature != null) {
                            creature.startEating();
                        }
                    }
                }
            }
            
            if (count >= GAME_OVER_THRESHOLD) {
                showGameOver();
            }
        }
	}
    
    private void showGameOver() {
    	if (gameOverScene == null){
    		isPaused = true;
	    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
	        gameOverScene = new GameOverScene(skin, stage, this);
	        stage.addActor(gameOverScene);
    	}
    }
    
    public void resetGame() {
    	//Reset Level,
    	currentLevel = 1;
    	resetLevelDisplay();
    	
	    //reset map
	    if (map != null) {
	        map.dispose();
	        
	    }
	    map = new Map("level.tmx");
	    //restart entity for game
	    if (entityManager != null) {
            entityManager = new EntityManager(camera, map,soundManager);
        }
        
        // Reset game-over overlay.
        if (gameOverScene != null) {
            gameOverScene.dispose();
            gameOverScene = null;
        }
	    
	    if (soundManager != null) {
	        soundManager.restartCurrentMusic();
	    }
	    isPaused = false;
    }

    //Player Input for Tower,
    private void handleInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
                float x = worldCoordinates.x;
                float y = worldCoordinates.y;

                // Left-Click to place a tower.
                if (button == Input.Buttons.LEFT) {
                    if (!map.isGreenArea(x, y)) {
                        entityManager.addEntity(new Tower(new Vector2(x, y),soundManager));
                    }
                }
                // Right-Click to remove a tower.
                if (button == Input.Buttons.RIGHT) {
                    entityManager.removeEntity(new Tower(new Vector2(x, y),soundManager));
                }
                return true;
            }
        });
        
        // Set the multiplexer as the input processor so that both the stage and custom adapter receive events.
        Gdx.input.setInputProcessor(multiplexer);
    }


    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        createButtons();
        
        if (entityManager == null) {
            entityManager = new EntityManager(camera, map,soundManager);
        }
    }

    @Override
    public void render(float delta) {
    	// Call update logic
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.render(camera);
        
        if (debugRenderer.isEnabled) {
            debugRenderer.renderMapDebug(camera, map);
            
            if (!entityManager.getEntities().isEmpty()) {
                debugRenderer.renderMinions(camera, entityManager.getEntities());
            }
        }

        entityManager.render(shapeRenderer);
        entityManager.render(batch);
        
        if (creature != null) {
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            creature.render(batch);
            batch.end();
        }
        
        //Display Level Number,
        if (displayTimer > 0) {
        	batch.begin();
        	String levelText = "Level " + currentLevel;
        	
        	//Enable Smooth Fading Effect,
        	float alpha = Math.min(1.0f, displayTimer/0.5f);
        	levelFont.setColor(1, 1, 1, alpha);

        	//Centering Level Display,
        	Vector2 textPosition = new Vector2(camera.position.x - (getTextWidth(levelText)/2), camera.position.y);
        	levelFont.draw(batch, levelText, textPosition.x, textPosition.y);
        	batch.end();
        }
        
        //Pause Overlay if Game is Paused,
        if (isPaused) {
        	//Allow Transparency,
        	Gdx.gl.glEnable(GL20.GL_BLEND);
        	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        	//Create Rectangle to be Semi-Transparent,
        	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        	shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.3f);
        	
        	//Overlays the Entire Screen,
        	shapeRenderer.setProjectionMatrix(camera.combined);
        	shapeRenderer.rect(0, 0, GameCore.VIEWPORT_WIDTH, GameCore.VIEWPORT_HEIGHT);
        	
        	shapeRenderer.end();
        	
        	//Disable Blending,
        	Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        
        stage.act(delta);
        stage.draw();
    }
    
    //Get Display Level Width,
    private float getTextWidth(String x) {
    	GlyphLayout layout = new GlyphLayout(levelFont, x);
    	return layout.width;
    }

    
    @Override
    public void resize(int width, int height) {
    	stage.getViewport().update(width, height);
    }
    
    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume() called");
        soundManager.resume();
        isPaused = false;
        }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause() called");
        soundManager.pause();
        isPaused = true;
    }
    
    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide() called");
    }

    @Override
    public void dispose() {
    	super.dispose();
        batch.dispose();
        map.dispose();
        debugRenderer.dispose ();
        soundManager.dispose();
        shapeRenderer.dispose();
        
        //Dispose of Pause Buttons,
        if (pauseTexture != null) pauseTexture.dispose();
        if (isPauseTexture != null) isPauseTexture.dispose();
        
        if (creature != null) {
            creature.dispose();
        }
        
        if (levelFont != null) {
        	levelFont.dispose();
        }
}
}