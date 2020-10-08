# **REST API - Microservice Template** #

This is a microservice template having the following capabilities:

* Protected endpoints access using **JWT** and **Spring Security**.
* Auto registration with **Eureka** Server.
* Monitoring service endpoints with **Hystrix** commands. 
* It's connected to Cloud Service Bus via a AMQP message broker (RabbitMQ)
* Enable **CORS** (optional) access. Do not enable **CORS** if **CORS** is already enabled in **Zuul** and the service is accessed via Zuul gateway.
* Configurations are keep centralized by a Config Server and the password used to access the Config Server is encrypted using a shared key.
* It is possible to use an asymmetrical encryption as well depending on how the Config Server is configured to encrypt sensitive data.
* The shared key is keep inside **bootstrap.yaml** for **POC** but in a real production environment **MUST** be provided by a command line parameter
 **-Dencrypt.key=** or in an environment variable **ENCRYPT_KEY**