package personal.kostera.rabbit;

import com.rabbitmq.client.AMQP;
import java.util.UUID;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSinkPublishOptions;

public class CustomRMQSinkPublishOptions<IN> implements RMQSinkPublishOptions<IN> {

    private final String exchangeName;
    private final String routingKey;

    public CustomRMQSinkPublishOptions(String exchangeName, String routingKey) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    @Override
    public String computeRoutingKey(IN a) {
        return routingKey;
    }

    @Override
    public AMQP.BasicProperties computeProperties(IN a) {
        return new AMQP.BasicProperties.Builder().correlationId(UUID.randomUUID().toString()).deliveryMode(2).build();
    }

    @Override
    public String computeExchange(IN a) {
        return exchangeName;
    }
}
