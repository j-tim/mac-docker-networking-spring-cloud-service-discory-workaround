package com.example.demo.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

  @LoadBalanced
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
    return restTemplate;
  }

  private ClientHttpRequestFactory getClientHttpRequestFactory() {
    int timeoutMs = 1000;
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(timeoutMs);
    return clientHttpRequestFactory;
  }
}
