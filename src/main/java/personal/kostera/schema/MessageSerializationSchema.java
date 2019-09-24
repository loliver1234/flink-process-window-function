package personal.kostera.schema;

import java.io.IOException;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import personal.kostera.model.Message;

public class MessageSerializationSchema implements SerializationSchema<Message> {

    private static final long serialVersionUID = 1L;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Message element) {
        byte[] out = new byte[0];
        try {
            out = mapper.writeValueAsString(element).getBytes();
        } catch (IOException e) {
            // Do nothing
        }
        return out;
    }
}