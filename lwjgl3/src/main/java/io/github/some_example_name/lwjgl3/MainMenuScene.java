package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainMenuScene extends AbstractScene {
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_PAD = 10;
	
    private Texture backgroundTexture;
    private Skin skin;
    private GameCore game;
    private SoundManager soundManager;
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    private OptionsScene optionsScene;

    
    public MainMenuScene(GameCore game) {
    	super();
    	this.game = game;
    }
    
    @Override
    protected void init() {        
    	// Load assets.
    	backgroundTexture = new Texture(Gdx.files.internal("skin/background.jpg"));
    	Gdx.app.log("MainMenu", "Background texture size: " + backgroundTexture.getWidth() + "x" + backgroundTexture.getHeight());
        skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        soundManager = SoundManager.getInstance();
        soundManager.playMenuMusic();
        
        // Set up background.
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        background.setFillParent(true);
        stage.addActor(background);
        
        // Create table.
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground((Drawable) null);  // Ensure table background is transparent.
        table.center();
        
        playButton = new TextButton("Play", skin);
        optionsButton = new TextButton("Options", skin);
        exitButton = new TextButton("Exit", skin);
        
        table.add(playButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();
        table.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();
        table.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD);
        stage.addActor(table);
        
        playButtonListener();
        optionsButtonListener();
        exitButtonListener();
    }
    
    //button listeners
    private void playButtonListener() {
    playButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
        	Gdx.app.log("MainMenu", "Start Button Clicked");
            SceneManager.getInstance().setScene(new GameScene(game));;
            }
        });
    }

    private void optionsButtonListener() {
    optionsButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.log("MainMenu", "Options Button Clicked");
            if (optionsScene != null) {
                optionsScene.dispose();
            }
            optionsScene = new OptionsScene(skin, stage, soundManager);
            stage.addActor(optionsScene);
        }
    });
    }
    
    private void exitButtonListener() {
    exitButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
        	Gdx.app.log("MainMenu", "Exit Button Clicked");
            Gdx.app.exit();
        }
    });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        skin.dispose();

    }

    @Override public void show(){}
    @Override public void hide(){}
    @Override public void pause(){}
    
    @Override 
    public void resume() {
    	soundManager.resume();
    	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}
}
