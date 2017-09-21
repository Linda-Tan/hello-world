package com.junliang.spring.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({JSON.class})
public class FastJsonHttpMessageConvertersConfiguration {

    @Configuration
    @ConditionalOnClass({FastJsonHttpMessageConverter.class}) //1
    @ConditionalOnProperty(//2
            name = {"spring.http.converters.preferred-json-mapper"},
            havingValue = "fastjson",
            matchIfMissing = true
    )
    protected static class FastJson2HttpMessageConverterConfiguration{
        protected FastJson2HttpMessageConverterConfiguration() {
        }

        @Bean
        @ConditionalOnMissingBean({FastJsonHttpMessageConverter.class})//3
        public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
            FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

            FastJsonConfig fastJsonConfig = new FastJsonConfig();//4
            fastJsonConfig.setSerializerFeatures(
                    SerializerFeature.PrettyFormat,
                    //SerializerFeature.WriteClassName,
                    SerializerFeature.WriteMapNullValue
            );
            ValueFilter valueFilter = new ValueFilter() {//5
                //o 是class
                //s 是key值
                //o1 是value值
                public Object process(Object o, String s, Object o1) {
                    if (null == o1){
                        o1 = "";
                    }
                    return o1;
                }
            };
            fastJsonConfig.setSerializeFilters(valueFilter);

            //处理中文乱码问题 已经在配置文件中强制转换utf-8.
            //List<MediaType> fastMediaTypes = new ArrayList<>();
            //fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            //converter.setSupportedMediaTypes(fastMediaTypes);

            converter.setFastJsonConfig(fastJsonConfig);

            return converter;
        }
    }
}
