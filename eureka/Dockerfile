FROM openjdk:8

EXPOSE 8761
ENV ENCRYPT_KEY="8N6pVhqW1Fpx8g7o"

ARG jar=eureka-0.0.1-SNAPSHOT.jar
COPY target/$jar /$jar

CMD java -Xms512M -Xmx1024M -jar eureka-0.0.1-SNAPSHOT.jar
