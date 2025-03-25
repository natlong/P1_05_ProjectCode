package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollisionManager {
    public static List<AbstractEntity> handleProjectileCollisions(List<AbstractEntity> entities) {
        List<AbstractEntity> toRemove = new ArrayList<>();

        for (AbstractEntity entity : entities) {
            if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                for (AbstractEntity target : entities) {
                    if (target instanceof Minion) {
                        Minion minion = (Minion) target;
                        if (minion.getBounds().overlaps(new Rectangle(
                        		projectile.getPosition().x - 5, 
                        		projectile.getPosition().y - 5, 10, 10))) {
                            minion.takeDamage(projectile.getDamage());
                            toRemove.add(projectile);
                            break;
                        }
                    }
                }
            }
        }

        return toRemove;
    }
    
    public static Minion handleMinionCreatureCollision(List<AbstractEntity> entities, Rectangle gameoverArea) {
        for (Iterator<AbstractEntity> iterator = entities.iterator(); iterator.hasNext();) {
            AbstractEntity entity = iterator.next();
            if (entity instanceof Minion) {
                Minion minion = (Minion) entity;
                if (minion.getBounds().overlaps(gameoverArea)) {
                    return minion;
                }
            }
        }
        return null;
    }
}

