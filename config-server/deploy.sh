#!/bin/bash

IMAGE_NAME=config
CONTAINER_NAME=config
EXPOSED_PORT=8888
DETACH=false
DOCKER=false

# build jar
mvn clean install

if $DOCKER; then
  # build Docker image
  docker rmi $IMAGE_NAME:1.0
  docker build --tag $IMAGE_NAME:1.0 .

  # deploy Docker container
  docker rm --force $CONTAINER_NAME

  if $DETACH; then
    docker run --publish $EXPOSED_PORT:$EXPOSED_PORT --detach --name $CONTAINER_NAME $IMAGE_NAME:1.0
  else
    docker run --publish $EXPOSED_PORT:$EXPOSED_PORT --name $CONTAINER_NAME $IMAGE_NAME:1.0
  fi
else
  export ENCRYPT_KEY="8N6pVhqW1Fpx8g7o"
  eval "java -jar target/config-server-0.0.1-SNAPSHOT.jar"
fi
