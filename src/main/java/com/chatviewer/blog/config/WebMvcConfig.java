package com.chatviewer.blog.config;

import com.chatviewer.blog.filter.StatisticInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author ChatViewer
 */
@Slf4j
@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 重写Jackson转换器
     * Long类型转String类型
     * 解决前端Long类型精度丢失问题（js解析只能解析到16位）
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1、定义一个新的Converter
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 2、定义转换Mapper
        ObjectMapper objectMapper = new JacksonObjectMapper();
        messageConverter.setObjectMapper(objectMapper);
        // 5、加入converter
        converters.add(messageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Resource
    StatisticInterceptor statisticInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(statisticInterceptor);
    }
}