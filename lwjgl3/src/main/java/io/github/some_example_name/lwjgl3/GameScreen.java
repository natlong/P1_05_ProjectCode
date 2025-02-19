package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;




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
    private Minion minion;
    private SoundManager soundManager; 
    private OptionsScreen optionsScreen;
    private GameoverScreen gameoverScreen;
    
    private ShapeRenderer shapeRenderer;
    private EntityManager entityManager;
    
    //Text Label,
    private Label levelLabel;
    private Label lifeLabel;
    private Label coinLabel;
    private Table infoTable;
    
    private int currentLevel = 1;
    private int playerLife = 5;
    private int playerCoin = 300;
    
    private Array<Image> heartImage;
    private Texture heartTexture;


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
        
        map = new Map("level.tmx");
        
        entityManager = new EntityManager(map, camera);
        shapeRenderer = new ShapeRenderer();
        handleInput();
        soundManager = new SoundManager();
        soundManager.playGameMusic();
        

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
        float topY = viewport.getWorldHeight()-60;
        float rightX = viewport.getWorldWidth() - 5;
        pauseButton.setPosition(rightX - BUTTON_SIZE, topY);
        settingsButton.setPosition(rightX - BUTTON_SIZE, topY - BUTTON_SIZE);
        
        pauseButtonListener();
        settingsButtonListener();
        
        stage.addActor(pauseButton);
        stage.addActor(settingsButton);
        
        //Display Level Label,
        Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        levelLabel = new Label("Level " + currentLevel, skin);
        levelLabel.setColor(Color.WHITE);
        levelLabel.setFontScale(1.5f);
        
        //Position at Top Centre,
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        table.add(levelLabel).padTop(10).center();
        
        stage.addActor(table);
    }
    
    private void createInfoTable() {
    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
    	heartTexture = new Texture(Gdx.files.internal("heart.png"));
    	
    	heartImage = new Array<>();
    	
    	//Creating Table for Player Life,
    	Table heartTable = new Table();
    	heartTable.pad(10);
    	
    	//Add Spacing between Heart Image,
    	float heartContainer = 200;
    	float heartSpacing = (heartContainer - (playerLife * 50)) / (playerLife - 1);
    	
    	//Displaying Heart Image,
    	for (int i = 0; i < playerLife; i++) {
            Image heart = new Image(new TextureRegionDrawable(new TextureRegion(heartTexture)));
            heart.setSize(50, 50);
            heartImage.add(heart);
            
            if (i > 0) { 
                heartTable.add().width(heartSpacing);
            }

            heartTable.add(heart).width(50).height(50);
        }
    	
    	//Add Labels to Table,
    	coinLabel = new Label("Coins: " + playerCoin, skin);
    	
    	//Creating Table for Coin Label,
    	Table coinTable = new Table();
    	coinTable.add(coinLabel).pad(10).padLeft(50).left();
    	
    	//Adding Heart Image and Coin Container together,
    	infoTable = new Table();
    	infoTable.bottom().right();
    	infoTable.add(heartTable).row();
    	infoTable.add(coinTable).expandX().left();
    	
    	//Container for Storing Variable,
    	Container<Table> container = new Container<>(infoTable);
        container.setSize(250, 100);
        container.setPosition(Gdx.graphics.getWidth() - container.getWidth() - 10, 10);

        //Setting Color and Transparency for Table,
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888); //Creating a 1 by 1 Pixmap,
        pixmap.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f)); //Grey + Semi-Transparent,
        pixmap.fill();
 
        //Creating Texture from Pixmap,
        Texture greyTexture = new Texture(pixmap);
        pixmap.dispose();
        container.background(new TextureRegionDrawable(new TextureRegion(greyTexture)));

    	stage.addActor(container);
    }
    
    //Dynamically Set Levels {Future Use},
    public void setLevel() {
    	currentLevel++;
    	levelLabel.setText("Level " + currentLevel);
    }
    
    //Dynamically Update Player Life and Tower Limit Label,
    private void updateInfoLabels() {
    	for (Image heart : heartImage) {
            heart.remove();
        }
    	
        heartImage.clear();
        
        //Add Spacing between Heart Image,
        Table heartTable = new Table();
        float heartContainerWidth = 200;
        float heartSpacing = (heartContainerWidth - (playerLife * 50)) / (playerLife - 1);

        //Displaying Heart Image,
        for (int i = 0; i < playerLife; i++) {
            Image heart = new Image(new TextureRegionDrawable(new TextureRegion(heartTexture)));
            heart.setSize(50, 50);
            heartImage.add(heart);
            
            if (i > 0) {
                heartTable.add().width(heartSpacing);
            }

            heartTable.add(heart).width(50).height(50);
        }

        //Updating Coin Label,
        coinLabel.setText("Coins: " + playerCoin);
        
    	//Creating Table for Coin Label,
    	Table coinTable = new Table();
    	coinTable.add(coinLabel).pad(10).padLeft(50).left();
        
        infoTable.clearChildren();
        infoTable.add(heartTable).row();
    	infoTable.add(coinTable).expandX().left();

        infoTable.layout();
    }
    
    //Dynamically Update Player Life Label {Future Use},
    private void updatePlayerLife(int x) {
    	playerLife = Math.max(0, x);
        updateInfoLabels();
    }

    //Dynamically Update Tower Limit Label {Future Use},
    private void updatePlayerCoin(int x) {
    	playerCoin = x;
    	coinLabel.setText("Coins: " + playerCoin);
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
        optionsScreen = new OptionsScreen(skin, stage, soundManager);
        stage.addActor(optionsScreen);
    }
    
    private void showGameOver() {
    	if (gameoverScreen == null){
    	Skin skin = new Skin(Gdx.files.internal("skin/lgdxs-ui.json"));
        gameoverScreen = new GameoverScreen(skin, stage, this);
        stage.addActor(gameoverScreen);}
    }

    //Player Input for Tower,
    private void handleInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
        	
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float x = camera.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).x;
                float y = camera.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).y;

                //Left-Click to Place Tower,
                if (button == Input.Buttons.LEFT) {
                    if (!map.isGreenArea(x, y)) {
                    	int towerCost = 50; //Tower Price {For Testing},
                    	
                    	if (playerCoin >= towerCost) {
                    		playerCoin -= towerCost;
                    		updatePlayerCoin(playerCoin);
                    		entityManager.addTower(new Vector2(x, y));
                    	} else {
                    		Gdx.app.log("GameScreen", "Not enough coins to place a Tower!");
                    	}
                    	
                        
                    }
                }
                
                //Right-Click to Remove Tower,
                if (button == Input.Buttons.RIGHT) {
                	int towerRefund = 50; //Tower Refund for Removal {For Testing},
                	
                	playerCoin += towerRefund;
                	updatePlayerCoin(playerCoin);
                    entityManager.removeTower(new Vector2(x, y));
                }

                return true;
            }
        });
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show() called");
        //map = new Map("level.tmx");
        createButtons();
        createInfoTable();
        
        if (entityManager == null) {
            entityManager = new EntityManager(map, camera);
        }
        
        //minion spawn point
        //Rectangle spawnArea = map.getSpawnPoint();
        //float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
        //float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
        //Vector2 position = new Vector2(spawnX, spawnY);
        //entityManager.spawnMinion("monster.png", 0.1f, position, map, 100f, camera);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            entityManager.update(delta);
        }
        // need to integrate chloe code
//        if (!isPaused) {
//            minion.update(delta); 
//            if (minion.hitGameoverArea()) {
//                showGameOver();
//                pause();} }
        
//        map.render(camera);
//        debugRenderer.renderDebug(camera, map, minion);
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        minion.draw(batch);
//        batch.end();      
//        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
//        stage.draw();

        
        map.render(camera);
        entityManager.render(batch, shapeRenderer);

        //Update and Rendering Stage,
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/60f));
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
    
//    public void resetGame() {
//	    //reset map
//	    if (map != null) {
//	        map.dispose();
//	        map = new Map("level.tmx");
//	    }
//	    
//	    //reset minion position
//	    if (minion != null) {
//	        Rectangle spawnArea = map.getSpawnPoint();
//	        float spawnX = spawnArea.x + (float)(Math.random() * spawnArea.width);
//	        float spawnY = spawnArea.y + (float)(Math.random() * spawnArea.height);
//	        minion.resetPosition(spawnX, spawnY);
//	        }
//	    
//	    if (soundManager != null) {
//	        soundManager.restartCurrentMusic();
//	    }
//	    
//	    gameoverScreen = null;
//	    }

    @Override
    public void dispose() {
        minion.dispose(); 
        batch.dispose();
        stage.dispose();
        map.dispose();
        debugRenderer.dispose ();
        soundManager.dispose();
        shapeRenderer.dispose();
        }
    }
	
