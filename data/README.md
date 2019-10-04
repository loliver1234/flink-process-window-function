## Prequisites
* Python 2.7.x
* Pip
* pipenv (https://docs.python-guide.org/dev/virtualenvs/#installing-pipenv)

## Running script with pipenv
In current directory:

Install requirements from Pipfile:
```
pipenv install
```
Activate Pipenv shell:
```
pipenv shell
```
Execute your scripts:
```
python consume_messages.py
python send_messages.py -h
```

### Scripts env variables
- RABBIT_USERNAME    - rabbit username - "rabbitmq" as default
- RABBIT_PASSWORD    - rabbit password - "rabbitmq" as default
- RABBIT_HOST        - rabbit hostname - "localhost" as default
- RABBIT_PORT        - rabbit port number - "5672" as default
- RABBIT_EXCHANGE    - rabbit exchange to send message to - "flink_ex" as default
- RABBIT_ROUTING_KEY - rabbit routing key to send messages with -  "pre_processing" as default
- RABBIT_QUEUE       - rabbit queue to consume messages from -"flink_output" as default