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


public class Map {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ArrayList<Rectangle> blockedAreas;
    
    private Rectangle DEFAULT_SPAWN = new Rectangle(1, 98, 25, 119);
    private static final String SPAWN_LAYER = "Spawnpoint_Layer";
    private static final String SPAWN_AREA = "spawn";
    private static final String BLOCKED_LAYER = "Blockarea_Layer";


    public Map(String mapFilePath) {
        this.map = new TmxMapLoader().load(mapFilePath);
        this.renderer = new OrthogonalTiledMapRenderer(map);
        this.blockedAreas = initiateBlockedAreas();             
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

    public ArrayList<Rectangle> getBlockedAreas() {
        return blockedAreas;
    }
    
 
    //detect collision
    public boolean isColliding(Rectangle entityBounds) {
        for (Rectangle blockedArea : blockedAreas) {
            if (blockedArea.overlaps(entityBounds)) {
            	//when collison to wall detected
                return true;
            }
        }
        return false;
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
