package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


// game entry point code, lol can change file name but rmb to edit the LWJGL and other file using this class name

public class GameCore extends Game {
	private SpriteBatch batch;
    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 960;


    @Override
    public void create() {
    	batch = new SpriteBatch();
    	//start with menu screen
        setScreen(new MainMenuScreen(this));
    }
    
    public SpriteBatch getSpriteBatch() {
        return batch;
    }
    

    @Override
    public void dispose() {
    	super.dispose(); 
        batch.dispose();
    }
}
