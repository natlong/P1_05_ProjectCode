package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class HPBar {
    private Rectangle bounds;
    private float currentHP;
    private float maxHP;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    public HPBar(float x, float y, float width, float height, float maxHP, OrthographicCamera camera) {
        this.bounds = new Rectangle(x, y, width, height);
        this.currentHP = maxHP;
        this.maxHP = maxHP;
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    public void takeDamage(float damage) {
        currentHP -= damage;
        if (currentHP < 0) {
            currentHP = 0; // Don't let HP go negative
        }
    }

    public boolean isDead() {
        return currentHP <= 0;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background (grey)
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Health bar (green)
        float healthPercentage = currentHP / maxHP;
        float healthBarWidth = bounds.width * healthPercentage;
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, healthBarWidth, bounds.height);

        //shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}