package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class PlanningOverlay extends AbstractOverlay {
    // Listener interface to notify when the overlay is closed.
    public interface PlanningOverlayListener {
        void onClose();
    }
    
    private PlanningOverlayListener listener;
    
    public PlanningOverlay(String title, Skin skin, com.badlogic.gdx.scenes.scene2d.Stage stage) {
        super(title, skin, stage);
        // Adjust size: Make overlay larger than the default.
        float newWidth = stage.getWidth() * 0.6f;
        float newHeight = stage.getHeight() * 0.5f;
        setSize(newWidth, newHeight);
        setPosition((stage.getWidth() - newWidth) / 2, (stage.getHeight() - newHeight) / 2);
        // Optionally, for debugging, set a semi-transparent background color:
        // this.setColor(1, 0, 0, 0.3f);
        initializeContent(skin);
    }
    
    @Override
    protected Table createContentTable(Skin skin) {
        Table mainTable = new Table();
        
        // Create an instructions label with wrapping enabled.
        String instructionsText = 
        		"[#FF0000]PLANNING PHASE[/#FF0000] \n\n" +
        		"Place towers - Left click to place \n " +
        		"Remove towers - Right click to remove \n\n " + 
        		"Player would not be able to place tower after starting game. \n\n" +
        		
        		"[#FF0000]GAME PHASE[/#FF0000] \n " + 
        		"Target food - Left click to select target \n\n " + 
        		"Review these instructions carefully.";
        
        Label planningHeading = new Label("PLANNING PHASE", skin);
        planningHeading.setColor(1, 0, 0, 1);
        
        Label planningContent = new Label(
        		"Place towers - Left click to place \n " +
        		"Remove towers - Right click to remove \n\n " + 
        		"Player would not be able to place tower outside of planning phase. \n\n", skin);
        
        Label gameHeading = new Label("GAME PHASE", skin);
        gameHeading.setColor(1, 0, 0, 1);
        
        Label gameContent = new Label(
        		"Target food - Left click to select target \n\n " + 
                "Review these instructions carefully.", skin);
        
        //mainTable.add(planningHeading).row();
        //mainTable.add(planningContent).row();
        
        
        //Label instructions = new Label(instructionsText, skin);
        planningHeading.setWrap(true);
        planningHeading.setAlignment(Align.center);
        planningContent.setWrap(true);
        planningContent.setAlignment(Align.center);
        gameHeading.setWrap(true);
        gameHeading.setAlignment(Align.center);
        gameContent.setWrap(true);
        gameContent.setAlignment(Align.center);
        
        
        // Create the CLOSE button.
        TextButton closeButton = createStandardButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (listener != null) {
                    listener.onClose();
                }
                remove(); // Dismiss the overlay.
            }
        });
        
        //mainTable.add(instructions).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(planningHeading).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(planningContent).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(gameHeading).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(gameContent).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(closeButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        
        return mainTable;
    }
    
    public void setPlanningOverlayListener(PlanningOverlayListener listener) {
        this.listener = listener;
    }
}
