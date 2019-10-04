#!/usr/bin/env bash
#title      :start_rabbit.sh
#author     :O.Kostera
#notes      :unix system targeted, docker installed
#decription :Bash script that starts rabbitmq broker

RABBITMQ_DEFAULT_USER="rabbitmq"
RABBITMQ_DEFAULT_PASS="rabbitmq"
RABBIT_HOSTNAME="rabbitmq"
RABBIT_CONTAINER_NAME="rabbitmq-0"

docker build --tag=rabbitmq:flink .

docker run --rm \
  --hostname $RABBIT_HOSTNAME \
  --name $RABBIT_CONTAINER_NAME \
  -e RABBITMQ_DEFAULT_USER=$RABBITMQ_DEFAULT_USER \
  -e RABBITMQ_DEFAULT_PASS=$RABBITMQ_DEFAULT_PASS \
  -p 15672:15672 \
  -p 5672:5672 \
  rabbitmq:flink
