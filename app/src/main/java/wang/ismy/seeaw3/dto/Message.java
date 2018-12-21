package wang.ismy.seeaw3.dto;

import wang.ismy.seeaw3.common.Destination;

import java.io.IOException;


public class Message {


    private Destination destination;

    private String content;

    public Message(Message message) {
        this.content = message.getContent();
        this.destination = message.getDestination();
    }

    public Message(){

    }

    public void sendMessage() throws IOException {
        destination.sendMessage(this);
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "destination=" + destination +
                ", content='" + content + '\'' +
                '}';
    }
}
