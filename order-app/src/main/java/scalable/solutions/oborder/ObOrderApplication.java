package scalable.solutions.oborder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
@SpringBootApplication
public class ObOrderApplication extends WebMvcConfigurerAdapter {

    private static final List<Locale> LOCALES = Arrays.asList(
            // @formatter:off
            new Locale("en"),
            new Locale("ro"),
            new Locale("es"),
            new Locale("fr"),
            new Locale("ja"));
    // @formatter:on

    public static void main(String[] args) {
        SpringApplication.run(ObOrderApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SmartLocaleResolver();
    }

    /**
     * I18n - Set locale from Accept-Language header.
     *
     * @author Marius
     */
    private static class SmartLocaleResolver extends AcceptHeaderLocaleResolver {

        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            String language = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            Locale locale = Locale.getDefault();
            if (language != null && !language.trim().isEmpty()) {
                List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
                locale = Locale.lookup(list, LOCALES);
            }
            return locale;
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
