package kaantelypeli.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import kaantelypeli.engine.Level;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.BiFunction;

import static kaantelypeli.utils.FXUtils.createButton;
import static kaantelypeli.utils.FileOperations.loadLevel;

/**
 * Main Graphical User Interface class.
 */
public class Game extends Application {
    public static final int SCALE = 2;
    private Stage mainStage;
    private Scene mainMenu;
    public static final String FILESERVER =  "http://xylix.fi/levels";



    /**
     * Launch the application.
     * @param args pass command line arguments to follow
     * main() method convention
    */
    public static void main(String[] args) {
        Application.launch();
    }
    
    @Override
    public void start(Stage stage) {
        mainStage = stage;
        // Button cloud = createButton("cloud-levels", event -> cloudMenu(mainStage));
        LevelEditor editor = new LevelEditor(stage, mainMenu);
        Button editorButton = createButton("editor", event -> editor.editorMenu());
        // VBox buttons = new VBox(cloud, editorButton);
        VBox buttons = new VBox(editorButton);

        InputStreamReader levelFolder = new InputStreamReader(Game.class.getResourceAsStream("/levels"));
        BufferedReader br = new BufferedReader(levelFolder);
        br.lines().forEach(line -> buttons.getChildren().add(levelButton(line, stage)));

        mainMenu = new Scene(buttons);
        stage.setScene(mainMenu);
        stage.show();
    }


    /* private void cloudMenu(Stage target) {
        ChoiceDialog<String> choice = selector(FILESERVER, target);
        Optional<String> result = choice.showAndWait();
        result.ifPresent(input ->  target.setScene(levelToScene(loadLevel(FILESERVER + '/' + input));
    }
     */

    private Button levelButton(String file, Stage stage) {
        final String levelName = file.replace(".json", "");
        return createButton(levelName, event -> {
            Level activeLevel = loadLevel(levelName);
            stage.setScene(levelToScene(activeLevel));
        });
    }

    public Scene getNextScene(Level l) {
        final int nextLevel = l.levelIndex + 1;
        return switch(nextLevel) {
            case 1, 2, 3, 4, 5 -> levelToScene(loadLevel(String.valueOf(nextLevel)));
            case 6, 667 -> this.mainMenu;
            default -> throw new IllegalStateException("Next level cannot be negative or more than amount of levels. Was %s".formatted(nextLevel));
        };
    }

    /**
     * Abstraction for hotkey binding to enable refactoring it to other callsite later.
     * Result of bindHotkey needs to be passed as an argument to scene.setOnKeyPressed
     * To bind hotkeys to the related level correctly
     */
    private EventHandler<? super KeyEvent> bindHotkey(Level level, Pane pane) {
        return (event) -> {
            final KeyCode pressedKey = event.getCode();
            switch (pressedKey) {
                case LEFT, A -> level.changeGravity(270);
                case RIGHT, D -> level.changeGravity(90);
                case R -> level.restart();
                case ESCAPE -> mainStage.setScene(mainMenu);
            }
            pane.setRotate(level.gravity);
        };
    }

    public Scene levelToScene(Level level) {
        Pane pane = new Pane(level.getHitboxes().toArray(Rectangle[]::new));
        Scene scene = new Scene(pane);

        scene.setOnKeyPressed(bindHotkey(level, pane));
        new AnimationTimer() {
            @Override public void handle(long timestamp) {
                var gameState = level.tick();
                switch(gameState) {
                    case WON -> {
                        this.stop();
                        mainStage.setScene(getNextScene(level));
                    }
                }
            }
        }.start();
        return scene;
    }

}
