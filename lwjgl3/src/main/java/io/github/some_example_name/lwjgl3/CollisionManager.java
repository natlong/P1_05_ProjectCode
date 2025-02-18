package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.List;

public class CollisionManager {
    
	public static void projectileMinionCollision(List<Projectile> projectiles, List<Minion> minions) {
		Iterator<Projectile> projectileIterator = projectiles.iterator();
		while (projectileIterator.hasNext()) {
			Projectile projectile = projectileIterator.next();
			Vector2 projPosition = projectile.getPosition();
			Rectangle projectileBounds = new Rectangle(projPosition.x - 5, projPosition.y - 5, 10, 10);
            
            for (Minion minion : minions) {
                if (projectileBounds.overlaps(minion.getBounds())) {
      		      	 	
                    // Collision detected: apply damage to the minion.
                    minion.takeDamage(projectile.getDamage());
                    // Remove the projectile from the game.
                    projectileIterator.remove();
                    break;
                }
            }
				
		}
	}
}
