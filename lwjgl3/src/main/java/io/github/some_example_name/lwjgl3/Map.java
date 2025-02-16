package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Map {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ArrayList<Rectangle> pathAreas;
    private ArrayList<Rectangle> blockedAreas;
    private ArrayList<Rectangle> greenAreas = new ArrayList<>();
    
    private Rectangle DEFAULT_SPAWN = new Rectangle(1, 98, 25, 119);
    private static final String SPAWN_LAYER = "Spawnpoint_Layer";
    private static final String SPAWN_AREA = "spawn";
    private static final String BLOCKED_LAYER = "Blockarea_Layer";
    
    private static final String PATH_LAYER = "Path_Layer"; // New


    public Map(String mapFilePath) {
        this.map = new TmxMapLoader().load(mapFilePath);
        this.renderer = new OrthogonalTiledMapRenderer(map);
        this.blockedAreas = initiateBlockedAreas();        
        this.pathAreas = loadPathAreas();
        this.greenAreas = new ArrayList<>();
    }
    
    
    //loads and store the coordinates of blocked areas from tmx map
    private ArrayList<Rectangle> initiateBlockedAreas(){
    	ArrayList<Rectangle> areas = new ArrayList<>(); //this is to create a list of coordinates of the block area
        MapLayer blockLayer = map.getLayers().get(BLOCKED_LAYER);
        
        if (blockLayer != null) {
            for (MapObject object : blockLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    areas.add(((RectangleMapObject) object).getRectangle());
                } 
             }
            } else {
            	Gdx.app.error("Map", "No blockarea layer found in map");
            }
        	return areas;
       }
    
    // NEW
    private ArrayList<Rectangle> loadPathAreas() {
        ArrayList<Rectangle> areas = new ArrayList<>();
        MapLayer pathLayer = map.getLayers().get("Path_Layer"); // ✅ Make sure this matches the TMX file

        if (pathLayer != null) {
            for (MapObject object : pathLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    areas.add(((RectangleMapObject) object).getRectangle());
                }
            }
            System.out.println("Loaded " + areas.size() + " path areas.");
        } else {
            System.out.println("⚠️ No Path_Layer found in TMX map!");
        }
        return areas;
    }

	
    public boolean isPath(float x, float y) {
        for (Rectangle path : blockedAreas) {
            if (path.contains(x, y)) {
                return true; // This position is on a path
            }
        }
        return false; // This position is NOT on a path
    }
    
    public ArrayList<Rectangle> getBlockedAreas() {
        return blockedAreas;
    }

 
    //detect collision
    public boolean isColliding(Rectangle entityBounds) {
        for (Rectangle path : pathAreas) {
            if (path.contains(entityBounds.x, entityBounds.y)) {
                return false; // Minions can move here
            }
        }
        return true; // Minions cannot move outside the path
    }

    public boolean isGreenArea(float x, float y) {
        for (Rectangle area : greenAreas) {
            if (area.contains(x, y)) {
                return true; // Clicked position is on a green area
            }
        }
        return false; // Clicked position is not on green
    }
	
    
    public Rectangle getSpawnPoint() {
        MapLayer spawnLayer = map.getLayers().get(SPAWN_LAYER);
        if (spawnLayer != null){
	        for (MapObject object : spawnLayer.getObjects()) {
	            if (SPAWN_AREA.equals(object.getName())) {
	                return ((RectangleMapObject) object).getRectangle();
	            }
	        }
        }
        //default position if no spawn point found
        Gdx.app.log("Map", "Warning: No spawn point found, using default");
        return DEFAULT_SPAWN;
    }


    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
