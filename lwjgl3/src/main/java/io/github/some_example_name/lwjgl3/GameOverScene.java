package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class GameOverScene extends Window {
    private static int BUTTON_WIDTH = 200;
    private static int BUTTON_HEIGHT = 50;
    
    private TextButton retryButton;
    private TextButton exitButton;
    private GameScene gameScene; 

    public GameOverScene(Skin skin, Stage stage, GameScene gameScene) {
    	//call skin from parent "GameScreen" 
        super("", skin);
        this.gameScene = gameScene;

        //size/position/behavior of the popup
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
        

        Table mainTable = contentInTable(skin);
        add(mainTable).expand().fill(); 
        
        }


        private Table contentInTable(Skin skin) {
        	Table mainTable = new Table();
    		Label titleLabel = new Label("GAMEOVER", skin);
    		titleLabel.setColor(Color.RED);
            titleLabel.setAlignment(Align.center);
            

            //gameover buttons
            retryButton = new TextButton("RETRY", skin);
            exitButton = new TextButton("EXIT", skin);
            
            mainTable.add(titleLabel).padTop(20).expandX().center().row();
            mainTable.add(retryButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padTop(40).row();
            mainTable.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padTop(20);
            
            retryButtonListener();
            exitButtonListener();
            
            return mainTable;
        }


        private void exitButtonListener() {
        	exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
		}
        
        private void retryButtonListener() {
        	retryButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dispose();
                    gameScene.resetGame();
                    gameScene.resume();
                }
            });
		}
		

    public void dispose() {
    	retryButton.clear();
    	exitButton.clear();
    	clear();
    	remove();
    }
}

