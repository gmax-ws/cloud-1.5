FROM openjdk:8

EXPOSE 8887
ENV ENCRYPT_KEY="8N6pVhqW1Fpx8g7o"

ARG jar=oauth2-0.0.1-SNAPSHOT.jar
COPY target/$jar /$jar

CMD java -Xms512M -Xmx1024M -jar oauth2-0.0.1-SNAPSHOT.jar
