package org.jgrades.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver;
import cz.jirutka.spring.exhandler.interpolators.SpelMessageInterpolator;
import cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils;
import org.jgrades.lic.api.exception.LicenceException;
import org.jgrades.lic.api.exception.LicenceNotFoundException;
import org.jgrades.lic.api.exception.UnreliableLicenceException;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.jgrades.rest")
@Import(RestDocsConfig.class)
@PropertySources({
        @PropertySource("classpath:jg-rest.properties"),
        @PropertySource(value = "file:${jgrades.application.properties.file}", ignoreResourceNotFound = true)
})
public class RestConfig extends WebMvcConfigurationSupport {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(jsonConverter());
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JodaModule());
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.valueOf("application/json")));
        jacksonConverter.setObjectMapper(jacksonObjectMapper());
        return jacksonConverter;
    }

    @Bean
    public DefaultServletHttpRequestHandler createDefaultServletHttpRequestHandler() {
        return new DefaultServletHttpRequestHandler();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSize(20000);
        return resolver;
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(exceptionHandlerExceptionResolver());
        resolvers.add(restExceptionResolver());
    }

    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(HttpMessageConverterUtils.getDefaultHttpMessageConverters());
        return resolver;
    }

    @Bean
    public RestHandlerExceptionResolver restExceptionResolver() {
        return RestHandlerExceptionResolver.builder()
                .withDefaultMessageSource(true)
                .withDefaultHandlers(true)
                .messageInterpolator(new SpelMessageInterpolator())
                .addHandler(new CustomErrorMessageRestExceptionHandler<LicenceException>(HttpStatus.INTERNAL_SERVER_ERROR))
                .addHandler(new CustomErrorMessageRestExceptionHandler<LicenceNotFoundException>(HttpStatus.NOT_FOUND))
                .addHandler(new CustomErrorMessageRestExceptionHandler<UnreliableLicenceException>(HttpStatus.INTERNAL_SERVER_ERROR))
                .defaultContentType(MediaType.APPLICATION_JSON)
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
