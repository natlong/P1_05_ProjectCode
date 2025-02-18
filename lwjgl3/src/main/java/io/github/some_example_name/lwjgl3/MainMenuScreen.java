package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class MainMenuScreen implements Screen {
	private static int BUTTON_WIDTH = 200;
	private static int BUTTON_HEIGHT = 50;
	private static int BUTTON_PAD = 10;
	
    private Stage stage;
    private Texture backgroundTexture;
    private Skin skin;
    private GameCore game;
    private SoundManager soundManager;
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    private OptionsScreen optionsScreen;

    
    public MainMenuScreen(GameCore game) {
    	this.game = game;
        stage = new Stage(new FitViewport(GameCore.VIEWPORT_WIDTH, GameCore.VIEWPORT_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        //for Menu background
        backgroundTexture = new Texture(Gdx.files.internal("skin/background.jpg"));
        // for UI Skin
        skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        soundManager = new SoundManager();
        soundManager.playMenuMusic();

        initiateGameUI();
    }
    
    private void initiateGameUI() {
    	Image background = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        background.setFillParent(true);
        stage.addActor(background);
        
        //menu Buttons + UI skins
         playButton = new TextButton("Play", skin);
         optionsButton = new TextButton("Options", skin);
         exitButton = new TextButton("Exit", skin);
        
        //create table for auto layout of the buttons
        Table table = new Table();
        table.setFillParent(true);
        table.center();  

        //add buttons w standard sizing
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
        	soundManager.pause();
            game.setScreen(new GameScreen(game));
            }
        });
    }

    private void optionsButtonListener() {
    optionsButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            Gdx.app.log("MainMenu", "Options Button Clicked");
            if (optionsScreen != null) {
                optionsScreen.dispose();
            }
            optionsScreen = new OptionsScreen(skin, stage, soundManager);
            stage.addActor(optionsScreen);
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
        soundManager.dispose();

    }

    @Override public void show(){}
    @Override public void hide(){}
    @Override public void pause(){}
    
    @Override 
    public void resume() {
    	soundManager.resume();
    	}
}
