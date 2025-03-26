package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import config.GameConfig;

public abstract class AbstractOverlay extends Window {
    //button size used in Gameover and Options
	protected static final int BUTTON_WIDTH = GameConfig.getInstance().getButtonWidth();
    protected static final int BUTTON_HEIGHT = GameConfig.getInstance().getButtonHeight();
    
    public AbstractOverlay(String title, Skin skin, Stage stage) {
        super(title, skin);
        
        // Common window setup
        float windowWidth = stage.getWidth() * 0.4f;
        float windowHeight = stage.getHeight() * 0.3f;
        setModal(true);
        setMovable(false);
        setResizable(false);
        setSize(windowWidth, windowHeight);
        setPosition(
            (stage.getWidth() - windowWidth) / 2,
            (stage.getHeight() - windowHeight) / 2
        );
    }
    
    // to initilize the content of the overlay
    protected void initializeContent(Skin skin) {
        Table mainTable = createContentTable(skin);
        add(mainTable).expand().fill();
    }
    protected abstract Table createContentTable(Skin skin);
    
    
    //standard button creation template, this is using default dimension
    protected TextButton createStandardButton(String text, Skin skin) {
        return createStandardButton(text, skin, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
    
    //this is if u want to change button dimension
    protected TextButton createStandardButton(String text, Skin skin, float width, float height) {
        TextButton button = new TextButton(text, skin);
        return button;
    }
    

    
    public void dispose() {
        clear();
        remove();
    }
}