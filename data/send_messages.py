#!/usr/bin/env python

import pika
import json
import uuid
import os
import sys
import getopt

rabbit_login = os.getenv("RABBIT_USERNAME", 'rabbitmq')
rabbit_pass = os.getenv("RABBIT_PASSWORD", 'rabbitmq')
rabbit_host = os.getenv("RABBIT_HOST", 'localhost')
rabbit_port = os.getenv("RABBIT_PORT", 5672)
rabbit_exchange = os.getenv("RABBIT_EXCHANGE", 'flink_ex')
rabbit_routing_key = os.getenv("RABBIT_ROUTING_KEY", 'pre_processing')

credentials = pika.PlainCredentials(rabbit_login, rabbit_pass)


def main(argv):
    keys_num = None
    iterations = None
    help_note = 'send_messages.py -n <unique_keys_num> -i <number_of_messages_per_key>'
    try:
        opts, args = getopt.getopt(argv, "n:i:", [])
    except getopt.GetoptError:
        print help_note
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print help_note
            sys.exit()
        elif opt in ("-n"):
            keys_num = int(arg)
        elif opt in ("-i"):
            iterations = int(arg)

    if keys_num is None:
        print 'Specify <unique_keys_num> with -n option'
        sys.exit(2)
    elif iterations is None:
        print 'Specify <number_of_messages_per_key> with -i option'
        sys.exit(2)
    else:
        send_to_rabbit(keys_num, iterations)


def send_to_rabbit(keys_num, iterations):
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        rabbit_host, rabbit_port, '/', credentials))
    channel = connection.channel()
    for iteration in range(0, iterations):
        for id in range(0, keys_num):
            message = {
                "messageId": "key-%d" % id,
                "status": "COMPLETED",
                "properties": {
                    "prop.1": 1,
                    "prop.2": 2,
                    "iteration": iteration
                }
            }
            message_properties = pika.BasicProperties(
                correlation_id=str(uuid.uuid4()))
            channel.basic_publish(exchange=rabbit_exchange, routing_key=rabbit_routing_key,
                                  properties=message_properties, body=json.dumps(message))
    connection.close()


if __name__ == "__main__":
    main(sys.argv[1:])
