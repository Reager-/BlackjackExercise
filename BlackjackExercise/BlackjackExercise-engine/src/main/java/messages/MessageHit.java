package messages;

public class MessageHit implements Message{

	private static final long serialVersionUID = 7451313751477655883L;
	private String message;

	public MessageHit() {
		super();
		this.message = "Hit";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageHit [message=" + message + "]";
	}

}