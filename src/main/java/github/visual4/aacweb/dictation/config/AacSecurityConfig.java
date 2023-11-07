package github.visual4.aacweb.dictation.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class AacSecurityConfig extends WebSecurityConfigurerAdapter{

	@Value("${dictation.mode}") String appMode;
	
	@PostConstruct
    public void logSecurityContext() {
    	System.out.println("[SPRING SECURITY]");
    	System.out.println(" * [MODE] " + appMode);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
    	http.formLogin().disable();
    	
    	http
    		.authorizeHttpRequests()
    		.antMatchers(
    			"/",
    			"/index.html",
    			"/support",
    			"/purchase",
    			"/manifest.json",
    			"/service-worker.js",
    			"/precache-manifest*.js",
    			"/css/*.css",
    			"/js/*.js",
    			"/img/**",
    			"/media/**",
    			"/robots.txt",
    			"/img/icons/*.png",
    			"/api/chapters/origin/L",
    			"/hooks/import",
    			"/api/**").permitAll()
    		.anyRequest().authenticated();
    	
    	http.csrf().disable();
    	
    	http
    		.headers()
    			.xssProtection();
//    			.and()
//    			.contentSecurityPolicy("script-src 'self'");
    }
    
}
