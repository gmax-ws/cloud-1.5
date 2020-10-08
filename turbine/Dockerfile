FROM openjdk:8

EXPOSE 8844
ENV ENCRYPT_KEY="8N6pVhqW1Fpx8g7o"

ARG jar=turbine-0.0.1-SNAPSHOT.jar
COPY target/$jar /$jar

CMD java -Xms512M -Xmx1024M -jar turbine-0.0.1-SNAPSHOT.jar
