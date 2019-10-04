#!/usr/bin/env bash
#title      :stop_rabbit.sh
#author     :O.Kostera
#notes      :unix system targeted, docker installed
#decription :Bash script that stops rabbitmq broker

RABBIT_CONTAINER_NAME="rabbitmq-0"

docker stop "$(docker ps | grep $RABBIT_CONTAINER_NAME | cut -f1 -d' ')"