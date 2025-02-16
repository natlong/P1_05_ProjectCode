package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

public class Tower {
    private float x, y;
    private float range = 250f;  // Detection range
    private float fireRate = 1.0f; // 1 shot per second
    private float cooldown = 0;

    public Tower(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void shoot(List<Projectile> projectiles, List<Minion> minions, float delta) {
        if (cooldown <= 0) {
            Minion target = findTarget(minions);
            if (target != null) {
                projectiles.add(new Projectile(x, y, target, 10)); // Fire projectile at minion
                cooldown = fireRate;
            }
        } else {
            cooldown -= delta;
        }
    }

    private Minion findTarget(List<Minion> minions) {
        Minion closest = null;
        float closestDistance = range;

        for (Minion minion : minions) {
            float distance = Vector2.dst(x, y, minion.getX(), minion.getY());
            if (distance < closestDistance) {
                closest = minion;
                closestDistance = distance;
            }
        }
        return closest;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0, 0, 1, 1); // Blue color for tower
        shapeRenderer.rect(x - 15, y - 15, 30, 30);
    }
}
