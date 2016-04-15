package messages;

public class MessagePlaceBet implements Message{
	
	private static final long serialVersionUID = -8423988321346226489L;
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