package personal.kostera.functions;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import personal.kostera.model.Message;

public class CustomProcessWindowFunction extends ProcessWindowFunction<Message, Message, String, TimeWindow> {

    @Override
    public void process(String key, Context context, Iterable<Message> elements, Collector<Message> out) {
        // Just emit collected messages, nothing else
        elements.forEach(out::collect);
    }
}
