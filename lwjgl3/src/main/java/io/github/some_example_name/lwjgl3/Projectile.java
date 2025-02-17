package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Minion target;
    private float speed = 200f; // Projectile speed (higher = faster)
    private int damage;
    private float damage;
	private Vector2 position;
	private Vector2 direction;

    public Projectile(Vector2 position, Minion target, float damage) {
        this.position = new Vector2(position);
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
