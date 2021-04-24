package API.Util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class DateJsonSerializer implements JsonSerializer<Date> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM d, yyyy");
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(simpleDateFormat.format(src));
    }
}
