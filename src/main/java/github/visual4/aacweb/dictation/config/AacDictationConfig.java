package github.visual4.aacweb.dictation.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class AacDictationConfig implements WebMvcConfigurer{

	@Value("${dictation.cors.allowed-urls}")
    String [] corsAllowedUrls;
	
	@Override
    public void addCorsMappings(CorsRegistry reg) {
        log.info("[CORS CONFIG] {}", Arrays.toString(corsAllowedUrls));
        reg
            .addMapping("/**")
            .allowedOrigins(corsAllowedUrls)
            .allowedMethods(
                    HttpMethod.GET.name(),
                    HttpMethod.HEAD.name(),
                    HttpMethod.POST.name(),
                    HttpMethod.PUT.name(),
                    HttpMethod.DELETE.name()
                    );
    }
}
