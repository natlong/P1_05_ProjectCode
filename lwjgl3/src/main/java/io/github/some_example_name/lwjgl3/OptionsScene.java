package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.Align;

public class OptionsScene extends Window {
	private static int BUTTON_SIZE = 60;
    private static int BUTTON_WIDTH = 200;
    private static int BUTTON_HEIGHT = 50;
    
    private Texture volumeOnTexture;
    private Texture volumeOffTexture;
    private ImageButton volumeButton;
    private TextButton backButton;
    private SoundManager soundManager;
    
	private OptionsClosedListener listener;

    public OptionsScene(Skin skin, Stage stage, SoundManager soundManager) {
    	//call skin from parent "Mainmenuscren" 
        super("", skin);
    	this.soundManager = soundManager;
        volumeOnTexture = new Texture(Gdx.files.internal("soundon.png"));
        volumeOffTexture = new Texture(Gdx.files.internal("soundoff.png"));
        
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

        Table mainTable = contentInTable(skin, soundManager);
        add(mainTable).expand().fill(); 
        
        }


        private Table contentInTable(Skin skin, SoundManager soundManager) {
        	Table mainTable = new Table();
    		Label titleLabel = new Label("OPTIONS", skin);
            titleLabel.setAlignment(Align.center);
            
            //music label w white font
            Label.LabelStyle whiteLabelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
            whiteLabelStyle.fontColor = Color.WHITE;
            Label soundLabel = new Label("MUSIC", whiteLabelStyle);
            
        	//to toggle soundon/off
            volumeButton = volumeButton(soundManager);
            //seperate the soundcontrol layout incase adding more function
            Table musicControl = musicControlTable(soundLabel, volumeButton);

            //back to screen button
            backButton = new TextButton("BACK", skin);
            
            mainTable.add(titleLabel).padTop(20).expandX().center().row();
            mainTable.add(musicControl).expandX().center().padTop(40).row();
            mainTable.add(backButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padTop(40);
            
            soundButtonListener();
            backButtonListener();
            
            return mainTable;
        }


		private Table musicControlTable(Label soundLabel, ImageButton volumeButton) {
            Table musicControl = new Table();
            musicControl.add(soundLabel).padRight(20);
            musicControl.add(volumeButton).size(BUTTON_SIZE);
            return musicControl;
		}
	
		//image of sound on off button
		private ImageButton volumeButton(SoundManager soundManager) {
            TextureRegionDrawable volumeImage;
            ImageButton.ImageButtonStyle volumeStyle = new ImageButton.ImageButtonStyle();
            if (soundManager.isMuted()) {
                volumeImage = new TextureRegionDrawable(volumeOffTexture);
            } else {
                volumeImage = new TextureRegionDrawable(volumeOnTexture);
            }
            volumeStyle.imageUp = volumeImage;
            return new ImageButton(volumeStyle);
            }
		

		private void soundButtonListener() {
	        volumeButton.addListener(new ChangeListener() {
	            @Override
	            public void changed(ChangeEvent event, Actor actor) {
	                soundManager.toggleSound();
	                TextureRegionDrawable newImage;
	                if (soundManager.isMuted()) {
	                    newImage = new TextureRegionDrawable(volumeOffTexture);
	                } else {
	                    newImage = new TextureRegionDrawable(volumeOnTexture);
	                }
	                volumeButton.getStyle().imageUp = newImage;
	            }
	        });
		}

		//Interface for Options Listener,
		public interface OptionsClosedListener {
			void onOptionsClosed();
		}
		
		//Method to Set Listener for GameCore,
		public void setOptionsClosedListener(OptionsClosedListener x) {
			this.listener = x;
		}
		
		//Update Back Button for Options Menu,
        private void backButtonListener() {
            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (listener != null) {
                    	listener.onOptionsClosed();
                    }
                	
                	remove();
                }
            });
			
		}


    public void dispose() {
        volumeOnTexture.dispose();
        volumeOffTexture.dispose();
    }
}