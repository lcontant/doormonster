package API.Startup;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    RequestIntercepter requestIntercepter;

    public WebMvcConfig(RequestIntercepter requestIntercepter) {
        this.requestIntercepter = requestIntercepter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.requestIntercepter);
    }
}
