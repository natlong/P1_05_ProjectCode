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


public class Map {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ArrayList<Rectangle> walkingPath;
    private ArrayList<Rectangle> blockedAreas;
    private ArrayList<Rectangle> greenAreas = new ArrayList<>();
    
    private Rectangle DEFAULT_SPAWN = new Rectangle(1, 98, 25, 119);
    
    public static final String SPAWN_LAYER = "Spawnpoint_Layer";
    public static final String SPAWN_AREA = "spawn";
    public static final String BLOCKED_LAYER = "Blockarea_Layer";
    public static final String PATH_LAYER = "Path_Layer";
    public static final String GAMEOVER_LAYER = "Gameover_Layer";
    public static final String GAMEOVER_AREA = "gameover" ;


    public Map(String mapFilePath) {
        this.map = new TmxMapLoader().load(mapFilePath);
        this.renderer = new OrthogonalTiledMapRenderer(map);
        this.blockedAreas = initiateBlockedAreas();        
        this.walkingPath = initiateWalkingPath();
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
    
    public ArrayList<Rectangle> getBlockedAreas() {
        return new ArrayList<>(blockedAreas);
    }
    
    private ArrayList<Rectangle> initiateWalkingPath(){
    	ArrayList<Rectangle> areas = new ArrayList<>(); //this is to create a list of coordinates of the area
        MapLayer WalkPath = map.getLayers().get(PATH_LAYER);
        
        if (WalkPath != null) {
            for (MapObject object : WalkPath.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    areas.add(((RectangleMapObject) object).getRectangle());
                } 
             }
            } else {
            	Gdx.app.error("Map", "No Walking path layer found in map");
            }
        	return areas;
       }

    public ArrayList<Rectangle> getWalkingPath() {
        return new ArrayList<>(walkingPath);
    }

	
    public boolean isPath(float x, float y) {
        for (Rectangle path : blockedAreas) {
            if (path.contains(x, y)) {
                return true; // This position is on a path
            }
        }
        return false; // This position is NOT on a path
    }
    
    //detect collision
    public boolean isColliding(Rectangle minionBounds) {
        for (Rectangle blockedArea : blockedAreas) {
            if (blockedArea.overlaps(minionBounds)) {
            	//when collison to wall detected
            	// help to add the behavior where it moves downstairs. when u reset logn enough soemtimes it will hit until the red parts.. 
            	// either u can adjust the behavior here or u play cheat and edit the tilemap so it doesnt hit haha. 
                return true;
            }
        }
        return false;
    }

    public boolean isGreenArea(float x, float y) {
        for (Rectangle area : greenAreas) {
            if (area.contains(x, y)) {
                return true; // Clicked position is on a green area
            }
        }
        return false; // Clicked position is not on green
    }
	
  //get the spawn point
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
    
    //get the gameover point
    public Rectangle getGameoverPoint() {
        MapLayer gameoverLayer = map.getLayers().get(GAMEOVER_LAYER);
        if (gameoverLayer != null){
	        for (MapObject object : gameoverLayer.getObjects()) {
	            if (GAMEOVER_AREA.equals(object.getName())) {
	                return ((RectangleMapObject) object).getRectangle();
	            }
	        }	        
        }
		return null;
    }


    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
    
    public ArrayList<Vector2> getPathWaypoints() {
        ArrayList<Vector2> waypoints = new ArrayList<>();
        
        // Iterate over each rectangle in the pathAreas list.
        for (int i = 0; i < walkingPath.size(); i++) {
            Rectangle rect = walkingPath.get(i);
            Vector2 entry, exit;
            
            // For vertical rectangles:
            if (rect.height > rect.width * 1.5f) {
                // For vertical, we assume the primary movement is vertical.
                // If there is a next rectangle, we compare center Y values to determine direction.
                if (i < walkingPath.size() - 1) {
                    Rectangle nextRect = walkingPath.get(i + 1);
                    float currentCenterY = rect.y + rect.height / 2f;
                    float nextCenterY = nextRect.y + nextRect.height / 2f;
                    if (currentCenterY > nextCenterY) {
                        // Moving downward: 
                        // Use full top edge for entry.
                        entry = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height);
                        // For exit, instead of using the very bottom (rect.y), use a point a fraction (e.g., 25%) above it.
                        exit  = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height * 0.25f);
                    } else {
                        // Moving upward:
                        entry = new Vector2(rect.x + rect.width / 2f, rect.y);
                        // For exit, use a point a fraction (e.g., 75%) from the bottom (i.e. 25% from the top).
                        exit  = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height * 0.75f);
                    }
                } else {
                    // For the last rectangle, use full edges.
                    entry = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height);
                    exit  = new Vector2(rect.x + rect.width / 2f, rect.y);
                }
            }
            // For horizontal rectangles:
            else if (rect.width > rect.height * 1.5f) {
                if (i < walkingPath.size() - 1) {
                    Rectangle nextRect = walkingPath.get(i + 1);
                    float currentCenterX = rect.x + rect.width / 2f;
                    float nextCenterX = nextRect.x + nextRect.width / 2f;
                    if (currentCenterX < nextCenterX) {
                        // Moving right:
                        entry = new Vector2(rect.x, rect.y + rect.height / 2f);
                        // Instead of the right edge, use a point 75% along the width.
                        exit  = new Vector2(rect.x + rect.width * 0.75f, rect.y + rect.height / 2f);
                    } else {
                        // Moving left:
                        entry = new Vector2(rect.x + rect.width, rect.y + rect.height / 2f);
                        // Instead of the left edge, use a point 25% along the width.
                        exit  = new Vector2(rect.x + rect.width * 0.25f, rect.y + rect.height / 2f);
                    }
                } else {
                    // For the last rectangle, use full edges.
                    entry = new Vector2(rect.x, rect.y + rect.height / 2f);
                    exit  = new Vector2(rect.x + rect.width, rect.y + rect.height / 2f);
                }
            }
            // For nearly square rectangles, use the center for both.
            else {
                entry = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
                exit  = new Vector2(rect.x + rect.width / 2f, rect.y + rect.height / 2f);
            }
            // Append both entry and exit waypoints.
            waypoints.add(entry);
            waypoints.add(exit);
        }
        // Remove the very last waypoint so that the minion does not target an extra point.
        if (!waypoints.isEmpty()) {
            waypoints.remove(waypoints.size() - 1);
        }
        // Create a defensive copy with new Vector2 instances
        ArrayList<Vector2> result = new ArrayList<>(waypoints.size());
        for (Vector2 waypoint : waypoints) {
            result.add(new Vector2(waypoint));
        }
        
        return result;
    }
}
