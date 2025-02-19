package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScene implements Screen {
    protected Stage stage;
    protected OrthographicCamera camera;

    public AbstractScene() {
    	camera = new OrthographicCamera();
    	stage = new Stage(new FitViewport(GameCore.VIEWPORT_WIDTH, GameCore.VIEWPORT_HEIGHT, camera));
    	Gdx.input.setInputProcessor(stage);

        init();
    }
    
    // Subclasses must implement this to set up their UI and game objects.
    protected abstract void init();
    
    // Subclasses implement update logic here.
    public abstract void update(float delta);
    
    @Override
    public void render(float delta) {
        update(delta);
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
