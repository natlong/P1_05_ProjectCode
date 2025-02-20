package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// game entry point code

public class GameCore extends Game {
	private SpriteBatch batch;
    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 960;
    
   
    @Override
    public void create() {
    	batch = new SpriteBatch();
    	//start with menu screen
        SceneManager.getInstance().setScene(new MainMenuScene(this));
    }
    
    public SpriteBatch getSpriteBatch() {
        return batch;
    }
    
    @Override
    public void render() {
        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        SceneManager.getInstance().render(delta);
    }
    
    @Override
    public void resize(int width, int height) {
        SceneManager.getInstance().resize(width, height);
    }
    

    @Override
    public void dispose() {
    	super.dispose();
        SceneManager.getInstance().getScene().dispose();
        batch.dispose();
    }
}
