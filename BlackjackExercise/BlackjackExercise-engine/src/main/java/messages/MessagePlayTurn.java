package messages;

public class MessagePlayTurn implements Message{
	
	private static final long serialVersionUID = 8483570862054254741L;
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