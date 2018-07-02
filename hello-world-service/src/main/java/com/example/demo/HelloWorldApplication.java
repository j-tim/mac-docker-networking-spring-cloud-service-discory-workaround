package com.example.demo;

import com.netflix.client.config.IClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
//@EnableDiscoveryClient // This is not required anymore!
public class HelloWorldApplication {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SpringClientFactory factory;

	@GetMapping("/")
	public String home() {
		return "Spring is here!";
	}

	@GetMapping("/hello")
	public String sayHelloWorld() {
		Map<String, ?> map = new HashMap<>();
		String randomWord = restTemplate.getForObject("http://random-word-service/random", String.class, map);
		return String.format("Hello Word: %s", randomWord);
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

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