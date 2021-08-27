package kaantelypeli.engine;

import kaantelypeli.ui.Game;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static kaantelypeli.utils.FileOperations.loadLevel;
import static org.junit.Assert.assertEquals;

public class LevelTest {
    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Game.class);
    }
    
    @Test
    public void loadLevelTest() {
        Level l = loadLevel("test0");
        assertEquals(l.getClass(), Level.class);
    }
    
    @Test
    public void negativeOneToJsonTest() {
        Level l = loadLevel("test1");
        assertEquals(new Level(Arrays.asList(new Entity("player", 0, 16),
                new Entity("victory", 0, 24))).getJson(), l.getJson());
    }
    
    @Test
    public void changeGravityTest() {
        Level l = loadLevel("test1");
        int old = l.gravity;
        l.changeGravity(4);
        assertEquals(old + 4, l.gravity);
    }


}
