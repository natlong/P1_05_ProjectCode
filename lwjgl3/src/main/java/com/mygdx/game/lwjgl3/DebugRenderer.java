package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class DebugRenderer {
    private ShapeRenderer shapeRenderer;
    private boolean isEnabled = true;
    
    public DebugRenderer() {
        shapeRenderer = new ShapeRenderer();
    }
    
    public void renderCollisionAreas(OrthographicCamera camera, List<Rectangle> blockedAreas, Rectangle entityBounds) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        
        // Draw blocked areas in red
        shapeRenderer.setColor(Color.RED);
        for (Rectangle blockedArea : blockedAreas) {
            shapeRenderer.rect(blockedArea.x, blockedArea.y, blockedArea.width, blockedArea.height);
        }
        
        // Draw entity bounds in green
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(entityBounds.x, entityBounds.y, entityBounds.width, entityBounds.height);
        
        shapeRenderer.end();
    }
    
    public void renderDebug(OrthographicCamera camera, Map map, Minion minion) {
        if (!isEnabled) return;

        // Create minion bounds inside the method
        Rectangle minionBounds = new Rectangle(
            minion.getPosition().x, 
            minion.getPosition().y, 
            minion.getWidth(), 
            minion.getHeight()
        );
        renderCollisionAreas(camera, map.getBlockedAreas(), minionBounds);
    }
    
        public void setEnabled(boolean enabled) {
            this.isEnabled = enabled;
        }
    
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}