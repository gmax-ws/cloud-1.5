# **Service Registry (Discovery)** #

* The implementation is based on **Netflix Eureka** Service Discovery.
* Sensitive informations like passwords are symmetrical encrypted. 
* The shared key is keep inside **application.yaml** and **MUST** be provided in a command line parameter **-Dencrypt.key=** or in an environment variable **ENCRYPT_KEY**