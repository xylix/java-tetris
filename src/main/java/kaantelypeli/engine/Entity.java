package kaantelypeli.engine;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import kaantelypeli.utils.FileOperations;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Objects;

import static kaantelypeli.ui.Game.SCALE;
import static kaantelypeli.utils.FileOperations.loadSprite;

/**
 * Exposes entity generation, movement and collision.
 */
@JsonAdapter(EntitySerializer.class)
public class Entity {
    final String type;
    final int x;
    final int y;
    
    private HashMap<String, Action> actionMap;
    private Integer width;
    private Integer height;
    transient Rectangle hitbox;
    boolean movable;
    boolean passable;
    
    /**
     Creates a new entity of `type` at location `x`,`y` with default width and height.
     * @param type Type of new entity. See resources/entities for info.
     * @param x X-coordinate of new entity.
     * @param y Y-coordinate of new entity.
     */
    public Entity(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        setProperties();
    }
    
    /**
     * Creates a new entity of `type` at location `x`,`y` with 
     * non-default width and height.
     * @param type Type of new entity. See resources/entities for info.
     * @param x X-coordinate of new entity.
     * @param y Y-coordinate of new entity.
     * @param width width of new entity.
     * @param height height of new entity.
     */
    public Entity(String type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setProperties();
    }
    
    /**
     * Sets entity properties based on entity types definition file.
     * Definition files located in entities/
     * Splitting setProperties from the constructor is neceessary so GSON can
     * call setProperties manually after instantiating the class with raw data
     */
    final void setProperties() {
        Properties source = FileOperations.loadProperties(type);
        if (width == null || height == null) {
            width = source.width;
            height = source.height;
        }
        movable = source.movable;
        passable = source.passable;
        actionMap = Objects.requireNonNullElseGet(source.actionMap, HashMap::new);
        this.hitbox = new Rectangle(x * SCALE, y * SCALE, width * SCALE, height * SCALE);
        hitbox.setId(type);
        this.hitbox.setFill(loadSprite(Objects.requireNonNullElseGet(source.graphics, () -> type + ".png")));
    }

    /**
     * Collision check.
     * @param collidee Entity to check collision with.
     * @return returns True if `this` entity is currently colliding with `collidee`
     */
    public boolean collide(Entity collidee) {
        Shape collisionBox = Shape.intersect(hitbox, collidee.hitbox);
        return collisionBox.getBoundsInLocal().getWidth() != -1;
    }
    
    /**
     * Deducts what happens in a collision.
     * @param collidee Entity to collide with.
     * @return String defining what to do when `this` and `collidee` collide.
     */
    public Action collisionAction(Entity collidee) {
        return this.actionMap.getOrDefault(collidee.type, Action.blank);
    }

    void move(int i) {
        i = Math.abs(i % 360);
        switch (i) {
            case 0 -> hitbox.setTranslateY(hitbox.getTranslateY() + SCALE);
            case 90 -> hitbox.setTranslateX(hitbox.getTranslateX() + SCALE);
            case 180 -> hitbox.setTranslateY(hitbox.getTranslateY() - SCALE);
            case 270 -> hitbox.setTranslateX(hitbox.getTranslateX() - SCALE);
            default -> Logger.error("Illegal movement call");
        }
    }

    public double getActualX() {
        return hitbox.getX() + hitbox.getTranslateX();
    }
    
    public double getActualY() {
        return hitbox.getY() + hitbox.getTranslateY();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + (this.movable ? 1 : 0);
        hash = 79 * hash + (this.passable ? 1 : 0);
        hash = 79 * hash + this.actionMap.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Entity other = (Entity) obj;
        return this.getJson().equals(other.getJson());
    }

    /**
     * Converts this entity into a JSON representation.
     * @return JSON representation of the entity.
     */
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Rectangle getHitbox() {
        return this.hitbox;
    }
}
