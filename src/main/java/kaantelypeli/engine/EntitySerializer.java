package kaantelypeli.engine;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EntitySerializer implements JsonDeserializer<Entity> {
    @Override
    public Entity deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
        EntityData data = jdc.deserialize(je, EntityData.class);
        Entity entity;
        if (data.width == null || data.height == null) {
            entity = new Entity(data.type, data.x, data.y);
        } else {
            entity = new Entity(data.type, data.x, data.y, data.width, data.height);
        }
        
        entity.setProperties();
        return entity;
    }

    private class EntityData {
        String type;
        int x;
        int y;
        Integer width;
        Integer height;
    }
}
