package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class DebugRenderer {
    private ShapeRenderer shapeRenderer;
    public boolean isEnabled = true;
    
    public DebugRenderer() {
        shapeRenderer = new ShapeRenderer();
    }
    
    //Render map only
    public void renderMapDebug(OrthographicCamera camera, Map map) {
        if (!isEnabled) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        
        // Draw blocked areas in red
        shapeRenderer.setColor(Color.RED);
        for (Rectangle blockedArea : map.getBlockedAreas()) {
            shapeRenderer.rect(blockedArea.x, blockedArea.y, blockedArea.width, blockedArea.height);
        }
        
        // Draw spawn area in black
        shapeRenderer.setColor(Color.BLACK);
        Rectangle spawnArea = map.getSpawnPoint();
        if (spawnArea != null) {
            shapeRenderer.rect(spawnArea.x, spawnArea.y, spawnArea.width, spawnArea.height);
        }
        
        // Draw walking path in yellow
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle pathArea : map.getWalkingPath()) {
            shapeRenderer.rect(pathArea.x, pathArea.y, pathArea.width, pathArea.height);
        }
        
        // Draw game over area in purple
        shapeRenderer.setColor(Color.PURPLE);
        Rectangle gameoverArea = map.getGameoverPoint();
        if (gameoverArea != null) {
            shapeRenderer.rect(gameoverArea.x, gameoverArea.y, gameoverArea.width, gameoverArea.height);
        }
        
        shapeRenderer.end();
    }
    
    //Render Minion only
    public void renderMinions(OrthographicCamera camera, List<AbstractEntity> entities) {
        if (!isEnabled || entities.isEmpty()) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
       
        shapeRenderer.setColor(Color.GREEN);
        for (AbstractEntity entity : entities) {
            if (entity instanceof Minion) {
                Minion minion = (Minion) entity;
                Rectangle bounds = minion.getBounds();
                shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
        
        shapeRenderer.end();
    }
    
    
    
    public void renderCollisionAreas(OrthographicCamera camera, List<Rectangle> blockedAreas, 
            Rectangle minionBounds, Rectangle spawnArea, 
            List<Rectangle> walkingPath, Rectangle gameoverArea)  {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        
        // Draw blocked areas in red
        shapeRenderer.setColor(Color.RED);
        for (Rectangle blockedArea : blockedAreas) {
            shapeRenderer.rect(blockedArea.x, blockedArea.y, blockedArea.width, blockedArea.height);
        }
        
        // Draw minion bounds in green
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(minionBounds.x, minionBounds.y, minionBounds.width, minionBounds.height);
        
        // Draw spawn area in black
        shapeRenderer.setColor(Color.BLACK);
        if (spawnArea != null) {
            shapeRenderer.rect(spawnArea.x, spawnArea.y, spawnArea.width, spawnArea.height);
        }
        
        // Draw walking path in yellow
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle pathArea : walkingPath) {
            shapeRenderer.rect(pathArea.x, pathArea.y, pathArea.width, pathArea.height);
        }
        
        // Draw game over area in purple
        shapeRenderer.setColor(Color.PURPLE);
        if (gameoverArea != null) {
            shapeRenderer.rect(gameoverArea.x, gameoverArea.y, gameoverArea.width, gameoverArea.height);
        }
        
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
        renderCollisionAreas(camera, 
                map.getBlockedAreas(), 
                minionBounds,
                map.getSpawnPoint(),
                map.getWalkingPath(),
                map.getGameoverPoint()
            );
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