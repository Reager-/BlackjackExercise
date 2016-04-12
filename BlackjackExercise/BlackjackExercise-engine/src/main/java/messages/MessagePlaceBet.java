package messages;

public class MessagePlaceBet implements Message{
	
	private String message;

	public MessagePlaceBet() {
		super();
		this.message = "PlaceBet";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessagePlaceBet [message=" + message + "]";
	}

}