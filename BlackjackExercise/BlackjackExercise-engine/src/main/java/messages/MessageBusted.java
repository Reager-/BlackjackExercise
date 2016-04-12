package messages;

public class MessageBusted implements Message{

	private String message;

	public MessageBusted() {
		super();
		this.message = "Busted";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageBusted [message=" + message + "]";
	}
	
}