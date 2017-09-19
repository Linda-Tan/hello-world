package com.junliang.helloworld;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@EnableZuulServer
@EnableEurekaClient
@EnableEurekaServer
@SpringBootApplication
public class HelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

	/*@Bean
	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {

			@Override
			public void customize(Builder builder) {
				builder.addHttpListener(8080, "0.0.0.0");
			}

		});
		return factory;
	}*/
	/*@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
				//SerializerFeature.WriteClassName,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty
		);
		fastConverter.setFastJsonConfig(fastJsonConfig);
		//处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		fastConverter.setSupportedMediaTypes(fastMediaTypes);

		HttpMessageConverter<?> converter = fastConverter;
		return new HttpMessageConverters(converter);
	}*/
}
