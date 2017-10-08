package com.junliang.spring;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.undertow.Undertow.Builder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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

	@Bean
	public RestTemplate restTemplate(){
		//TODO 2017/9/25 这里可以采用不同的实现方式。 比如：ClientHttpRequestFactory ，或自己设置messageConverters
		//http://www.jianshu.com/p/c9644755dd5e
		RestTemplate  restTemplate=	 new RestTemplate();
		restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
		return restTemplate;
	}
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
