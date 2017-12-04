package com.junliang.spring;

import io.undertow.Undertow.Builder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;

@EnableCaching
@EnableZuulServer
@EnableEurekaClient
@EnableEurekaServer
@SpringBootApplication
public class HelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}


	//@Bean
	//public ServerEndpointExporter serverEndpointExporter(){
	//	return new ServerEndpointExporter();
	//}




	//@Bean
	//public AccessFilter accessFilter(){
	//	return new AccessFilter();
	//}

	@Bean
	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory undertow = new UndertowEmbeddedServletContainerFactory();
		undertow.addBuilderCustomizers((Builder builder) -> {
			builder.addHttpListener(8762, "0.0.0.0");
		});
		//log.info("\n*** Undertow http setting successful." + properties.getPort());
		return undertow;
	}

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
