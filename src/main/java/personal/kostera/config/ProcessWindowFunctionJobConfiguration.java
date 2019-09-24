package personal.kostera.config;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;

public class ProcessWindowFunctionJobConfiguration {

    private final static String RABBIT_HOST_PROPERTY_NAME = "rabbit.host";
    private final static String RABBIT_PORT_PROPERTY_NAME = "rabbit.port";
    private final static String RABBIT_USER_PROPERTY_NAME = "rabbit.username";
    private final static String RABBIT_PASS_PROPERTY_NAME = "rabbit.password";
    private final static String RABBIT_VHOST_PROPERTY_NAME = "rabbit.vhost";

    private final static String RABBIT_SINK_EXCHANGE_PROPERTY_NAME = "rabbit.sink.exchange";
    private final static String RABBIT_SINK_ROUTING_KEY_PROPERTY_NAME = "rabbit.sink.routingKey";
    private final static String RABBIT_SOURCE_QUEUE_NAME_PROPERTY_NAME = "rabbit.source.queueName";

    private final static String RABBIT_PREFETCH_COUNT = "rabbit.prefetch.count";

    private final static String CHECKPOINT_ENABLED = "checkpoint.enabled";
    private final static String CHECKPOINT_INTERVAL = "checkpoint.interval";
    private final static String CHECKPOINT_MODE = "checkpoint.mode";
    private final static String CHECKPOINT_PAUSE = "checkpoint.pause";
    private final static String CHECKPOINT_TIMEOUT = "checkpoint.timeout";
    private final static String CHECKPOINT_CONCURRENT_MAX = "checkpoint.concurrent.max";
    private final static String CHECKPOINT_DATA_URI = "checkpoint.data.uri";
    private final static String CHECKPOINT_ROCKSB_OPTIONS = "checkpoint.rocksdb.options";

    private String rabbitHost;
    private int rabbitPort;
    private String rabbitUserName;
    private String rabbitPassword;
    private String rabbitVirtualHost;

    private String rabbitSinkExchange;
    private String rabbitSinkRoutingKey;
    private String rabbitSourceQueue;
    private int rabbitPrefetchCount;

    private boolean checkpointEnabled;
    private int checkpointInterval;
    private String checkpointMode;
    private int checkpointPause;
    private int checkpointTimeout;
    private int checkpointConcurrentMax;
    private String checkpointDataUri;
    private PredefinedOptions rocksDbPredefinedOptions;

    public ProcessWindowFunctionJobConfiguration(final Configuration properties) {
        this.rabbitHost = properties.getString(RABBIT_HOST_PROPERTY_NAME, "localhost");
        this.rabbitPort = properties.getInteger(RABBIT_PORT_PROPERTY_NAME, 5682);
        this.rabbitUserName = properties.getString(RABBIT_USER_PROPERTY_NAME, "rabbitmq");
        this.rabbitPassword = properties.getString(RABBIT_PASS_PROPERTY_NAME, "rabbitmq");
        this.rabbitVirtualHost = properties.getString(RABBIT_VHOST_PROPERTY_NAME, "/");

        this.rabbitSinkExchange = properties.getString(RABBIT_SINK_EXCHANGE_PROPERTY_NAME, "flink_ex");
        this.rabbitSinkRoutingKey = properties.getString(RABBIT_SINK_ROUTING_KEY_PROPERTY_NAME, "processed_messages");
        this.rabbitSourceQueue = properties.getString(RABBIT_SOURCE_QUEUE_NAME_PROPERTY_NAME, "flink_input");
        this.rabbitPrefetchCount = properties.getInteger(RABBIT_PREFETCH_COUNT, 65535);

        this.checkpointEnabled = properties.getBoolean(CHECKPOINT_ENABLED, false);
        this.checkpointInterval = properties.getInteger(CHECKPOINT_INTERVAL, 1000);
        this.checkpointMode = properties.getString(CHECKPOINT_MODE, "EXACTLY_ONCE");
        this.checkpointPause = properties.getInteger(CHECKPOINT_PAUSE, 1000);
        this.checkpointTimeout = properties.getInteger(CHECKPOINT_TIMEOUT, 10000);
        this.checkpointConcurrentMax = properties.getInteger(CHECKPOINT_CONCURRENT_MAX, 1);
        this.checkpointDataUri = properties.getString(CHECKPOINT_DATA_URI, "file:///tmp/flink/checkpoints");
        this.rocksDbPredefinedOptions =
            PredefinedOptions.valueOf(properties.getString(CHECKPOINT_ROCKSB_OPTIONS, "DEFAULT"));
    }

    public String getRabbitHost() {
        return rabbitHost;
    }

    public int getRabbitPort() {
        return rabbitPort;
    }

    public String getRabbitUserName() {
        return rabbitUserName;
    }

    public String getRabbitPassword() {
        return rabbitPassword;
    }

    public String getRabbitVirtualHost() {
        return rabbitVirtualHost;
    }

    public String getRabbitSinkExchange() {
        return rabbitSinkExchange;
    }

    public String getRabbitSinkRoutingKey() {
        return rabbitSinkRoutingKey;
    }

    public String getRabbitSourceQueue() {
        return rabbitSourceQueue;
    }

    public int getRabbitPrefetchCount() {
        return rabbitPrefetchCount;
    }

    public Boolean getCheckpointEnabled() {
        return checkpointEnabled;
    }

    public int getCheckpointInterval() {
        return checkpointInterval;
    }

    public String getCheckpointMode() {
        return checkpointMode;
    }

    public int getCheckpointPause() {
        return checkpointPause;
    }

    public int getCheckpointTimeout() {
        return checkpointTimeout;
    }

    public int getCheckpointConcurrentMax() {
        return checkpointConcurrentMax;
    }

    public String getCheckpointDataUri() {
        return checkpointDataUri;
    }

    public PredefinedOptions getRocksDbPredefinedOptions() {
        return rocksDbPredefinedOptions;
    }
}
