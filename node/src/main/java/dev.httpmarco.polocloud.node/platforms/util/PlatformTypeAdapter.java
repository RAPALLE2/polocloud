package dev.httpmarco.polocloud.node.platforms.util;

import com.google.gson.*;
import dev.httpmarco.polocloud.node.Node;
import dev.httpmarco.polocloud.node.platforms.Platform;
import dev.httpmarco.polocloud.node.platforms.PlatformService;
import dev.httpmarco.polocloud.node.platforms.PlatformType;
import dev.httpmarco.polocloud.node.platforms.PlatformVersion;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Set;

public class PlatformTypeAdapter implements JsonDeserializer<Platform>, JsonSerializer<Platform> {

    public static final PlatformTypeAdapter INSTANCE = new PlatformTypeAdapter();

    @Override
    public Platform deserialize(JsonElement json, Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        var object = (JsonObject) json;

        var name = object.get("platform").getAsString();

        var type = object.has("type") ? PlatformType.valueOf(object.get("type").getAsString().toUpperCase()) : PlatformType.SERVER;
        var shutdownCommand = object.has("shutdownCommand") ? object.get("shutdownCommand").getAsString() : Platform.DEFAULT_SHUTDOWN_COMMAND;

        PlatformVersion[] versions = context.deserialize(object.get("versions"), PlatformVersion[].class);
        var platform = new Platform(name, type, Set.of(versions), shutdownCommand);

        if (object.has("patcher")) {
            platform.platformPatcher(PlatformService.PATCHERS.stream().filter(it -> it.patchId().equalsIgnoreCase(object.get("patcher").getAsString())).findFirst().orElse(null));
        }

        if (object.has("startArguments")) {
            platform.startArguments(context.deserialize(object.getAsJsonArray("startArguments"), String[].class));
        }


        return platform;
    }

    @Override
    public JsonElement serialize(@NotNull Platform src, Type typeOfSrc, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("platform", src.platform());
        object.addProperty("type", src.type().name());

        if (!src.shutdownCommand().equalsIgnoreCase(Platform.DEFAULT_SHUTDOWN_COMMAND)) {
            object.addProperty("shutdownCommand", src.shutdownCommand());
        }

        if (src.platformPatcher() != null) {
            object.addProperty("patcher", src.platformPatcher().patchId());
        }

        if (src.startArguments() != null) {
            object.add("startArguments", context.serialize(src.startArguments()));
        }

        object.add("versions", context.serialize(src.versions()));
        return object;
    }
}
