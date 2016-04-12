package messages;

public class MessageBetDone implements Message{

	private String message;

	public MessageBetDone() {
		super();
		this.message = "BetDone";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageBetDone [message=" + message + "]";
	}
	
}