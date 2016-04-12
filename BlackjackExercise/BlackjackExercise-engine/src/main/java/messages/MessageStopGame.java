package messages;

public class MessageStopGame implements Message{
	
	private String message;

	public MessageStopGame() {
		super();
		this.message = "StopGame";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageStopGame [message=" + message + "]";
	}

}