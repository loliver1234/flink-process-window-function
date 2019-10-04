#!/bin/bash

#Enabled job control
set -m

# Start server in bg
nohup rabbitmq-server "$@" &

# Wait for rabbitmq server
rabbitmqctl wait /var/lib/rabbitmq/mnesia/rabbit@rabbitmq.pid

# Create Default Rabbitmq user
rabbitmqctl add_user "$RABBITMQ_DEFAULT_USER" "$RABBITMQ_DEFAULT_PASS" 2>/dev/null ; \
rabbitmqctl set_user_tags "$RABBITMQ_DEFAULT_USER" administrator ; \
rabbitmqctl set_permissions -p / "$RABBITMQ_DEFAULT_USER"  ".*" ".*" ".*" ;

# move rabbitmq server to foreground
fg