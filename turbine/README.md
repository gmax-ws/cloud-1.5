# **Turbine Dashboard** #

* Collect **Hystrix** streams from configured services and provide a centralized dashboard.
* **Turbine Dashboard** is connected to **Cloud Service Bus** via a **AMQP** message broker (**RabbitMQ**)
* Configurations are keep centralized by a Config Server and the password used to access the Config Server is encrypted using a shared key.
* It is possible to use an asymmetrical encryption as well depending on how the Config Server is configured to encrypt sensitive data.
* The shared key is keep inside **bootstrap.yaml** for **POC** but in a real production environment **MUST** be provided by a command line parameter
 **-Dencrypt.key=** or in an environment variable **ENCRYPT_KEY**