#!/usr/bin/env python

import pika
import os
import sys
import getopt

rabbit_login = os.getenv("RABBIT_USERNAME", 'rabbitmq')
rabbit_pass = os.getenv("RABBIT_PASSWORD", 'rabbitmq')
rabbit_host = os.getenv("RABBIT_HOST", 'localhost')
rabbit_port = os.getenv("RABBIT_PORT", 5672)
rabbit_queue = os.getenv("RABBIT_QUEUE", 'flink_output')

credentials = pika.PlainCredentials(rabbit_login, rabbit_pass)
connection = pika.BlockingConnection(pika.ConnectionParameters(
    rabbit_host, rabbit_port, '/', credentials))
channel = connection.channel()

messageNo = 0
sys.stdout.write("\rMessages processed count: %d" % messageNo)
sys.stdout.flush()


def callback(ch, method, properties, body):
    global messageNo
    messageNo += 1
    sys.stdout.write("\rMessages processed count: %d" % messageNo)
    sys.stdout.flush()


channel.basic_consume(callback, queue=rabbit_queue, no_ack=True)
channel.start_consuming()
connection.close()
