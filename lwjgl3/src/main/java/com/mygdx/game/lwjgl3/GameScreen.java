package com.mygdx.game.lwjgl3;

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
	private static final int PAUSE_SIZE = 70;
    private static final int PAUSE_MARGIN = 70;
    
	private DebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Stage stage;
    private boolean isPaused;
    private Map map;
    private ImageButton pauseButton;
    private Minion minion;
    private Music bgMusic;

	    public GameScreen(GameCore game) {
	    	//i crreated this to have a clearer view of the tiled map collision
	    	debugRenderer = new DebugRenderer();
	    	//put to false to close it
	    	debugRenderer.setEnabled(true);
	    	
	        camera = new OrthographicCamera();
	        viewport = new FitViewport(GameCore.VIEWPORT_WIDTH, GameCore.VIEWPORT_HEIGHT, camera);
	        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
	        camera.update();
	
	        batch = new SpriteBatch();
	        stage = new Stage(viewport, batch);
	        Gdx.input.setInputProcessor(stage);
	      
	        initiateMusic();
	       
	    }
	    
	    private void initiateMusic() {
		     // bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/guinea_gavin.mp3"));
		     // bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/garage_climber.mp3"));
		        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/mathematics.mp3"));
	         //	bgMusic = Gdx.audio.newMusic(Gdx.files.internal("BgMusic/burn.mp3"));	      
		        bgMusic.setLooping(true);
		        bgMusic.play();
	    }
      
    private void createPauseButton() {
        // Load the pause button texture
        Texture pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(pauseTexture);

        //create pausebutton
        pauseButton = new ImageButton(drawable);
        pauseButton.setSize(PAUSE_SIZE, PAUSE_SIZE);  // Set button size
        pauseButton.setPosition(viewport.getWorldWidth() - PAUSE_MARGIN, viewport.getWorldHeight() - PAUSE_MARGIN); // Top-right corner

        
      //pause button listeners
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
        stage.addActor(pauseButton);
    }
    
    
    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        map = new Map("level.tmx");
        createPauseButton();
        //minion spawn point
        Rectangle spawnArea = map.getSpawnPoint();
        float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
        float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
        minion = new Minion("monster.png", 0.1f, spawnX, spawnY, map);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            minion.update(delta);}
        map.render(camera);
        debugRenderer.renderDebug(camera, map, minion);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        minion.draw(batch); // Draw the minion
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
        if (bgMusic != null) {
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
	        }
	    }
	
