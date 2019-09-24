package personal.kostera;

import java.io.IOException;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import personal.kostera.config.ProcessWindowFunctionJobConfiguration;
import personal.kostera.functions.CustomProcessWindowFunction;
import personal.kostera.model.Message;
import personal.kostera.rabbit.CustomRMQSinkPublishOptions;
import personal.kostera.rabbit.CustomRMQSource;
import personal.kostera.schema.MessageDeserializationSchema;
import personal.kostera.schema.MessageSerializationSchema;

public class ProcessWindowFunctionJob {

    private static final String CONFIG_PROPERTY_FILE_NAME = "application.properties";
    private static final String JOB_NAME = "Process Window Function";
    private static final String MESSAGE_INPUT_STREAM_OPERATOR_ID = "messageInputStreamOperator";
    private static final String COMPLETED_MESSAGES_FUNCTION_OPERATOR_ID = "completedMessagesFunctionOperator";
    private static final String PROCESS_WINDOW_FUNCTION_OPERATOR_ID = "processWindowFunctionOperator";

    public static void main(String[] args) throws Exception {

        final ParameterTool properties = ParameterTool.fromPropertiesFile(
            ProcessWindowFunctionJob.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTY_FILE_NAME));
        final ParameterTool parameterArgs = ParameterTool.fromArgs(args);
        final Configuration configuration = properties.mergeWith(parameterArgs).getConfiguration();
        final ProcessWindowFunctionJobConfiguration config = new ProcessWindowFunctionJobConfiguration(configuration);

        final RMQConnectionConfig connectionConfig =
            new RMQConnectionConfig.Builder().setVirtualHost(config.getRabbitVirtualHost())
                .setHost(config.getRabbitHost()).setUserName(config.getRabbitUserName())
                .setPassword(config.getRabbitPassword()).setPort(config.getRabbitPort()).build();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        if (config.getCheckpointEnabled()) {
            setCheckpoints(env, config);
        }

        final DataStream<Message> stream = env.addSource(
            new CustomRMQSource<>(connectionConfig, config.getRabbitSourceQueue(), true,
                                  new MessageDeserializationSchema(), config.getRabbitPrefetchCount()))
            .setParallelism(1).uid(MESSAGE_INPUT_STREAM_OPERATOR_ID).name("Message source stream");

        final KeyedStream<Message, String> keyedStream = stream
            .filter((FilterFunction<Message>) msg -> (msg != null && "COMPLETED".equalsIgnoreCase(msg.getStatus())))
            .uid(COMPLETED_MESSAGES_FUNCTION_OPERATOR_ID).name("Completed messages filter")
            .keyBy(Message::getMessageId);

        final SingleOutputStreamOperator<Message> processWindowFunctionStream =
            keyedStream.window(ProcessingTimeSessionWindows.withGap(Time.milliseconds(100)))
                .process(new CustomProcessWindowFunction()).uid(PROCESS_WINDOW_FUNCTION_OPERATOR_ID)
                .name("Process window function");

        processWindowFunctionStream.addSink(new RMQSink<>(connectionConfig, new MessageSerializationSchema(),
                                                          new CustomRMQSinkPublishOptions<>(
                                                              config.getRabbitSinkExchange(),
                                                              config.getRabbitSinkRoutingKey()))).name("Messages sink");

        env.execute(JOB_NAME);
    }

    private static void setCheckpoints(StreamExecutionEnvironment env, ProcessWindowFunctionJobConfiguration config)
        throws IOException {
        RocksDBStateBackend backend = new RocksDBStateBackend(config.getCheckpointDataUri(), true);
        backend.setPredefinedOptions(config.getRocksDbPredefinedOptions());
        env.setStateBackend(backend);
        env.enableCheckpointing(config.getCheckpointInterval());
        final CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointingMode(CheckpointingMode.valueOf(config.getCheckpointMode()));
        checkpointConfig.setMinPauseBetweenCheckpoints(config.getCheckpointPause());
        checkpointConfig.setCheckpointTimeout(config.getCheckpointTimeout());
        checkpointConfig.setMaxConcurrentCheckpoints(config.getCheckpointConcurrentMax());
        checkpointConfig
            .enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }
}
