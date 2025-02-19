package io.github.some_example_name.lwjgl3;

public class SceneManager {
    private static SceneManager instance;
    private AbstractScene currentScene;
    
    private SceneManager() { }
    
    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    public void setScene(AbstractScene scene) {
        if(currentScene != null) {
            currentScene.dispose();
        }
        currentScene = scene;
    }
    
    public AbstractScene getScene() {
        return currentScene;
    }
    
    public void render(float delta) {
        if(currentScene != null) {
            currentScene.render(delta);
        }
    }
    
    public void resize(int width, int height) {
        if(currentScene != null) {
            currentScene.resize(width, height);
        }
    }
}
