/*
 * Copyright 2020 California Community Colleges Chancellor's Office
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cvcoei.sistools.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Map;

@Service
public class JsonService {

    private final Gson gson = new GsonBuilder().create();

    private final Gson gsonPretty = new GsonBuilder()
        .setPrettyPrinting()
        .create();

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
     * Serialize a Map into a pretty formatted JSON string.
     * @param map the map to serialize
     * @return JSON pretty formatted string
     */
    public String toJsonPretty(Map<String, Object> map) {
        return gsonPretty.toJson(map);
    }

}
