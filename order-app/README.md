# **Order Application** #

This a potential web application (client) designed to consume secured services via **Zuul** gateway.
This application has been designed to validate the microservices architecture.
Also server side internationalization (**i18n**) options are implemented as a **POC**.
The application is able to test the following issues:

* Obtain a **JWT** access token from Authentication and Authorization endpoint.
* Obtain a new **JWT** token (refresh token in case of existing token is expired.
* Call **REST APIs** services which implement a standard set of **CRUD** operations.

This tool is also useful because calling rest services endpoints we are able to monitoring those endpoints and show the results from a **Turbine Dashboard**.