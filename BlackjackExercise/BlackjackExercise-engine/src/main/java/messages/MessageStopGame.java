package messages;

public class MessageStopGame implements Message{

	private static final long serialVersionUID = 6599368730123824666L;
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