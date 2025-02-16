package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Minion target;
    private float speed = 200f; // Projectile speed (higher = faster)
    private int damage;

    public Projectile(float x, float y, Minion target, int damage) {
        this.position = new Vector2(x, y);
        this.target = target;
        this.damage = damage;
    }

    public boolean move(float delta) {
        if (target == null) return true;

        Vector2 direction = new Vector2(target.getX() - position.x, target.getY() - position.y);

        if (direction.len() < 5) {
            target.takeDamage(10);  //
            return true;  //
        }

        direction.nor().scl(speed * delta);
        position.add(direction);
        return false;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for projectiles
        shapeRenderer.circle(position.x, position.y, 5);
    }
}
