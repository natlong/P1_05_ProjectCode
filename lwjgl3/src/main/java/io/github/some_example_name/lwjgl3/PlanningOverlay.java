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
        float newHeight = stage.getHeight() * 0.3f;
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
        Label instructions = new Label("PLANNING PHASE\n\nPlace towers (Left click to place, Right click to remove).\nReview these instructions carefully.", skin);
        instructions.setWrap(true);
        instructions.setAlignment(Align.center);
        
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
        
        mainTable.add(instructions).width(getWidth() - 20).padBottom(20).row();
        mainTable.add(closeButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        
        return mainTable;
    }
    
    public void setPlanningOverlayListener(PlanningOverlayListener listener) {
        this.listener = listener;
    }
}
