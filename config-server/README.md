# **Configuration Server** #

Configuration Server is a server which allow to keep application settings centralized.
The implementation is based on **Spring Cloud Config** server with the following features:

* Encrypted sensitive properties for applications using a shared key. The shared key
  **MUST** be provided as a command line parameter -**Dencrypt.key=** or as an environment variable **ENCRYPT_KEY**
* For a better security an asymmetrical encryption should be used based on private/public pair of keys. 
* It use a **Bitbucket** remote repository to store application properties.
* It support **WebHooks** notifications via the **/monitor** end point. The config server **MUST** be visible outside on the network
from **Bitbucket**. If is not possible for different reasons including security you ca use the **ngrok** tool
which allow you to create a secured tunnel between **Bitbucket** repository and the machine on which
**Config server** is running. 

Example: 
```
#!bash

$ ngrok http 8888
```


The Bitbucket webhook will call the **/monitor** endpoint on **ngrok** server which will forward the request
to specified port (**8888**) on the local machine. For more details please go to [https://ngrok.com](Link URL)

* Another feature is a **Service Bus** which is used to send refresh events to services registered with bus as listeners.
The **Service Bus** require a transport system, a message broker like RabbitMQ, Kafka or Redis. We are using RabbitMQ for our POC.
For refresh setting an **/bus/refresh** endpoint can be accessed via **POST** and a parameter **path=**
The **path** parameter support wildcards and specify which resources are targeted by the message.
Example: **path=servivce1** or **path=serv\*** or **path=\***

## **Important:** ##
In order to use the encryption and decryption features you need the full-strength JCE installed in your JVM (it’s not there by default). You can download the "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files" from Oracle, and follow instructions for installation (essentially replace the 2 policy files in the JRE lib/security directory with the ones that you downloaded).