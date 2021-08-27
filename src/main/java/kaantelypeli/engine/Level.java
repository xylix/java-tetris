package kaantelypeli.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles level generation and level management. 
 * Exposes gravitate() method for binding gravitation to game ticks.
 */
public class Level {
    public int gravity;
    public int levelIndex;
    public State gameState;
    private List<Entity> entities;

    private Level() {
        gravity = 0;
        levelIndex = -1;
        gameState = State.ONGOING;
        entities = new ArrayList<>();
    }
    /**
     * Creates level from entity list.
     * LevelEditor uses this.
     * @param entities list of entities to place on level
     */
    public Level(List<Entity> entities) {
        this();
        this.entities = entities;
    }

    /**
     * Changes the gravity by `degrees`.
     *
     * @param   degrees   Wanted change in degrees
     */
    public void changeGravity(int degrees) {
        gravity += degrees;
    }

    public enum State {
        WON,
        LOST,
        ONGOING;
    }
    /**
     * Method to externally bind this level to game ticks. Should get called every tick when level is active.
     */
    public State tick() {
        if (this.gameState == State.LOST) {
            this.restart();
            return State.LOST;
        }
        gravitate();
        return this.gameState;
    }

    public void restart() {
        entities.forEach(e -> {
            e.hitbox.setTranslateX(0);
            e.hitbox.setTranslateY(0);
        });
        this.gameState = State.ONGOING;
    }

    /** Applies gravity to all entities on the level. Movables get moved here. */
    private void gravitate() {
        entities.stream().filter(e -> (e.movable)).forEach(collider -> {
            collider.move(gravity);
            entities.stream().filter(collider::collide).forEach(collidee -> {
                Action action = collider.collisionAction(collidee);
                handleAction(collider, collidee, action);
            });
        });
    }

    /** Resolves Entity interaction. */
    private void handleAction(Entity collider, Entity collidee, Action action) {
        if (!collidee.passable) {
            collider.move(gravity + 540);
        }
        if (action == Action.blank) {
            return;
        } else if (action == Action.victory) {
            System.out.println("You're winner!");
            this.gameState = State.WON;
        } else if (action == Action.loss) {
            this.gameState = State.LOST;
        } else if (action == Action.open) {
            collidee.passable = true;
            collidee.hitbox.setFill(Color.TRANSPARENT);
            collider.movable = false;
            collider.hitbox.setFill(Color.TRANSPARENT);
        } 
        Logger.trace(action.toString());
    }

    /**
     * Converts the level to JSON.
     * @return JSON representation of the level
     */
    public String getJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public List<Rectangle> getHitboxes() {
        return entities.stream().map(e -> e.hitbox).collect(Collectors.toList());
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }
}
