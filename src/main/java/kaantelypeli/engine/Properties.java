package kaantelypeli.engine;

import java.util.HashMap;

public class Properties {
    boolean movable = false;
    boolean passable = true;
    String graphics;
    int width;
    int height;

    HashMap<String, Action> actionMap;
}
