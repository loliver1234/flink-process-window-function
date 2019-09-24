package personal.kostera.model;

import java.util.Map;

public class Message {

    private String messageId;
    private String status;
    private Map<String, Object> messageProperties;

    public Message() {
    }

    public Message(final String messageId, final String status, final Map<String, Object> messageProperties) {
        this.messageId = messageId;
        this.status = status;
        this.messageProperties = messageProperties;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Map<String, Object> getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(final Map<String, Object> messageProperties) {
        this.messageProperties = messageProperties;
    }
}
