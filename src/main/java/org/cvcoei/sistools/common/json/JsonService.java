package org.cvcoei.sistools.common.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Map;

@Service
public class JsonService {

    private final Gson gson = new Gson();

    /**
     * Type adapter with working with simple string/object maps
     */
    private final Type typeMapStringObject =
        new TypeToken<Map<String, Object>>() {}.getType();

    /**
     * Deserialize a JSON string into a generic Map.
     * @param json the string to deserialize
     * @return Map of keys and values
     */
    public Map<String, Object> toMap(String json) {
        return gson.fromJson(json, typeMapStringObject);
    }

    /**
     * Serialize a Map into a JSON string.
     * @param map the map to serialize
     * @return JSON formatted string
     */
    public String toJson(Map<Object, Object> map) {
        return gson.toJson(map);
    }

}
