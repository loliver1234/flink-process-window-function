package personal.kostera.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.List;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.MultipleIdsMessageAcknowledgingSourceBase;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

public class CustomRMQSource<OUT> extends MultipleIdsMessageAcknowledgingSourceBase<OUT, String, Long>
    implements ResultTypeQueryable<OUT> {

    private static final long serialVersionUID = 1L;
    protected final String queueName;
    private final RMQConnectionConfig rmqConnectionConfig;
    private final boolean usesCorrelationId;
    private final int prefetchCount;
    protected DeserializationSchema<OUT> schema;
    protected transient Connection connection;
    protected transient Channel channel;
    protected transient QueueingConsumer consumer;
    protected transient boolean autoAck;

    private transient volatile boolean running;

    public CustomRMQSource(RMQConnectionConfig rmqConnectionConfig, String queueName, boolean usesCorrelationId,
                           DeserializationSchema<OUT> deserializationSchema, final int prefetchCount) {
        super(String.class);
        this.rmqConnectionConfig = rmqConnectionConfig;
        this.queueName = queueName;
        this.usesCorrelationId = usesCorrelationId;
        this.schema = deserializationSchema;
        this.prefetchCount = prefetchCount;
    }

    protected ConnectionFactory setupConnectionFactory() throws Exception {
        return rmqConnectionConfig.getConnectionFactory();
    }

    @Override
    public void open(Configuration config) throws Exception {
        super.open(config);
        ConnectionFactory factory = setupConnectionFactory();
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            if (channel == null) {
                throw new RuntimeException("None of RabbitMQ channels are available");
            }
            // Added prefetch
            channel.basicQos(this.prefetchCount, true);
            consumer = new QueueingConsumer(channel);
            RuntimeContext runtimeContext = getRuntimeContext();
            if (runtimeContext instanceof StreamingRuntimeContext && ((StreamingRuntimeContext) runtimeContext)
                .isCheckpointingEnabled()) {
                autoAck = false;
                // enables transaction mode
                channel.txSelect();
            } else {
                autoAck = true;
            }

            channel.basicConsume(queueName, autoAck, consumer);

        } catch (IOException e) {
            throw new RuntimeException(
                "Cannot create RMQ connection with " + queueName + " at " + rmqConnectionConfig.getHost(), e);
        }
        running = true;
    }

    @Override
    public void close() throws Exception {
        super.close();
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Error while closing RMQ connection with " + queueName + " at " + rmqConnectionConfig.getHost(), e);
        }
    }

    @Override
    public void run(SourceContext<OUT> ctx) throws Exception {
        while (running) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            synchronized (ctx.getCheckpointLock()) {

                OUT result = schema.deserialize(delivery.getBody());

                if (schema.isEndOfStream(result)) {
                    break;
                }

                if (!autoAck) {
                    final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                    if (usesCorrelationId) {
                        final String correlationId = delivery.getProperties().getCorrelationId();
                        //						Changed:
                        //						Preconditions.checkNotNull(correlationId, "RabbitMQ source was instantiated " +
                        //								"with usesCorrelationId set to true but a message was received with " +
                        //								"correlation id set to null!");
                        //						Skipping messages if correlationID is null
                        if (correlationId == null) {
                            sessionIds.add(deliveryTag);
                            continue;
                        }
                        if (!addId(correlationId)) {
                            // we have already processed this message
                            continue;
                        }
                    }
                    sessionIds.add(deliveryTag);
                }

                ctx.collect(result);
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    @Override
    protected void acknowledgeSessionIDs(List<Long> sessionIds) {
        try {
            for (long id : sessionIds) {
                channel.basicAck(id, false);
            }
            channel.txCommit();
        } catch (IOException e) {
            throw new RuntimeException("Messages could not be acknowledged during checkpoint creation.", e);
        }
    }

    @Override
    public TypeInformation<OUT> getProducedType() {
        return schema.getProducedType();
    }
}

