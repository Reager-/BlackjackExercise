package messages;

public class MessageLastPlayerPlayed implements Message{

	private static final long serialVersionUID = -3184683592297140195L;
	
	private String message;

	public MessageLastPlayerPlayed() {
		super();
		this.message = "PlayTurn";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageLastPlayerPlayed [message=" + message + "]";
	}
	
}