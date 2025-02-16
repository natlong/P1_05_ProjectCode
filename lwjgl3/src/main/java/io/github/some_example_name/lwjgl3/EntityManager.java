package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EntityManager is responsible for managing and updating all game entities as per our UML diagram:
 * - Towers (placed by the player)
 * - Minions (enemy units moving through the path)
 * - Projectiles (fired by towers at minions)
 */

public class EntityManager {
    private List<Tower> towers;
    private List<Minion> minions;
    private List<Projectile> projectiles;

    public EntityManager() {
        this.towers = new ArrayList<>();
        this.minions = new ArrayList<>();
        this.projectiles = new ArrayList<>();
    }

    public void addTower(float x, float y) {
        towers.add(new Tower(x, y));
    }

    public void spawnMinion(String spritePath, float startX, float startY, Map map, float maxHP) {
        minions.add(new Minion(spritePath, 0.1f, startX, startY, map, maxHP));
    }

    // Updates all game entities (minions, towers, and projectiles)
    public void update(float delta) {
        for (Minion minion : minions) {
            minion.update(delta);
        }

        // Towers shoot at minions (generates new projectiles)
        for (Tower tower : towers) {
            tower.shoot(projectiles, minions, delta);
        }

        // Move projectiles and remove any that reach the minion
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().move(delta)) {
                iterator.remove();
            }
        }
    }
    
    // Renders all game entities on the screen
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        for (Minion minion : minions) {
            minion.draw(batch);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Tower tower : towers) {
            tower.draw(shapeRenderer);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public void dispose() {
        for (Minion minion : minions) {
            minion.dispose();
        }
    }
}