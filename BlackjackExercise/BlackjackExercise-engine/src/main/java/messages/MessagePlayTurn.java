package messages;

public class MessagePlayTurn implements Message{
	
	private String message;

	public MessagePlayTurn() {
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
		return "MessagePlayTurn [message=" + message + "]";
	}

}