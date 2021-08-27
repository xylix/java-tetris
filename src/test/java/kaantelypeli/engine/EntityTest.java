package kaantelypeli.engine;

import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class EntityTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();
    
    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
    }
    
    @Test
    public void illegalMovement() {
        Entity test = new Entity("player", 0, 0);
        double x = test.hitbox.getX() + test.hitbox.getTranslateX();
        double y = test.hitbox.getY() + test.hitbox.getTranslateY();
        test.move(4);
        assertEquals(x, test.getActualX(), 0);
        assertEquals(y, test.getActualY(), 0);
        assertTrue(systemErrRule.getLog().contains("Illegal movement call"));
    }
    
    @Test (expected = NullPointerException.class)
    public void entityNotFound() {
        new Entity("test", 0, 0);
        assertTrue(systemErrRule.getLog().contains("Entity: test not found."));
    }
    
    @Test
    public void getJson() {
        Entity e = new Entity("player", 0, 0);
        assertEquals("{\"type\":\"player\",\"x\":0,\"y\":0,\"actionMap\":{\"lava\":\"loss\",\"victory\":\"victory\"},\"width\":14,\"height\":14,\"movable\":true,\"passable\":true}",
                e.getJson());
    }
    
    @Test
    public void hashCodeTest() {
        Entity e = new Entity("player", 0, 0);
        Entity e2 = new Entity("player", 0, 0);
        assertEquals(e.hashCode(), e2.hashCode());
        Entity e3 = new Entity("wall", 0, 0);
        assertNotEquals(e.hashCode(), e3.hashCode());
    }

    @Test
    public void equalsSelf() {
        Entity e = new Entity("player", 0, 0);
        assertTrue(e.equals(e));
    }

    @Test
    public void equalsNull() {
        Entity e = new Entity("player", 0, 0);
        assertFalse(e.equals(null));
    }

    @Test
    public void equalsObject() {
        Entity e = new Entity("player", 0, 0);
        assertFalse(e.equals(new Object()));
    }
    
    @Test
    public void getHitbox() {
        Entity e = new Entity("player", 0, 0);
        Rectangle hitbox = e.hitbox;
        assertEquals(0, (int) hitbox.getX());
        assertEquals(0, (int) hitbox.getY());
    }
}
