package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
//    private GameCore game;
    private boolean isPaused;


    public GameScene(GameCore game) {
    	super();
//    	this.game = game;
    	
//    	//i created this to have a clearer view of the tiled map collision
    	debugRenderer = new DebugRenderer();
    	//put to false to close it
    	debugRenderer.setEnabled(false);
    }
    
    protected void init() {
         camera.position.set(GameCore.VIEWPORT_WIDTH / 2, GameCore.VIEWPORT_HEIGHT / 2, 0);
         camera.update();
         
         batch = new SpriteBatch();
         shapeRenderer = new ShapeRenderer();
         
         // load tilemap
         map = new Map("level.tmx");
         entityManager = new EntityManager(camera, map);
         soundManager = new SoundManager();
         soundManager.playGameMusic();
         
         createButtons();
         handleInput();
    }
   
	    
    private void createButtons() {
        // Load the pause button texture
        Texture pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        Texture settingsTexture = new Texture(Gdx.files.internal("settings.png"));

        
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(pauseTexture);
        TextureRegionDrawable settingsDrawable = new TextureRegionDrawable(settingsTexture);
        

        //create buttons
        pauseButton = new ImageButton(pauseDrawable);
        settingsButton = new ImageButton(settingsDrawable);
        
        //button size
        pauseButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        settingsButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        
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

        
    private void pauseButtonListener() {
        pauseButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (isPaused) {
                    resume();
                } else {
                    pause();
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
        if (optionsScreen != null) {
            optionsScreen.dispose();
        }
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        optionsScreen = new OptionsScene(skin, stage, soundManager);
        stage.addActor(optionsScreen);
    }
    
    @Override
	public void update(float delta) {
    	if (!isPaused) {
            entityManager.update(delta);
            
         // Count minions that have reached the gameover area.
            int count = 0;
            Rectangle gameoverArea = map.getGameoverPoint();
            if (gameoverArea != null) {
                for (AbstractEntity entity : entityManager.getEntities()) {
                	if (entity instanceof Minion && ((Minion) entity).getBounds().overlaps(gameoverArea)) {
                        count++;
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
                    if (!map.isGreenArea(x, y)) {
                        entityManager.addEntity(new Tower(new Vector2(x, y)));
                    }
                }
                // Right-Click to remove a tower.
                if (button == Input.Buttons.RIGHT) {
                    entityManager.removeEntity(new Tower(new Vector2(x, y)));
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
            entityManager = new EntityManager(camera, map);
        }
    }

    @Override
    public void render(float delta) {
    	// Call update logic
        update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        map.render(camera);
        entityManager.render(shapeRenderer);
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
        soundManager.dispose();
        shapeRenderer.dispose();
	}
}