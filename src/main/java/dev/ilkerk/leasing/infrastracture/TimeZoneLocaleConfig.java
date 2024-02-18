package dev.ilkerk.leasing.infrastracture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.TimeZone;

@Component
@Slf4j
public class TimeZoneLocaleConfig implements BeanFactoryPostProcessor, EnvironmentAware, Ordered {
    private Environment env;

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory ignored) throws BeansException {
        TimeZone.setDefault(
                TimeZone.getTimeZone(env.getProperty("server.time-zone", "UTC"))
        );
        Locale.setDefault(
                Locale.forLanguageTag(env.getProperty("server.locale", "en-US"))
        );
        log.info("TimeZone: {}", TimeZone.getDefault().toString());
        log.info("Locale: {}", Locale.getDefault().toString());
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.env = environment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}