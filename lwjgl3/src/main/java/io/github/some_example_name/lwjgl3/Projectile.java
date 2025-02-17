package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Minion target;
    private float speed = 200f; // Projectile speed (higher = faster)
    private float damage;
	private Vector2 position;
	private Vector2 direction;

    public Projectile(Vector2 position, Minion target, float damage) {
        this.position = new Vector2(position);
        this.target = target;
        this.damage = damage;
        this.direction = new Vector2(target.getPosition().x - position.x, target.getPosition().y - position.y);
    }

    public boolean move(float delta) {
        if (target == null) return true;

        if (position.dst(target.getPosition()) < speed * delta) {
            target.takeDamage(this.damage);  //
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
