package integration;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import kaantelypeli.ui.Game;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

import static kaantelypeli.ui.Game.FILESERVER;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LevelEditorIT extends ApplicationTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();
    @Rule public TemporaryFolder folder= new TemporaryFolder();


    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Game.class);
    }

    @Test
    public void addTile() {
        openFirstLevel();
        systemOutRule.clearLog();
        spawnTile();
        push(KeyCode.ENTER);
        assertThat(systemOutRule.getLog(), containsString("TRACE: {\"type\":\"wall\""));
        assertThat(systemOutRule.getLog(), containsString("\"actionMap\":{},\"width\":16,\"height\":16,"));
    }


    @Test
    public void cancelAddingTile() {
        openFirstLevel();
        systemOutRule.clearLog();
        clickOn(MouseButton.PRIMARY);
        push(KeyCode.ESCAPE);
        assertEquals("", systemOutRule.getLog());
    }

    @Test
    public void addWideTile() {
        openFirstLevel();
        systemOutRule.clearLog();
        spawnTile();
        // Set width and height
        push(KeyCode.TAB);
        push(KeyCode.DIGIT3);
        push(KeyCode.DIGIT2);
        push(KeyCode.TAB);
        push(KeyCode.DIGIT3);
        push(KeyCode.DIGIT2);

        push(KeyCode.ENTER);
        assertThat(systemOutRule.getLog(), containsString(
                "TRACE: {\"type\":\"wall\""
        ));
        assertThat(systemOutRule.getLog(), containsString(",\"width\":32,\"height\":32"));
    }

    public void spawnTile() {
        clickOn(MouseButton.PRIMARY);
        // Navigate to type selector.
        push(KeyCode.TAB);
        push(KeyCode.SPACE);
        push(KeyCode.SPACE);
    }

    @Test
    public void levelUpload() {
        openFirstLevel();
        push(KeyCode.TAB);
        push(KeyCode.TAB);
        push(KeyCode.SPACE);
        sleep(200);
        assertThat(systemOutRule.getLog(), containsString("TRACE: POSTing to " + FILESERVER + "/levels/"));
        assertThat(systemOutRule.getLog(), containsString("TRACE: kong.unirest.EmptyResponse"));
    }

    @Test
    public void saveLevel() {
        openFirstLevel();
        push(KeyCode.TAB);
        push(KeyCode.SPACE);
        sleep(200);
        push(KeyCode.E);
        push(KeyCode.ENTER);

        assertThat(systemOutRule.getLog(), containsString("TRACE: Saving file to"));
    }

    @Test
    public void cancelledSave () {
        openFirstLevel();
        push(KeyCode.TAB);
        push(KeyCode.SPACE);
        sleep(200);
        push(KeyCode.ESCAPE);
        assertThat(systemOutRule.getLog(), containsString("Save dialog cancelled"));
    }

    public void openFirstLevel() {
        clickOn("#editor");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
    }
}

