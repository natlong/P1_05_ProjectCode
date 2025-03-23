package io.github.some_example_name.lwjgl3;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.Align;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;


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
    private static final float LEVEL_Y_OFFSET = 20f;
    private static final float TOP_SPACING = 30f;
    private static final float TOWER_REMOVAL_RADIUS = 30f;
	//Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
    private Table HealthAndCoinTable;
    
    private Label levelLabel;
    private Label healthLabel;
    private Label coinsLabel;
    
    private int currentLevel = 1;
    private int playerHealth = 5;
    private int playerCoins = 300;
    

    public GameScene(GameCore game) {
    	super();
//    	this.game = game;
    	
//    	//i created this to have a clearer view of the tiled map collision
    	debugRenderer = new DebugRenderer();
    	//put to false to close it
    	debugRenderer.setEnabled(false);
    }
    
    protected void init() {
    	
    	//Display Health and Coins Display,
    	createHealthAndCoinsDisplay();
    	
    	//Level Display,
    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
    	levelLabel = new Label("Label " + currentLevel, skin);
    	levelLabel.setAlignment(Align.center);
    	levelLabel.setFontScale(1.5f);
    	levelLabel.setColor(Color.WHITE);
    	
    	//Position Label at Top Center,
    	levelLabel.setPosition((GameCore.VIEWPORT_WIDTH - levelLabel.getPrefWidth()) / 2, GameCore.VIEWPORT_HEIGHT - LEVEL_Y_OFFSET - TOP_SPACING);
    	
    	//Adding to Stage,
    	stage.addActor(levelLabel);
    	setLevel(1);
    	updateHealth(5);
    	updateCoins(300);
    	
         camera.position.set(GameCore.VIEWPORT_WIDTH / 2, GameCore.VIEWPORT_HEIGHT / 2, 0);
         camera.update();
         
         batch = new SpriteBatch();
         shapeRenderer = new ShapeRenderer();
         soundManager = SoundManager.getInstance();
         soundManager.playGameMusic();
         
         // load tilemap
         map = new Map("level.tmx");
         entityManager = new EntityManager(camera, map);

     	Rectangle gameoverArea = map.getGameoverPoint();
     	if (gameoverArea != null) { 
     		float xOffset = 10f;
     		float yOffset = -40f;
     	    creature = new Creature(gameoverArea.x + xOffset, gameoverArea.y + yOffset);
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
    
    //Create Health and Coins Display,
    private void createHealthAndCoinsDisplay() {
    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
    	
    	//Create Labels,
    	healthLabel = new Label("Health: " + playerHealth, skin);
    	coinsLabel = new Label("Coins: " + playerCoins, skin);
    	
    	//Create Layout Table,
    	HealthAndCoinTable = new Table();
    	HealthAndCoinTable.bottom().right();
    	HealthAndCoinTable.setFillParent(true);
    	HealthAndCoinTable.pad(10);
    	
    	//Adding Items to Table,
    	HealthAndCoinTable.add(healthLabel).padBottom(5).row();
    	HealthAndCoinTable.add(coinsLabel);
    	
    	stage.addActor(HealthAndCoinTable);
    }
    
    //Update Health Display,
    public void updateHealth(int x) {
    	playerHealth = x;
    	
    	healthLabel.setText("Health: " + playerHealth);
    }
    
    //Update Coins Display,
    public void updateCoins(int x) {
    	playerCoins = x;
    	
    	coinsLabel.setText("Coins: " + playerCoins);
    }
    
    //Update Level Display,
    public void setLevel(int x) {
    	currentLevel = x;
    	
    	if (levelLabel != null) {
    		levelLabel.setText("Level " + currentLevel);
    	}
    	
    	//Centering after Update,
    	levelLabel.setPosition((GameCore.VIEWPORT_WIDTH - levelLabel.getPrefWidth()) / 2, GameCore.VIEWPORT_HEIGHT - LEVEL_Y_OFFSET - TOP_SPACING);
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
    	
    		//Only Update Game if NOT Paused,
    		if (!isPaused) {
                entityManager.update(delta);
                
                if (creature != null) {
                    creature.update(delta);
                }
    		}
            
         // Count minions that have reached the gameover area.
            Rectangle gameoverArea = map.getGameoverPoint();
            if (gameoverArea != null) {
                for (AbstractEntity entity : entityManager.getEntities()) {
                	if (entity instanceof Minion && ((Minion) entity).getBounds().overlaps(gameoverArea)) {
                        entityManager.removeEntity(entity);
                        
                        //Update Player Health,
                        updateHealth(playerHealth-1);
                        
                        if (creature != null) {
                            creature.startEating();
                        }
                        
                        break;
                    }
                }
            }
            
            if (playerHealth <= 0) {
                showGameOver();
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
    	setLevel(1);
    	updateHealth(5);
    	updateCoins(300);
    	
	    //reset map
	    if (map != null) {
	        map.dispose();
	        
	    }
	    map = new Map("level.tmx");
	    //restart entity for game
	    if (entityManager != null) {
            entityManager = new EntityManager(camera, map);
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
                    if (map.isBlockedArea(x, y)) {
                    	if (playerCoins >= 100) {
                    		entityManager.addEntity(new Tower(new Vector2(x, y)));
                    		updateCoins(playerCoins-100);
                    	} else {
                    		Gdx.app.log("Game", "Not enough coins to place Tower!");
                    	}
                        
                    	return true;
                    }
                }
                
                // Right-Click to remove a tower.
                if (button == Input.Buttons.RIGHT) {
                    Tower towerToRemove = null;
                    
                    for (AbstractEntity entity : entityManager.getEntities()) {
                        if (entity instanceof Tower) {
                            Tower tower = (Tower) entity;
                            float distance = tower.getPosition().dst(x, y);

                            if (distance <= TOWER_REMOVAL_RADIUS) {
                                towerToRemove = tower;
                                break;
                            }
                        }
                    }

                    if (towerToRemove != null) {
                        boolean removed = entityManager.removeEntity(towerToRemove);
                        
                        if (removed) {
                            updateCoins(playerCoins + 100);
                            
                        } else {
                            Gdx.app.log("Game", "Failed to remove tower within range.");
                        }
                        
                    } else {
                        Gdx.app.log("Game", "No tower found within range!");
                    }
                    return true;
                }
                return false;
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
            entityManager = new EntityManager(camera, map);
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
        
        //Update Level Visibility,
        //levelLabel.setVisible(!isPaused);
        
        stage.act(delta);
        stage.draw();
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
        shapeRenderer.dispose();
        
        //Dispose of Pause Buttons,
        if (pauseTexture != null) pauseTexture.dispose();
        if (isPauseTexture != null) isPauseTexture.dispose();
        
        if (creature != null) {
            creature.dispose();
        }
        
//        if (skin != null) {
//        	skin.dispose();
//        	skin = null;
//      }
        
        if (HealthAndCoinTable != null) {
        	HealthAndCoinTable.remove();
        }
}
}