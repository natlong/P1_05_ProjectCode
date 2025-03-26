package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.Align;

import config.GameConfig;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

//New enum to define game phases (could also be in its own file)
enum GamePhase {
	PLANNING,
	SIMULATION
}

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
    private GameConfig gameConfig;
    private Map map;
    private OptionsScene optionsScreen;
    private GameOverScene gameOverScene;
    private Texture pauseTexture;
    private Texture isPauseTexture;
    private TextureRegionDrawable pauseDrawable;
    private TextureRegionDrawable isPauseDrawable;
    private Creature creature;
    private Texture teethTexture;
    private Texture coinPileTexture;
   
    
    //Game Pause Variable,
    private boolean isPaused;
    
    //UI Level Variable,
    private static final float LEVEL_Y_OFFSET = 20f;
    private static final float TOP_SPACING = 30f;
    private static final float TOWER_REMOVAL_RADIUS = 30f;
    private Table HealthAndCoinTable;
    
    private Label levelLabel;
    private Label healthLabel;
    private Label healthCount;
    private Label coinsLabel;
    private Label coinsCount;
    
    private int currentLevel = 1;
    private int playerHealth = 5;
    private int playerCoins = 300;
    
    // NEW: Field to track the current phase. Start in PLANNING.
    private GamePhase currentPhase = GamePhase.PLANNING;
    // NEW: Field to hold a manually selected target in SIMULATION phase.
    private Targetable manualTarget;

    public GameScene(GameCore game) {
    	super();
//    	this.game = game;
    	
//    	//i created this to have a clearer view of the tiled map collision
    	debugRenderer = new DebugRenderer();
    	//put to false to close it
    	debugRenderer.setEnabled(false);
	}
    
    protected void init() {
    	Gdx.app.log("GameScene", "init() called");
    	
    	//Display Health and Coins Display,
    	createHealthAndCoinsDisplay();
    	
    	//Level Display,
    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
    	levelLabel = new Label("PLANNING", skin);
    	levelLabel.setAlignment(Align.center);
    	levelLabel.setFontScale(1.5f);
    	levelLabel.setColor(Color.WHITE);
    	
    	//Position Label at Top Center,
    	levelLabel.setPosition((GameCore.VIEWPORT_WIDTH - levelLabel.getPrefWidth()) / 2, GameCore.VIEWPORT_HEIGHT - LEVEL_Y_OFFSET - TOP_SPACING);
    	
    	//Adding to Stage,
    	stage.addActor(levelLabel);
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
         gameConfig = GameConfig.getInstance();
         gameConfig.loadConfig(currentLevel); //To Update
         entityManager = new EntityManager(gameConfig, camera, map);
         
         //Get Level and Update Dynamically from EntityManager,
         setupEntityManager();

     	Rectangle gameoverArea = map.getGameoverPoint();
     	if (gameoverArea != null) { 
     		float xOffset = 10f;
     		float yOffset = -40f;
     	    creature = new Creature(gameoverArea.x + xOffset, gameoverArea.y + yOffset);
     	}
         
        createButtons();
        handleInput();
        currentPhase = GamePhase.PLANNING;
        createPlanningOverlay();
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
        
        //Create a table for buttons
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.top().right().pad(20);
        
        buttonTable.add(pauseButton).size(30).right().row();
        buttonTable.add(settingsButton).size(30).right().padTop(20);
        
        pauseButtonListener();
        settingsButtonListener();
        
        stage.addActor(buttonTable);
    }
    
    //Create Health and Coins Display,
    private void createHealthAndCoinsDisplay() {
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        teethTexture = new Texture(Gdx.files.internal("teeth.png"));
        coinPileTexture = new Texture(Gdx.files.internal("coinpile.png"));

        // Create Labels
        healthLabel = new Label("Health:", skin);
        healthLabel.setAlignment(Align.left);
        
        healthCount = new Label("" + playerHealth, skin);
        healthCount.setAlignment(Align.center);
        
        coinsLabel = new Label("Coins:", skin);
        coinsLabel.setAlignment(Align.left);
        
        coinsCount = new Label("" + playerCoins, skin);
        coinsCount.setAlignment(Align.center);

        HealthAndCoinTable = new Table();
        HealthAndCoinTable.setFillParent(true);
        HealthAndCoinTable.bottom().right().pad(20);
        
        //Create row with 3 columns: label, value, icon
        HealthAndCoinTable.add(healthLabel).left();
        HealthAndCoinTable.add(healthCount).center().minWidth(60).padLeft(5).padRight(5);
        HealthAndCoinTable.add(new Image(teethTexture)).size(30).right();
        HealthAndCoinTable.row().padTop(7);
        
        HealthAndCoinTable.add(coinsLabel).left();
        HealthAndCoinTable.add(coinsCount).center().minWidth(60).padLeft(5).padRight(5);
        HealthAndCoinTable.add(new Image(coinPileTexture)).size(30).right();
        
        stage.addActor(HealthAndCoinTable);
        }
    
    //Update Health Display,
    public void updateHealth(int x) {
        playerHealth = x;
        healthCount.setText("" + playerHealth);
      }
    
    //Update Coins Display,
    public void updateCoins(int x) {
        playerCoins = x;      
        coinsCount.setText("" + playerCoins);

      }
    
    //Getting Level from EntityManager,
    private void setupEntityManager() {
        //Listener to Sync Levels with EntityManager,
        entityManager.setLevelChangeListener(new EntityManager.LevelChangeListener() {

			@Override
			public void onLevelChanged(int newLevel) {
                currentLevel = newLevel;
                updateLevelDisplay();  //Update UI when level changes,
			}
        });
    }

    //Update UI display for the current level,
    private void updateLevelDisplay() {
        levelLabel.setText("Level: " + currentLevel);
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
    
 // NEW: Create the planning overlay. When closed, add the START button to the game scene.
    private void createPlanningOverlay() {
        if (currentPhase != GamePhase.PLANNING) return;
        
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        PlanningOverlay planningOverlay = new PlanningOverlay("", skin, stage);
        planningOverlay.setPlanningOverlayListener(new PlanningOverlay.PlanningOverlayListener() {
            @Override
            public void onClose() {
                Gdx.app.log("GameScene", "Planning overlay closed. Adding START button.");
                createStartButton();
            }
        });
        Gdx.app.log("GameScene", "Adding PlanningOverlay to stage");
        stage.addActor(planningOverlay);
    }

    // NEW: Create a START button in the game scene (outside the overlay).
    private void createStartButton() {
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        final TextButton startButton = new TextButton("START", skin);
        
        // Position it at the bottom center.
        startButton.setPosition((GameCore.VIEWPORT_WIDTH - startButton.getPrefWidth()) / 2, 20);
        
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentPhase = GamePhase.SIMULATION;
                Gdx.app.log("GameScene", "Switched to SIMULATION phase via START button.");
                startButton.remove(); // Remove the start button after starting.
            }
        });
        
        stage.addActor(startButton);
    }
    
//    //Player Input for Tower,
//    private void handleInput() {
//        InputMultiplexer multiplexer = new InputMultiplexer();
//        multiplexer.addProcessor(stage);
//        multiplexer.addProcessor(new InputAdapter() {
//            @Override
//            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//                Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
//                float x = worldCoordinates.x;
//                float y = worldCoordinates.y;
//
//                // Left-Click to place a tower.
//                if (button == Input.Buttons.LEFT) {
//                    if (map.isBlockedArea(x, y)) {
//                    	if (playerCoins >= 100) {
//                    		entityManager.addEntity(new Tower(new Vector2(x, y)));
//                    		updateCoins(playerCoins-100);
//                    	} else {
//                    		Gdx.app.log("Game", "Not enough coins to place Tower!");
//                    	}
//                        
//                    	return true;
//                    }
//                }
//                
//                // Right-Click to remove a tower.
//                if (button == Input.Buttons.RIGHT) {
//                    Tower towerToRemove = null;
//                    
//                    for (AbstractEntity entity : entityManager.getEntities()) {
//                        if (entity instanceof Tower) {
//                            Tower tower = (Tower) entity;
//                            float distance = tower.getPosition().dst(x, y);
//
//                            if (distance <= TOWER_REMOVAL_RADIUS) {
//                                towerToRemove = tower;
//                                break;
//                            }
//                        }
//                    }
//
//                    if (towerToRemove != null) {
//                        boolean removed = entityManager.removeEntity(towerToRemove);
//                        
//                        if (removed) {
//                            updateCoins(playerCoins + 100);
//                            
//                        } else {
//                            Gdx.app.log("Game", "Failed to remove tower within range.");
//                        }
//                        
//                    } else {
//                        Gdx.app.log("Game", "No tower found within range!");
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//        
//        // Set the multiplexer as the input processor so that both the stage and custom adapter receive events.
//        Gdx.input.setInputProcessor(multiplexer);
//    }
    // Adjusted input handling to support two phases.
    private void handleInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));
                float x = worldCoordinates.x;
                float y = worldCoordinates.y;
                
                if (currentPhase == GamePhase.PLANNING) {
                    // PLANNING phase: left-click places towers, right-click removes towers.
                    if (button == Input.Buttons.LEFT) {
                        if (map.isBlockedArea(x, y)) {
                            if (playerCoins >= 100) {
                                entityManager.addEntity(new Tower(new Vector2(x, y)));
                                updateCoins(playerCoins - 100);
                                Gdx.app.log("GameScene", "Tower placed. Coins left: " + playerCoins);
                            } else {
                                Gdx.app.log("GameScene", "Not enough coins to place Tower!");
                            }
                        }
                        return true;
                    }
                    if (button == Input.Buttons.RIGHT) {
                        Tower towerToRemove = null;
                        for (AbstractEntity entity : entityManager.getEntities()) {
                            if (entity instanceof Tower) {
                                Tower tower = (Tower) entity;
                                if (tower.getPosition().dst(x, y) <= TOWER_REMOVAL_RADIUS) {
                                    towerToRemove = tower;
                                    break;
                                }
                            }
                        }
                        if (towerToRemove != null && entityManager.removeEntity(towerToRemove)) {
                            updateCoins(playerCoins + 100);
                            Gdx.app.log("GameScene", "Tower removed. Coins: " + playerCoins);
                        } else {
                            Gdx.app.log("GameScene", "No tower found within range!");
                        }
                        return true;
                    }
                } else if (currentPhase == GamePhase.SIMULATION) {
                    // SIMULATION phase: left-click toggles the target flag on a minion.
                    if (button == Input.Buttons.LEFT) {
                        for (AbstractEntity entity : entityManager.getEntities()) {
                            if (entity instanceof Food) {
                                Food minion = (Food) entity;
                                if (minion.getBounds().contains(x, y)) {
                                    boolean newState = !minion.isUserTargeted();
                                    minion.setUserTargeted(newState);
                                    Gdx.app.log("GameScene", "Minion " + minion.getName() + " user-targeted: " + newState);
                                    break; // Toggle only one per click.
                                }
                            }
                        }
                    }
                    // Optionally, you could have right-click untarget the minion:
                    else if (button == Input.Buttons.RIGHT) {
                        for (AbstractEntity entity : entityManager.getEntities()) {
                            if (entity instanceof Food) {
                                Food food = (Food) entity;
                                if (food.getBounds().contains(x, y)) {
                                    food.setUserTargeted(false);
                                    Gdx.app.log("GameScene", "Minion " + food.getName() + " untargeted.");
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }
    
    // Getter for manualTarget; towers can check this to override their default targeting.
    public Targetable getManualTarget() {
        return manualTarget;
    }
    
    @Override
	public void update(float delta) {
		//Only Update Game if NOT Paused,
    	if (!isPaused) {
            // In SIMULATION phase, update simulation game logic.
            if (currentPhase == GamePhase.SIMULATION) {
                entityManager.update(delta);
                if (creature != null) {
                    creature.update(delta);
                }
            }
        }
	    
	 // Count minions that have reached the gameover area.
	    Rectangle gameoverArea = map.getGameoverPoint();
	    if (gameoverArea != null) {
	    	Food collidedMinion = CollisionManager.handleMinionCreatureCollision(entityManager.getEntities(), gameoverArea);
	        if (collidedMinion != null) {
	        	collidedMinion.setHp(0);
	        	entityManager.foodEaten(collidedMinion);
	        	
	        	
	        	//Decrease Health only if Minion = Bad Food,
	        	if (collidedMinion.isBadFood()) {
	        		updateHealth(playerHealth - 1);
	        		
	        		if (playerHealth <= 0) {
	        			showGameOver();
	        			return;
	        		}
	        	}
	        	
	            if (creature != null) {
	                creature.startEating();
	            }
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
        levelLabel.setText("PLANNING");
    	updateHealth(5);
    	updateCoins(300);
    	
	    //reset map
	    if (map != null) {
	        map.dispose();
	        
	    }
	    map = new Map("level.tmx");
	    //restart entity for game
	    if (entityManager != null) {
            entityManager = new EntityManager(gameConfig, camera, map);
            setupEntityManager();
            entityManager.setCurrentLevel(0);
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
	    
	    currentPhase = GamePhase.PLANNING;
	    // Add the planning overlay so the player sees planning instructions again.
	    createPlanningOverlay();
	    
	    Gdx.app.log("GameScene", "Game has been reset and is now in PLANNING phase.");
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        createButtons();
        
        if (entityManager == null) {
            entityManager = new EntityManager(gameConfig, camera, map);
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