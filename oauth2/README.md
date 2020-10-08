# **Authentication and Authorization Server** #

* This project is an Authentication and Authorization Server. 
* It is configured to issue **JWT** or **random** tokens as well. By default **JWT** tokens are issued.
* Sensitive informations like passwords are symmetrical encrypted. 
* The shared key is keep inside **application.yaml** and **MUST** be provided in a command line parameter **-Dencrypt.key=** or in an environment variable **ENCRYPT_KEY**