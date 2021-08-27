package kaantelypeli.ui;

import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class LevelEditorTest extends ApplicationTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();

    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Game.class);
    }

    @Test
    public void openLevel() {
        clickOn("#editor");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        assertThat(systemOutRule.getLog(), containsString("TRACE: Opened a copy of level 1 in editor"));
    }
}
