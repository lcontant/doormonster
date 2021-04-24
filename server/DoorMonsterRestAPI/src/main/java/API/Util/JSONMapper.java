package API.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.sql.Date;

public class JSONMapper {

    private static JSONMapper instance;
    private Gson mapper;
    public static JSONMapper getInstance() {
        if (instance == null) {
            instance = new JSONMapper();
        }
        return instance;
    }

    private JSONMapper() {
        this.mapper = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PRIVATE)
                .registerTypeAdapter(Date.class, new DateJsonSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer()).create();
    }

    public String JSONStringify(Object object) {
        return mapper.toJson(object);
    }

   public Gson getMapper() {
        return mapper;
   }



}
