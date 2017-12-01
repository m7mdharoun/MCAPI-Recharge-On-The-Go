package com.mc.driveweek.mcfitbit;

import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class McFitbitApplication {
  private static String KEY_PATH;
  private static String KEY_ALIAS;
  private static String PASSWORD;
  private static String CONSUMER_KEY;

  @Bean
  public GoogleMapsService googleMapsService(  @Value("${api.google.maps.key}") String key) {
    return new GoogleMapsService(key);
  }

  @Bean
  public GoogleStaticMapsService googleStaticMapsService( @Value("${api.google.staticmaps.key}") String key) {
    return new GoogleStaticMapsService(key);
  }

  @Bean
  public FilterRegistrationBean simpleAuthFilter(@Value("${proxy.key}") String key) {
    SimpleAuthFilter greetingFilter = new SimpleAuthFilter(key);

    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setName("simple-auth");
    registrationBean.setFilter(greetingFilter);
    registrationBean.setOrder(1);
    return registrationBean;
  }

  public static void main(String[] args) throws Exception {

    SpringApplication.run(McFitbitApplication.class, args);

    // Initialize Authentication context and application environment
    InputStream stream = new ClassPathResource(KEY_PATH).getInputStream();
    ApiConfig.setAuthentication(new OAuthAuthentication(CONSUMER_KEY, stream, KEY_ALIAS, PASSWORD));
    // For production: use ApiConfig
    ApiConfig.setSandbox(false);
  }

  @Value("${api.key.path}")
  public void setKeyPath(String val) {
    KEY_PATH = val;
  }

  @Value("${api.key.alias}")
  public void setKeyAlias(String val) {
    KEY_ALIAS = val;
  }

  @Value("${api.key.password}")
  public void setPassword(String val) {
    PASSWORD = val;
  }

  @Value("${api.consumer.key}")
  public void setConsumerKey(String val) {
    CONSUMER_KEY = val;
  }
}
