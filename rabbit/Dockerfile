FROM rabbitmq:3-management

ADD rabbitmq.conf /etc/rabbitmq/
ADD definitions.json /etc/rabbitmq/
ADD init.sh /init.sh

CMD ["/init.sh"]