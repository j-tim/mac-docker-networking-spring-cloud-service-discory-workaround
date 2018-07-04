# Docker for Mac `docker01` bridge workaround - Running Spring Cloud Microservices in Docker / IntelliJ

## Introduction

While developing our Microservices we heavily using Docker for (local) development.
Some of our Microservices we run in Docker (including Spring Cloud Config Server and Spring Cloud Netflix Eureka)
but we run the Microservice under development from our favorite IDE IntelliJ.

Recently we moved from a Linux desktop to a shiny Macbook Pro :)
Everything was fine until we started some of our microservices in Docker and the Microservice under development in IntelliJ
but the service was not able to load it's configuration from the config server and was not able to communicate with
the other microservice(s) running in Docker.

What is the cause of this issue?   

From the Docker website: [https://docs.docker.com/docker-for-mac/networking/#there-is-no-docker0-bridge-on-macos](https://docs.docker.com/docker-for-mac/networking/#there-is-no-docker0-bridge-on-macos)

> There is no docker0 bridge on macOS Because of the way networking is implemented in Docker for Mac, you cannot see a docker0 interface on the host. This interface is actually within the virtual machine.

Unlike running Docker on Linux, Docker for Mac doesn't support a `docker0` bridge what does this mean?
On Linux you can directly connect to the `internal docker network` IP addresses but because of the extra Docker network layer in Mac you can't... 

The root cause of the issue is a mix of Apple not willing to backport a fix that has been release in 10.12 and
Docker not willing add additional root access for Docker for mac. 

From the Docker website: [https://docs.docker.com/v17.12/docker-for-mac/networking/#a-view-into-implementation](https://docs.docker.com/v17.12/docker-for-mac/networking/#a-view-into-implementation)

> there are several problems. In particular, there is a bug in macOS that is only fixed in 10.12 and is not being backported as far as we can tell, which means that we could not support this in all supported macOS versions. In addition, this network setup would require root access which we are trying to avoid entirely in Docker for Mac (we currently have a very small root helper that we are trying to remove).

Result: we have to deal with this issue and work around the problem. 
There are several work around for the missing `docker0` bridge 
on Mac but this example project will focus on a workaround that plays well Spring Cloud Microservices and doesn't require
you to install additional software or run scripts to work around the problem.   

## The example project

* All Microservice are based on Spring Boot 2 / Spring Cloud Finchley
* `hello-world-service` is calls the `random-word-service`. The output for the user is `Hello: <A random word>`
* Microservices `hello-world-service`, `random-word-service` and `config-server` register at the `service-registry` (Spring Cloud Eureka)
* Microservices `hello-world-service` and `random-word-service` load their configuration from the `config-server`  

| Service             | Description                                 | Port | Running in  | Link  |
|---------------------|---------------------------------------------|------|-------------|-------|
| hello-world-service | Service with Rest API to say Hello          | 8080 | IntelliJ    | [http://localhost:8080/hello](http://localhost:8080/hello)  |
| random-word-service | Service with Rest API to create random word | 8081 | Docker      | [http://localhost:8081/random](http://localhost:8081/random)  |
| config-server       | Spring Cloud Config Server                  | 8888 | Docker      | [http://localhost:8888/hello-world-service/default](http://localhost:8888/hello-world-service/default)  |
| service-registry    | Spring Cloud Netflix Eureka                 | 8761 | Docker      | [http://localhost:8761](http://localhost:8761)  |

## How to build this project

Build all the Spring Boot / Cloud microservices

```bash
mvn clean package
```

Now build all the Docker images

```bash
docker-compose build
```

## How to run this project

```bash
docker-compose up -d
```

Wait until all services are started. 
This can some time because all services needs to register at Eureka and then resolve the config server.
Now all four services are running in Docker.

To show the missing `docker0` bridge in action stop `hello-world-service`

```bash
docker-compose stop hello-world-service
```

Now run the the Hello World Service from IntelliJ with the correct Spring Profile.
We introduced several profiles to explain each step towards a working solution 

For the Spring Profiles see: `bootstrap.yml` or the `hello-world-service`

| Spring Profile                                               | Description                                                                          | Working solution |
|--------------------------------------------------------------|--------------------------------------------------------------------------------------|------------------|
| default                                                      | Can't connect to Eureka                                                              | No               |
| docker-local-directly-connect-to-eureka                      | Can connect to Eureka but can't connect to config server                             | No               |
| docker-local-directly-connect-to-config-server               | Can connect to Eureka and config server but can't connect to Random word service     | No               |
| docker-local-static-ribbon-configuration                     | Can connect to `random-word-service` using static ribbon config                      | Yes              |
| docker-local-static-ribbon-configuration-multiple-instances  | Can connect to multiple instances of the `random-word-service` (static ribbon config | Yes              |

## IntelliJ Docker plugin

If you are new to Docker you can install this very nice IntelliJ Docker integration plugin!

[https://plugins.jetbrains.com/plugin/7724-docker-integration](Download docker integration plugin)

## `docker0` bridge related issues

* https://github.com/docker/for-mac/issues/171
* https://forums.docker.com/t/support-tap-interface-for-direct-container-access-incl-multi-host/17835/24 
