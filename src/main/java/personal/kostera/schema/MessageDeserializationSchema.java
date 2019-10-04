package personal.kostera.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import personal.kostera.model.Message;

public class MessageDeserializationSchema implements DeserializationSchema<Message> {

    private final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, Object> getMapFromJsonNode(JsonNode jsonNode) {
        Map<String, Object> resultMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> field = iterator.next();
            resultMap.put(field.getKey(), field.getValue());
        }
        return resultMap;
    }

    @Override
    public Message deserialize(byte[] message) {
        try {
            final ObjectNode messageJson = this.mapper.readValue(message, ObjectNode.class);
            final String messageId = messageJson.get("messageId").textValue();
            final String status = messageJson.get("status").textValue();
            final JsonNode properties = messageJson.get("properties");
            final Map<String, Object> messageProperties = getMapFromJsonNode(properties);
            return new Message(messageId, status, messageProperties);
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(Message nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Message> getProducedType() {
        return TypeExtractor.getForClass(Message.class);
    }
}
