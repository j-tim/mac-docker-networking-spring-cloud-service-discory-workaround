package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
@RestController
@Slf4j
public class RandomWordServiceApplication {

    private List<String> wordList;

    public RandomWordServiceApplication() {
        wordList = Arrays.asList("Robot", "Bean", "Flux", "Banana", "Mono", "Dust", "Orange", "Code", "Lion");
    }

    @GetMapping("/")
    public String home() {
        return "Spring is here!";
    }

    @GetMapping("/random")
    public String generateRandomWord() {
        Random random = new Random();
        String randomWord = wordList.get(random.nextInt(wordList.size()));

        log.info("Created random word: {}", randomWord);

        return randomWord;
    }

    public static void main(String[] args) {
        SpringApplication.run(RandomWordServiceApplication.class, args);
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @GetMapping("/discovery-client-services")
    public Map<String, List<String>> showEurekaClientServiceDetails() {
        List<String> services = discoveryClient.getServices();

        Map<String, List<String>> mapping = new HashMap<>();

        for (String serviceName : services) {
            List<ServiceInstance> instancesByServiceName = discoveryClient.getInstances(serviceName);

            List<String> serviceInstanceUris = new ArrayList<>();

            for (ServiceInstance serviceInstance : instancesByServiceName) {
                String instanceUri = serviceInstance.getUri().toString();
                serviceInstanceUris.add(instanceUri);
            }

            mapping.put(serviceName, serviceInstanceUris);
        }
        return mapping;
    }
}