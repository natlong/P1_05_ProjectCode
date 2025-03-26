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

import config.GameConfig;

public class OptionsScene extends AbstractOverlay {
	private static int BUTTON_SIZE = GameConfig.getInstance().getButtonSize();

    private Texture volumeOnTexture;
    private Texture volumeOffTexture;
    private ImageButton volumeButton;
    private TextButton backButton;
    private SoundManager soundManager;
    
	private OptionsClosedListener listener;

    public OptionsScene(Skin skin, Stage stage, SoundManager soundManager) {
    	//call skin from parent "Mainmenuscren" 
        super("", skin, stage);
    	this.soundManager = soundManager;
        volumeOnTexture = new Texture(Gdx.files.internal("soundon.png"));
        volumeOffTexture = new Texture(Gdx.files.internal("soundoff.png"));
        initializeContent(skin);
        
        }

    	@Override
        protected Table createContentTable(Skin skin) {
        	Table mainTable = new Table();
    		Label titleLabel = new Label("OPTIONS", skin);
            titleLabel.setAlignment(Align.center);
            
            //music label w white font
            Label.LabelStyle whiteLabelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
            whiteLabelStyle.fontColor = Color.WHITE;
            Label soundLabel = new Label("MUSIC", whiteLabelStyle);
            
        	//to toggle soundon/off
            volumeButton = volumeButton();
            //seperate the soundcontrol layout incase adding more function
            Table musicControl = musicControlTable(soundLabel, volumeButton);

            //back to screen button
            backButton = createStandardButton("BACK", skin);
            
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
		private ImageButton volumeButton() {
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

    @Override
    public void dispose() {
    	super.dispose();
        volumeOnTexture.dispose();
        volumeOffTexture.dispose();
    }
}