package messages;

public class MessageHit implements Message{

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