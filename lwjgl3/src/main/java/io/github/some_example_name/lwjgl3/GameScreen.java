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

public class GameScreen implements Screen {
	private static final int BUTTON_SIZE = 60;
    private static final int BUTTON_MARGIN = 60;
    private static final int BUTTON_SPACING = 1;
    private float maxHP = 100;
    
	private DebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Stage stage;
    private boolean isPaused;
    private Map map;
    private ImageButton pauseButton;
    private ImageButton settingsButton;
    private ImageButton volumeButton;
    private Texture volumeOnTexture;
    private Texture volumeOffTexture;
    private Minion minion;
    private SoundManager soundManager; 

	    public GameScreen(GameCore game) {
	    	//i crreated this to have a clearer view of the tiled map collision
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
	        soundManager = new SoundManager();
	      
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
    private void openSettings() {
        Gdx.app.log("GameScreen", "Settings button clicked");
    }
      
    private void volumeButtonListener() {
    	volumeButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                toggleSound();
                return true;
            }
        });
    }  
    
    
    private void toggleSound() {
    	soundManager.toggleSound();
        if (soundManager.isMuted()) {
            volumeButton.getStyle().imageUp = new TextureRegionDrawable(volumeOffTexture);
        } else {
            volumeButton.getStyle().imageUp = new TextureRegionDrawable(volumeOnTexture);
        }
    }
    
    
    
    
    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        map = new Map("level.tmx");
        createButtons();
        
        //minion spawn point
        Rectangle spawnArea = map.getSpawnPoint();
        float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
        float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
        minion = new Minion("monster.png", 0.1f, spawnX, spawnY, map, maxHP);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            minion.update(delta); }
            
        map.render(camera);
        debugRenderer.renderDebug(camera, map, minion);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        minion.draw(batch);
        batch.end();      
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
        }
    
    

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        minion.dispose(); 
        batch.dispose();
        stage.dispose();
        soundManager.dispose();
        map.dispose();
        debugRenderer.dispose ();
        volumeOnTexture.dispose();
        volumeOffTexture.dispose();
	        }
	    }
	
