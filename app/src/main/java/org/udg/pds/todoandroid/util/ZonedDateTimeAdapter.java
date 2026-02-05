package org.udg.pds.todoandroid.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Gson TypeAdapter for ZonedDateTime using ISO 8601 format.
 * Handles serialization and deserialization of ZonedDateTime objects.
 */
public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    // Alternative formatter for parsing ISO 8601 with offset (e.g., 2025-12-27T15:25:45.295417Z)
    private static final DateTimeFormatter ISO_INSTANT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        // Serialize to ISO 8601 format
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String dateString = json.getAsString();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            // Try parsing with ISO_ZONED_DATE_TIME first (includes zone id like [UTC])
            return ZonedDateTime.parse(dateString, FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                // Try parsing with ISO_OFFSET_DATE_TIME (e.g., 2025-12-27T15:25:45.295417Z)
                return ZonedDateTime.parse(dateString, ISO_INSTANT_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    // Try parsing as ISO_INSTANT and convert to ZonedDateTime
                    return ZonedDateTime.parse(dateString);
                } catch (DateTimeParseException e3) {
                    throw new JsonParseException("Unable to parse ZonedDateTime: " + dateString, e3);
                }
            }
        }
    }
}
