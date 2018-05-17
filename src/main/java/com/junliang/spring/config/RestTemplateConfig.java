package com.junliang.spring.config;


import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class RestTemplateConfig {

    @Bean("serverRest")
    public RestTemplate serverRest(){
        //http://www.jianshu.com/p/c9644755dd5e
        RestTemplate  restTemplate=	 new RestTemplate();
        restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
        return restTemplate;
    }

    @Bean("restTemplate")
    @ConditionalOnMissingBean({ RestOperations.class, RestTemplate.class })
    public RestTemplate restTemplate(){

        //http://rensanning.iteye.com/?page=3
        RestTemplate  restTemplate=	 new RestTemplate(simpleClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean({ClientHttpRequestFactory.class})
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        //try {
        //
        //    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        //
        //    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
        //            .loadTrustMaterial(null, acceptingTrustStrategy)
        //            .build();
        //
        //    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        //
        //    CloseableHttpClient httpClient = HttpClients.custom()
        //            .setSSLSocketFactory(csf)
        //            .build();
        //
        //    HttpComponentsClientHttpRequestFactory requestFactory =
        //            new HttpComponentsClientHttpRequestFactory();
        //
        //    requestFactory.setHttpClient(httpClient);
        //
        //    return  requestFactory;
        //
        //
        //} catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
        //
        //    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //    factory.setReadTimeout(15000);// ms
        //    factory.setConnectTimeout(15000);// ms
        //    return factory;
        //}

        try {
            return new HttpComponentsClientHttpRequestFactory(httpClient());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setReadTimeout(15000);// ms
            factory.setConnectTimeout(15000);// ms
            return factory;
        }
    }


    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    /**
     * 设置信任自签名证书
     *
     * @param keyStorePath      密钥库路径
     * @param keyStorepass      密钥库密码
     * @return
     */
    public static SSLContext custom(String keyStorePath, String keyStorepass){
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return sc;
    }

    @Bean
    public HttpClient httpClient() throws KeyManagementException, NoSuchAlgorithmException {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                //.register("https", SSLConnectionSocketFactory.getSocketFactory())
                //采用绕过验证的方式处理https请求
                .register("https", new SSLConnectionSocketFactory(createIgnoreVerifySSL()))
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(5);
        connectionManager.setDefaultMaxPerRoute(5);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(8000)
                .setConnectTimeout(8000)
                .setConnectionRequestTimeout(8000)
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }




}
