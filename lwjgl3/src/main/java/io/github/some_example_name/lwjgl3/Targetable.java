package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Interface for objects that can be targeted by towers and projectiles
 */
public interface Targetable {
    Vector2 getPosition();
    Rectangle getBounds();
    void takeDamage(float damage);
    boolean isDead();
}