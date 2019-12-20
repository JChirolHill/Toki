package projects.chirolhill.juliette.finalprojectchirolhilljuliette.model;

public class TextMessage {
    private String message;
    private boolean fromMe;

    // need to know message and who message is from (user or Dialogflow agent)
    public TextMessage(String message, boolean fromMe) {
        this.message = message;
        this.fromMe = fromMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }
}
