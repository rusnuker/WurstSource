// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util.json;

import com.google.gson.JsonObject;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.google.gson.JsonElement;
import java.util.function.Predicate;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.ArrayList;
import java.util.Objects;
import com.google.gson.JsonArray;

public final class WsonArray
{
    private final JsonArray json;
    
    public WsonArray(final JsonArray json) {
        this.json = Objects.requireNonNull(json);
    }
    
    public ArrayList<String> getAllStrings() {
        return StreamSupport.stream((Spliterator<Object>)this.json.spliterator(), false).filter((Predicate<? super Object>)JsonUtils::isString).map((Function<? super Object, ?>)JsonElement::getAsString).collect((Collector<? super Object, ?, ArrayList<String>>)Collectors.toCollection(() -> new ArrayList()));
    }
    
    public ArrayList<WsonObject> getAllObjects() {
        return StreamSupport.stream((Spliterator<Object>)this.json.spliterator(), false).filter(JsonElement::isJsonObject).map((Function<? super Object, ?>)JsonElement::getAsJsonObject).map(json -> new WsonObject(json)).collect((Collector<? super Object, ?, ArrayList<WsonObject>>)Collectors.toCollection(() -> new ArrayList()));
    }
    
    public JsonArray toJsonArray() {
        return this.json;
    }
}
