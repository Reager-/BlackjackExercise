package messages;

public class MessageStartRound implements Message {
	
	private static final long serialVersionUID = 6406890055066057014L;
	private String message;

	public MessageStartRound() {
		super();
		this.message = "StartRound";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MessageStartRound [message=" + message + "]";
	}
	
}