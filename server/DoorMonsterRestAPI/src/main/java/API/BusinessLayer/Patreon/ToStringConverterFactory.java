package API.BusinessLayer.Patreon;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class ToStringConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //noinspection EqualsBetweenInconvertibleTypes
        if (String.class.equals(type)) {
            return (Converter<ResponseBody, Object>) responseBody -> responseBody.string();
        }
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return (Converter<String, RequestBody>) value -> RequestBody.create(MediaType.parse("text/plain"), value);
        }
        return null;
    }
}
