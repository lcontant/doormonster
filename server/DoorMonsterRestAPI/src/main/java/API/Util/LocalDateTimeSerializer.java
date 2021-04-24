package API.Util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM d, yyyy");
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        Instant instant = src.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        return new JsonPrimitive(simpleDateFormat.format(date));
    }
}
