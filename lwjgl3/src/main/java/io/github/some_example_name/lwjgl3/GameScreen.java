package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Collections;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.InputAdapter;
import java.util.List;
import java.util.Iterator;


public class GameScreen implements Screen {
	private static final int BUTTON_SIZE = 60;
    private static final int BUTTON_MARGIN = 60;
    private static final int BUTTON_SPACING = 1;
    
	private DebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Stage stage;
    private boolean isPaused;
    private boolean isMuted;
    private Map map;
    private ImageButton pauseButton;
    private ImageButton settingsButton;
    private ImageButton volumeButton;
    private Texture volumeOnTexture;
    private Texture volumeOffTexture;
    private Minion minion;
    private Music bgMusic;
    
    private ShapeRenderer shapeRenderer;
    private EntityManager entityManager;


    public GameScreen(GameCore game) {
    	//i created this to have a clearer view of the tiled map collision
    	debugRenderer = new DebugRenderer();
    	//put to false to close it
    	debugRenderer.setEnabled(false);
    	
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameCore.VIEWPORT_WIDTH, GameCore.VIEWPORT_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        batch = new SpriteBatch();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
        
        entityManager = new EntityManager();
        
        handleInput();
        initiateMusic();
    }
	    
    private void initiateMusic() {
	      	bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/guinea_gavin.mp3"));
	      	bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/garage_climber.mp3"));
	        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/mathematics.mp3"));
	        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/burn.mp3"));	      
	        bgMusic.setLooping(true);
	        bgMusic.play();
    }
      
   
	    
    private void createButtons() {
        // Load the pause button texture
        Texture pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        Texture settingsTexture = new Texture(Gdx.files.internal("settings.png"));
        volumeOnTexture = new Texture(Gdx.files.internal("soundon.png"));
        volumeOffTexture = new Texture(Gdx.files.internal("soundoff.png"));
        
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(pauseTexture);
        TextureRegionDrawable settingsDrawable = new TextureRegionDrawable(settingsTexture);
        TextureRegionDrawable volumeOnDrawable = new TextureRegionDrawable(volumeOnTexture);
        

        //create buttons
        pauseButton = new ImageButton(pauseDrawable);
        settingsButton = new ImageButton(settingsDrawable);
        volumeButton = new ImageButton(volumeOnDrawable);
        
        //button size
        pauseButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        settingsButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        volumeButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        
        //button position, top down arrangement
        float topY = viewport.getWorldHeight() - BUTTON_MARGIN;
        float rightX = viewport.getWorldWidth() - BUTTON_MARGIN;
        pauseButton.setPosition(rightX - BUTTON_SIZE, topY);
        volumeButton.setPosition(rightX - BUTTON_SIZE, topY - BUTTON_SIZE - BUTTON_SPACING);
        settingsButton.setPosition(rightX - BUTTON_SIZE, topY - (2 * BUTTON_SIZE) - (2 * BUTTON_SPACING));
        
        pauseButtonListener();
        settingsButtonListener();
        volumeButtonListener();
        
        stage.addActor(pauseButton);
        stage.addActor(settingsButton);
        stage.addActor(volumeButton);
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
    private void volumeButtonListener() {
    	volumeButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                toggleVolume();
                return true;
            }
        });
    }  

    private void openSettings() {
        Gdx.app.log("GameScreen", "Settings button clicked");
    }
    
    private void toggleVolume() {
        isMuted = !isMuted;
        if (isMuted) {
            bgMusic.setVolume(0f);
            volumeButton.getStyle().imageUp = new TextureRegionDrawable(volumeOffTexture);
        } else {
            bgMusic.setVolume(1f);
            volumeButton.getStyle().imageUp = new TextureRegionDrawable(volumeOnTexture);
        }
    }

    // Handles player input for placing towers
    private void handleInput() {
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float x = camera.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).x;
                float y = camera.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).y;

                if (!map.isGreenArea(x, y)) {
                    entityManager.addTower(x, y);
                }
                return true;
            }
        });
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        map = new Map("level.tmx");
        createButtons();
        
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        
        //minion spawn point
        Rectangle spawnArea = map.getSpawnPoint();
        float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
        float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
        entityManager.spawnMinion("monster.png", spawnX, spawnY, map, 100f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            entityManager.update(delta);
        }

        map.render(camera);
        entityManager.render(batch, shapeRenderer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume() called");
        if (bgMusic != null && !isMuted) {
            bgMusic.play();
        }
        isPaused = false;
        }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause() called");
        if (bgMusic != null) {
            bgMusic.pause();
        }
        isPaused = true;
    }
    
    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide() called");
    }

    @Override
    public void dispose() {
        minion.dispose(); 
        batch.dispose();
        stage.dispose();
        bgMusic.dispose();
        map.dispose();
        debugRenderer.dispose ();
        volumeOnTexture.dispose();
        volumeOffTexture.dispose();
        shapeRenderer.dispose();
        }
    }
	
